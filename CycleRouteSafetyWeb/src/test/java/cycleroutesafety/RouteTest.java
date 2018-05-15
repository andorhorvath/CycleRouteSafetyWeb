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
public class RouteTest {
    
    public RouteTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of toString method, of class Route.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Route instance = new Route();
        String expResult = "[" + instance.getRouteID() + "; " + instance.getRouteName() + "; " + instance.getAuthor() + "; " + instance.getStartPoint()+ "; " + instance.getFinishPoint() + "; " + instance.getRouteLength() + "; " + instance.getLastUpdateTime() + "]";
        String result = instance.toString();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of setRouteName method, of class Route.
     */
    @Test
    public void testSetRouteName() {
        System.out.println("setRouteName");
        String routeName = "";
        Route instance = new Route();
        instance.setRouteName(routeName);
        
        String result = instance.getRouteName();

        assertEquals(result, routeName);
    }

    /**
     * Test of setAuthor method, of class Route.
     */
    @Test
    public void testSetAuthor() {
        System.out.println("setAuthor");
        String author = "";
        Route instance = new Route();
        instance.setAuthor(author);
        
        String result = instance.getAuthor();
        
        assertEquals(result, author);
    }

    /**
     * Test of setStartPoint method, of class Route.
     */
    @Test
    public void testSetStartPoint() {
        System.out.println("setStartPoint");
        String startPoint = "";
        Route instance = new Route();
        instance.setStartPoint(startPoint);
        
        String result = instance.getStartPoint();
        
        assertEquals (result, startPoint);
    }

    /**
     * Test of setFinishPoint method, of class Route.
     */
    @Test
    public void testSetFinishPoint() {
        System.out.println("setFinishPoint");
        String finishPoint = "";
        Route instance = new Route();
        instance.setFinishPoint(finishPoint);
        
        String result = instance.getFinishPoint();
        
        assertEquals (result, finishPoint);
    }

    /**
     * Test of setRouteLength method, of class Route.
     */
    @Test
    public void testSetRouteLength() {
        System.out.println("setRouteLength");
        int routeLength = 0;
        Route instance = new Route();
        instance.setRouteLength(routeLength);
    }

    /**
     * Test of setLastUpdateTime method, of class Route.
     */
    @Test
    public void testSetLastUpdateTime() {
        System.out.println("setLastUpdateTime");
        String lastUpdateTime = "";
        Route instance = new Route();
        instance.setLastUpdateTime(lastUpdateTime);
        
        String result = instance.getLastUpdateTime();
        
        assertEquals (result, lastUpdateTime);
    }

    /**
     * Test of getRouteID method, of class Route.
     */
    @Test
    public void testGetRouteID() {
        System.out.println("getRouteID");
        Route instance = new Route();
        int expResult = 0;
        int result = instance.getRouteID();
        assertEquals(expResult, result);
    }

    /**
     * Test of getRouteName method, of class Route.
     */
    @Test
    public void testGetRouteName() {
        System.out.println("getRouteName");
        Route instance = new Route();
        String expResult = null;
        String result = instance.getRouteName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAuthor method, of class Route.
     */
    @Test
    public void testGetAuthor() {
        System.out.println("getAuthor");
        Route instance = new Route();
        String expResult = null;
        String result = instance.getAuthor();
        assertEquals(expResult, result);
    }

    /**
     * Test of getStartPoint method, of class Route.
     */
    @Test
    public void testGetStartPoint() {
        System.out.println("getStartPoint");
        Route instance = new Route();
        String expResult = null;
        String result = instance.getStartPoint();
        assertEquals(expResult, result);
    }

    /**
     * Test of getFinishPoint method, of class Route.
     */
    @Test
    public void testGetFinishPoint() {
        System.out.println("getFinishPoint");
        Route instance = new Route();
        String expResult = null;
        String result = instance.getFinishPoint();
        assertEquals(expResult, result);
    }

    /**
     * Test of getRouteLength method, of class Route.
     */
    @Test
    public void testGetRouteLength() {
        System.out.println("getRouteLength");
        Route instance = new Route();
        int expResult = 0;
        int result = instance.getRouteLength();
        assertEquals(expResult, result);
    }

    /**
     * Test of getLastUpdateTime method, of class Route.
     */
    @Test
    public void testGetLastUpdateTime() {
        System.out.println("getLastUpdateTime");
        Route instance = new Route();
        String expResult = null;
        String result = instance.getLastUpdateTime();
        assertEquals(expResult, result);
    }
    
}
