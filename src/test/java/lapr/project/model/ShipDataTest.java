package lapr.project.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ShipDataTest {
    public ShipDataTest() {
    }

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testGetLatitude() {
        System.out.println("getLatitude()");

        ShipData ship = new ShipData(LocalDateTime.now(), 123.45, 321.54, 15.4, 45.0, 42.0, 'B');
        double expRes = 123.45;

        assertEquals(expRes, ship.getLatitude(), "Should be equal");
    }

    @Test
    public void testGetLongitude() {
        System.out.println("getLongitude()");

        ShipData ship = new ShipData(LocalDateTime.now(), 123.45, 321.54, 15.4, 45.0, 42.0, 'B');
        double expRes = 321.54;

        assertEquals(expRes, ship.getLongitude(), "Should be equal");
    }

    @Test
    public void testGetSog() {
        System.out.println("getSog()");

        ShipData ship = new ShipData(LocalDateTime.now(), 123.45, 321.54, 15.4, 45.0, 42.0, 'B');
        double expRes = 15.4;

        assertEquals(expRes, ship.getSog(), "Should be equal");
    }

    @Test
    public void testGetCog() {
        System.out.println("getCog()");

        ShipData ship = new ShipData(LocalDateTime.now(), 123.45, 321.54, 15.4, 45.0, 42.0, 'B');
        double expRes = 45.0;

        assertEquals(expRes, ship.getCog(), "Should be equal");
    }

    @Test
    public void testPrintData() {
        System.out.println("printData()");

        ShipData ship = new ShipData(LocalDateTime.now(), 123.45, 321.54, 15.4, 45.0, 42.0, 'B');
        String expRes = "Date: " + ship.getDateTime() +
                ", Latitude: " + ship.getLatitude() +
                ", Longitude: " + ship.getLongitude() +
                ", SOG: " + ship.getSog() +
                ", COG: " + ship.getCog() +
                ", Heading: " + ship.getHeading() +
                ", Transceiver: " + ship.getTransceiver();

        assertEquals(expRes, ship.toString());
    }
}
