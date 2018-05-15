/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cycleroutesafety;

import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Andor Horvath
 */
public class ManageDatabaseTest {
    
    public ManageDatabaseTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of countNumberOfMarkers method, of class ManageDatabase.
     * 
     * Testing for an actual value of 5 markers.
     */
    @Test
    public void testCountNumberOfMarkers() {
        System.out.println("countNumberOfMarkers");
        ManageDatabase instance = new ManageDatabase();
        int expResult = 6;
        int result = instance.countNumberOfMarkers();
        assertEquals(expResult, result);
    }

    /**
     * Test of readPois method, of class ManageDatabase.
     * 
     * For an empty Database the test is successful.
     * 
     * If data is loaded, the result fails.
     * See testReadRoutes for explanation of failure:
     * https://stackoverflow.com/questions/16788275/what-exactly-does-assertequals-check-for-when-asserting-lists
     * these should be equal... by human standards
     */
    @Test
    public void testReadPois() {
        System.out.println("readPois");
        ManageDatabase instance = new ManageDatabase();
        ArrayList<Poi> expResult = new ArrayList<>(0);
        ArrayList<Poi> result = instance.readPois();
        assertEquals(expResult, result);
    }

    /**
     * Test of readOneMarkerTypeById method, of class ManageDatabase.
     */
    @Test
    public void testReadOneMarkerTypeById() {
        System.out.println("readOneMarkerTypeById");
        int markerID = 0;
        ManageDatabase instance = new ManageDatabase();
        String expResult = "";
        String result = instance.readOneMarkerTypeById(markerID);
        assertEquals(expResult, result);
    }

    /**
     * Test of createRoute method, of class ManageDatabase.
     */
    @Test
    public void testCreateRoute() {
        System.out.println("createRoute");
        String routeName = "";
        String author = "";
        String startPoint = "";
        String finishPoint = "";
        int routeLength = 0;
        String lastUpdateTime = "";
        boolean plannedRoute = false;
        ManageDatabase instance = new ManageDatabase();
        instance.createRoute(routeName, author, startPoint, finishPoint, routeLength, lastUpdateTime, plannedRoute);
    }

    /**
     * Test of createMarker method, of class ManageDatabase.
     */
    @Test
    public void testCreateMarker() {
        System.out.println("createMarker");
        String description = "";
        String markerType = "";
        ManageDatabase instance = new ManageDatabase();
        instance.createMarker(description, markerType);

    }
    
    

    /**
     * Test of refreshPois method, of class ManageDatabase.
     */
    @Test
    public void testRefreshPois() {
        System.out.println("refreshPois");
        ArrayList<Poi> newPois = new ArrayList<>(0);
        ManageDatabase instance = new ManageDatabase();
        instance.refreshPois(newPois);
    }

    /**
     * Test of modifyRoute method, of class ManageDatabase.
     */
    @Test
    public void testModifyRoute() {
        System.out.println("modifyRoute");
        int routeID = 0;
        String routeName = "";
        String author = "";
        String startPoint = "";
        String finishPoint = "";
        int routeLength = 0;
        String lastUpdateTime = "";
        boolean plannedRoute = false;
        ManageDatabase instance = new ManageDatabase();
        instance.modifyRoute(routeID, routeName, author, startPoint, finishPoint, routeLength, lastUpdateTime, plannedRoute);
    }

    /**
     * Test of modifyPoi method, of class ManageDatabase.
     */
    @Test
    public void testModifyPoi() {
        System.out.println("modifyPoi");
        int poiID = 0;
        double lat = 0.0;
        double lng = 0.0;
        int markerID = 0;
        ManageDatabase instance = new ManageDatabase();
        instance.modifyPoi(poiID, lat, lng, markerID);
    }

    /**
     * Test of deleteRoute method, of class ManageDatabase.
     */
    @Test
    public void testDeleteRoute() {
        System.out.println("deleteRoute");
        int routeID = 0;
        ManageDatabase instance = new ManageDatabase();
        instance.deleteRoute(routeID);
    }

    /**
     * Test of deletePoi method, of class ManageDatabase.
     */
    @Test
    public void testDeletePoi() {
        System.out.println("deletePoi");
        int poiID = 0;
        ManageDatabase instance = new ManageDatabase();
        instance.deletePoi(poiID);
    }
    
    /**
     * Test of deleteMarker method, of class ManageDatabase.
     */
    @Test
    public void testDeleteMarker() {
        System.out.println("deleteMarker");
        int markerId = 0;
        ManageDatabase instance = new ManageDatabase();
        instance.deleteMarker(markerId);
    }

    /**
     * Test of logSQLException method, of class ManageDatabase.
     */
    @Test
    public void testLogSQLException() {
        System.out.println("logSQLException");
        SQLException exceptionCatched = new SQLException();
        ManageDatabase instance = new ManageDatabase();
        instance.logSQLException(exceptionCatched);
    }
    
    /**
     * Test of readOneRouteById method, of class ManageDatabase.
     * 
     * They seems to be equal as null and 0 values are returned.
     */
    @Test
    public void testReadOneRouteById() {
        System.out.println("readOneRouteById");
        int routeID = 1;
        ManageDatabase instance = new ManageDatabase();
        Route expectedResult = new Route();

        Route result = instance.readOneRouteById(routeID);
        assertEquals(expectedResult, result);
    }
    
    /**
     * Test of readRoutes method, of class ManageDatabase.
     * https://stackoverflow.com/questions/16788275/what-exactly-does-assertequals-check-for-when-asserting-lists
     * these should be equal... by human standards
     */
    @Test
    public void testReadRoutes() {
        System.out.println("readRoutes");
        ManageDatabase instance = new ManageDatabase();
        Route routeOne = new Route(1, "Városligeti út", "ahorvath", "Budapest, Kós Károly stny., 1146 Hungary", "Budapest, Pálma u. 6, 1146 Hungary", 1, "2018-05-12 19:51:09");
        Route routeTwo = new Route(2, "Kiss Rozihoz", "ahorvath", "Nefelejcs utca 24", "Budapest, Pálma u. 6, 1146 Hungary", 1, "2018-05-12 19:22:37");
        //ArrayList<Route> expResult = [[1; Városligeti út; ahorvath; Budapest, Kós Károly stny., 1146 Hungary; Budapest, Pálma u. 6, 1146 Hungary; 1; 2018-05-12 19:51:09],
        //[2; Kiss Rozihoz; ahorvath; Nefelejcs utca 24; Budapest, Pálma u. 6, 1146 Hungary; 1; 2018-05-12 19:22:37]];
        ArrayList<Route> sampleDataExpectedResult = new ArrayList<>();
        sampleDataExpectedResult.add(routeOne);
        sampleDataExpectedResult.add(routeTwo);
        ArrayList<Route> result = instance.readRoutes();
        assertEquals(sampleDataExpectedResult, result);
    }

    /**
     * Test of readMarkers method, of class ManageDatabase.
     * 
     * See testReadRoutes for explanation of failure:
     * https://stackoverflow.com/questions/16788275/what-exactly-does-assertequals-check-for-when-asserting-lists
     * these should be equal... by human standards
     */
    @Test
    public void testReadMarkers() {
        System.out.println("readMarkers");
        ManageDatabase instance = new ManageDatabase();
        ArrayList<MyMarker> expResult = null;
        ArrayList<MyMarker> result = instance.readMarkers();
        assertEquals(expResult, result);
    }
}
