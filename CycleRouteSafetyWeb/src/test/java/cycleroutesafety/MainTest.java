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
public class MainTest {
    
    public MainTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of main method, of class Main.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        Main.main(args);
    }

    /**
     * Test of computeColsForMarkers method, of class Main.
     */
    @Test
    public void testComputeColsForMarkers() {
        System.out.println("computeColsForMarkers");
        int numberOfMarkers = 0;
        Integer expResult = 4;
        Integer result = Main.computeColsForMarkers(numberOfMarkers);
        assertEquals(expResult, result);
    }

    /**
     * Test of computeRowsForMarkers method, of class Main.
     */
    @Test
    public void testComputeRowsForMarkers() {
        System.out.println("computeRowsForMarkers");
        int numberOfMarkers = 0;
        Integer expResult = 0;
        Integer result = Main.computeRowsForMarkers(numberOfMarkers);
        assertEquals(expResult, result);
    }
    
}
