/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cycleroutesafety;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Andor Horvath
 */
public class PoiTest {
    
    public PoiTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of toString method, of class Poi.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Poi instance = new Poi();
        String expResult = "[" + instance.getPoiID()+ "; " + instance.getLat()+ "; " + instance.getLng() + "; " + instance.getMarkerID() + "]";
        String result = instance.toString();
        assertEquals(expResult, result);
    }

    /**
     * Test of setLat method, of class Poi.
     */
    @Test
    public void testSetLat() {
        System.out.println("setLat");
        double lat = 0.0;
        Poi instance = new Poi();
        instance.setLat(lat);
    }

    /**
     * Test of setLng method, of class Poi.
     */
    @Test
    public void testSetLng() {
        System.out.println("setLng");
        double lng = 0.0;
        Poi instance = new Poi();
        instance.setLng(lng);
    }

    /**
     * Test of setPoiType method, of class Poi.
     */
    @Test
    public void testSetPoiType() {
        System.out.println("setPoiType");
        int markerID = 0;
        Poi instance = new Poi();
        instance.setPoiType(markerID);
    }

    /**
     * Test of getPoiID method, of class Poi.
     */
    @Test
    public void testGetPoiID() {
        System.out.println("getPoiID");
        Poi instance = new Poi();
        int expResult = 0;
        int result = instance.getPoiID();
        assertEquals(expResult, result);
    }

    /**
     * Test of getLat method, of class Poi.
     */
    @Test
    public void testGetLat() {
        System.out.println("getLat");
        Poi instance = new Poi();
        double expResult = 0.0;
        double result = instance.getLat();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getLng method, of class Poi.
     */
    @Test
    public void testGetLng() {
        System.out.println("getLng");
        Poi instance = new Poi();
        double expResult = 0.0;
        double result = instance.getLng();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getMarkerID method, of class Poi.
     */
    @Test
    public void testGetMarkerID() {
        System.out.println("getMarkerID");
        Poi instance = new Poi();
        int expResult = 0;
        int result = instance.getMarkerID();
        assertEquals(expResult, result);
    }
    
}
