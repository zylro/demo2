package zylro.atc.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zylro.atc.ServiceException;
import zylro.atc.Utils;
import zylro.atc.dataaccess.AirTrafficQueueDataAccess;
import zylro.atc.dataaccess.AircraftDataAccess;
import zylro.atc.model.AirTrafficPriorityQueue;
import zylro.atc.model.Aircraft;

/**
 * Service layer to handle logic for enqueue and dequeue aircrafts
 *
 * @author wot
 */
public class AirTrafficControlService {

    private static final Logger LOG = LoggerFactory.getLogger(AirTrafficControlService.class);
    private final AircraftDataAccess aircraftData;
    private final AirTrafficQueueDataAccess queueData;
    private LinkedList<UUID> queue;
    private final Object lock = new Object();

    public AirTrafficControlService(AircraftDataAccess aircraftData,
            AirTrafficQueueDataAccess queueData) {
        this.aircraftData = aircraftData;
        this.queueData = queueData;
        initializeQueue();
    }

    public List<Aircraft> getQueueState() {
        List<Aircraft> queuedAircrafts = new ArrayList<>();
        queue.forEach((aircraftId) -> {
            queuedAircrafts.add(aircraftData.getAircraftById(aircraftId));
        });
        return queuedAircrafts;
    }

    public void enqueue(Aircraft incomingAircraft) {
        Aircraft aircraft = aircraftData.getAircraftById(incomingAircraft.getId());
        if (aircraft == null) {
            aircraftData.upsertAircraft(incomingAircraft);
        }
        synchronized (lock) {
            insertAircraftByPriority(incomingAircraft);
            updateQueue();
        }
    }

    public Aircraft dequeue() {
        UUID polledAircraftId;
        synchronized (lock) {
            UUID peekedAircraftId = queue.peekFirst();
            if (peekedAircraftId == null) {
                throw new ServiceException(HttpStatus.BAD_REQUEST_400, "Queue is empty");
            }
            polledAircraftId = queue.poll();
            if (!peekedAircraftId.equals(polledAircraftId)) {
                LOG.error("Race condition occured, aircraft already dequeued: {} {}",
                        peekedAircraftId, polledAircraftId);
                throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR_500, "");
            }
            updateQueue();
        }
        //not deleting aircraft to keep a track of aircrafts
        return aircraftData.getAircraftById(polledAircraftId);
    }

    private void insertAircraftByPriority(Aircraft aircraft) {
        int currentIndex = queue.size(), insertIndex = 0;
        Iterator<UUID> iter = queue.descendingIterator();
        while (iter.hasNext()) {
            UUID currentAircraftId = iter.next();
            Aircraft currentAircraft = aircraftData.getAircraftById(currentAircraftId);
            int priority = aircraft.compareTo(currentAircraft);
            if (priority <= 0) {
                insertIndex = currentIndex;
                break;
            }
            currentIndex--;
        }
        if (insertIndex != 0) {
            queue.add(insertIndex, aircraft.getId());
        } else {
            queue.offerFirst(aircraft.getId());
        }
    }

    //retrieves queue from mongodb
    private void initializeQueue() {
        AirTrafficPriorityQueue retrievedQueue = queueData.getPriorityQueue();
        LOG.debug("Existing queue found: " + Utils.toJson(retrievedQueue));
        this.queue = queueData.getPriorityQueue().getQueue();
    }

    //persists the queue into mongodb
    private void updateQueue() {
        AirTrafficPriorityQueue updatedQueue = new AirTrafficPriorityQueue();
        updatedQueue.setQueue(queue);
        queueData.upsertQueue(updatedQueue);
    }
}
