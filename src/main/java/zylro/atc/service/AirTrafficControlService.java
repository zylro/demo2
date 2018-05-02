package zylro.atc.service;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.UUID;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zylro.atc.ServiceException;
import zylro.atc.dataaccess.AirTrafficQueueDataAccess;
import zylro.atc.dataaccess.AircraftDataAccess;
import zylro.atc.model.AirTrafficPriorityQueue;
import zylro.atc.model.Aircraft;

/**
 *
 * @author wot
 */
public class AirTrafficControlService {

    private static final Logger LOG = LoggerFactory.getLogger(AirTrafficControlService.class);
    private final AircraftDataAccess aircraftData;
    private final AirTrafficQueueDataAccess queueData;
    private LinkedList<UUID> queue;

    public AirTrafficControlService(AircraftDataAccess aircraftData,
            AirTrafficQueueDataAccess queueData) {
        this.aircraftData = aircraftData;
        this.queueData = queueData;
        initializeQueue();
    }

    public LinkedList<UUID> getQueueState() {
        return queue;
    }

    public void enqueue(Aircraft incomingAircraft) {
        Aircraft aircraft = aircraftData.getAircraftById(incomingAircraft.getId());
        if (aircraft == null) {
            aircraftData.upsertAircraft(aircraft);
        }
        insertAircraftByPriority(aircraft);
        updateQueue();
    }

    public Aircraft dequeue() {
        UUID peekedAircraftId = queue.peekFirst();
        if (peekedAircraftId == null) {
            throw new ServiceException(HttpStatus.BAD_REQUEST_400, "Queue is empty");
        }
        UUID polledAircraftId = queue.poll();
        if (!peekedAircraftId.equals(polledAircraftId)) {
            LOG.error("Race condition occured, aircraft already dequeued: {} {}",
                    peekedAircraftId, polledAircraftId);
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR_500, "");
        }
        updateQueue();
        //not deleting aircraft to keep a track of aircrafts
        return aircraftData.getAircraftById(polledAircraftId);
    }

    private void insertAircraftByPriority(Aircraft aircraft) {
        int currentIndex = 0, insertIndex = -1;
        Iterator<UUID> iter = queue.descendingIterator();
        while (iter.hasNext()) {

            UUID currentAircraftId = iter.next();
            Aircraft currentAircraft = aircraftData.getAircraftById(currentAircraftId);
            if (aircraft.compareTo(currentAircraft) < 0) {
                insertIndex = currentIndex + 1;
                break;
            }
            currentIndex++;
        }
        if (insertIndex != -1) {
            queue.add(insertIndex, aircraft.getId());
        } else {
            queue.offer(aircraft.getId());
        }
    }

    //retrieves queue from mongodb
    private void initializeQueue() {
        this.queue = queueData.getPriorityQueue().getQueue();
    }

    //persists the queue into mongodb
    private void updateQueue() {
        AirTrafficPriorityQueue updatedQueue = new AirTrafficPriorityQueue();
        updatedQueue.setQueue(queue);
        queueData.upsertQueue(updatedQueue);
    }
}
