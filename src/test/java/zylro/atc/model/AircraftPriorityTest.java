package zylro.atc.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * Unit testing the priority of air crafts via compareTo
 *
 * @author wot
 */
public class AircraftPriorityTest {

    private Aircraft ac1;
    private Aircraft ac2;

    @Before
    public void initializeAircrafts() {
        ac1 = new Aircraft();
        ac2 = new Aircraft();
    }

    /**
     * 4. Large Aircrafts of a given type have priority over Small Aircrafts of
     * the same type
     */
    @Test
    public void verifySizePriority() {
        ac1.setType(Aircraft.Type.VIP);
        ac1.setSize(Aircraft.Size.Small);
        ac2.setType(Aircraft.Type.VIP);
        ac2.setSize(Aircraft.Size.Large);

        //large crafts have higher priority
        assertTrue(ac1.compareTo(ac2) < 0);
        assertTrue(ac2.compareTo(ac1) > 0);

        //same size, equals
        ac1.setSize(Aircraft.Size.Large);
        assertTrue(ac2.compareTo(ac1) == 0);

        ac1.setSize(Aircraft.Size.Small);
        ac2.setSize(Aircraft.Size.Small);
        assertTrue(ac2.compareTo(ac1) == 0);

        ac1.setSize(Aircraft.Size.Large);
        assertTrue(ac1.compareTo(ac2) > 0);

        //type has higher precendence
        ac2.setType(Aircraft.Type.Emergency);
        assertTrue(ac1.compareTo(ac2) < 0);
    }

    /**
     * 1. VIP highest priority except, Emergency (VIP 2nd) 2. Emergency higher
     * priority than all (Emergency 1st) 3. Passenger higher than Cargos (3rd
     * Passenger, 4th Cargo)
     */
    @Test
    public void verifyTypePriority() {
        ac1.setType(Aircraft.Type.Emergency);
        ac2.setType(Aircraft.Type.Emergency);

        assertTrue(ac1.compareTo(ac2) == 0);
        ac2.setType(Aircraft.Type.VIP);
        assertTrue(ac1.compareTo(ac2) > 0);
        ac2.setType(Aircraft.Type.Passenger);
        assertTrue(ac1.compareTo(ac2) > 0);
        ac2.setType(Aircraft.Type.Cargo);
        assertTrue(ac1.compareTo(ac2) > 0);

        ac1.setType(Aircraft.Type.VIP);
        assertTrue(ac1.compareTo(ac2) > 0);
        ac2.setType(Aircraft.Type.Passenger);
        assertTrue(ac1.compareTo(ac2) > 0);
        ac2.setType(Aircraft.Type.VIP);
        assertTrue(ac1.compareTo(ac2) == 0);
        ac2.setType(Aircraft.Type.Emergency);
        assertTrue(ac1.compareTo(ac2) < 0);

        ac1.setType(Aircraft.Type.Passenger);
        assertTrue(ac1.compareTo(ac2) < 0);
        ac2.setType(Aircraft.Type.VIP);
        assertTrue(ac1.compareTo(ac2) < 0);
        ac2.setType(Aircraft.Type.Passenger);
        assertTrue(ac1.compareTo(ac2) == 0);
        ac2.setType(Aircraft.Type.Cargo);
        assertTrue(ac1.compareTo(ac2) > 0);

        ac1.setType(Aircraft.Type.Cargo);
        assertTrue(ac1.compareTo(ac2) == 0);
        ac2.setType(Aircraft.Type.Passenger);
        assertTrue(ac1.compareTo(ac2) < 0);
        ac2.setType(Aircraft.Type.VIP);
        assertTrue(ac1.compareTo(ac2) < 0);
        ac2.setType(Aircraft.Type.Emergency);
        assertTrue(ac1.compareTo(ac2) < 0);
    }
}
