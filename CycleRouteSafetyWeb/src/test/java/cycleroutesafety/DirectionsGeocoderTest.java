/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cycleroutesafety;

import com.teamdev.jxmaps.LatLng;
import com.teamdev.jxmaps.Map;
import com.teamdev.jxmaps.MapBrowser;
import com.teamdev.jxmaps.MapObject;
import com.teamdev.jxmaps.Marker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Andor Horvath
 */
public class DirectionsGeocoderTest {
    
    public DirectionsGeocoderTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

   
    /**
     * Test of addAllPois method, of class DirectionsGeocoder.
     */
    @Test
    public void testAddAllPois() {
        System.out.println("addAllPois");
        ArrayList<Poi> pois = new ArrayList<>();
        Poi testPoi = new Poi(999, 1.1, 2.2, 1);
        pois.add(testPoi);
        DirectionsGeocoder instance = new DirectionsGeocoder();
        cycleroutesafety.MyMarker testMarker = new cycleroutesafety.MyMarker(1, "", "");
        ArrayList<cycleroutesafety.MyMarker> tempAllMarkers = new ArrayList<>();
        tempAllMarkers.add(testMarker);
        instance.setAllMarkers(tempAllMarkers);
        instance.allMarkers.add(testMarker);
        
        instance.addAllPois(pois, instance.getMap());
    }

    /**
     * Test of hideAllPois method, of class DirectionsGeocoder.
     */
    @Test
    public void testHideAllPois() {
        System.out.println("hideAllPois");
        DirectionsGeocoder instance = new DirectionsGeocoder();
        instance.hideAllPois();
    }

    /**
     * Test of showAllPois method, of class DirectionsGeocoder.
     */
    @Test
    public void testShowAllPois() {
        System.out.println("showAllPois");
        DirectionsGeocoder instance = new DirectionsGeocoder();
        instance.showAllPois();
    }

    /**
     * Test of arePoisNeedDbPersist method, of class DirectionsGeocoder.
     */
    @Test
    public void testArePoisNeedDbPersist() {
        System.out.println("arePoisNeedDbPersist");
        ArrayList<Poi> poiArrayOne = new ArrayList<>();
        Poi testPoi = new Poi();
        poiArrayOne.add(testPoi);
        
        ArrayList<Poi> poiArrayTwo = new ArrayList<>();
        Poi testPoiTwo = new Poi();
        poiArrayTwo.add(testPoiTwo);
        
        DirectionsGeocoder instance = new DirectionsGeocoder();
        boolean expResult = false;
        boolean result = instance.arePoisNeedDbPersist(poiArrayOne, poiArrayTwo);
        assertEquals(expResult, result);
    }

    /**
     * Test of typeOfMarker method, of class DirectionsGeocoder.
     */
    @Test
    public void testGetTypeOfMarker() {
        System.out.println("typeOfMarker");
        int markerID = 1;
        DirectionsGeocoder instance = new DirectionsGeocoder();
        String expResult = "";
        
        ArrayList<cycleroutesafety.MyMarker> tempAllMarkers = new ArrayList<>(0);
        instance.setAllMarkers(tempAllMarkers);
        
        String result = instance.getTypeOfMarker(markerID);
        assertEquals(expResult, result);
    }

    /**
     * Test of getFromField method, of class DirectionsGeocoder.
     */
    @Test
    public void testGetFromField() {
        System.out.println("getFromField");
        DirectionsGeocoder instance = new DirectionsGeocoder();
        String expResult = "Budapest, Pázmány Péter stny. 1a, 1117 Magyarország";
        String result = instance.getFromField().getText();
        assertEquals(expResult, result);
    }

    /**
     * Test of getToField method, of class DirectionsGeocoder.
     * I am testing the contained value.
     */
    @Test
    public void testGetToField() {
        System.out.println("getToField");
        DirectionsGeocoder instance = new DirectionsGeocoder();
        String expResult = "Budapest, Pázmány Péter stny. 1a, 1117 Magyarország";
        String result = instance.getToField().getText();
        assertEquals(expResult, result);
    }

    /**
     * Test of configureControlPanel method, of class DirectionsGeocoder.
     */
    @Test
    public void testConfigureControlPanel() {
        System.out.println("configureControlPanel");
        DirectionsGeocoder instance = new DirectionsGeocoder();
        instance.configureControlPanel();
    }

    /**
     * Test of getPreferredHeight method, of class DirectionsGeocoder.
     */
    @Test
    public void testGetPreferredHeight() {
        System.out.println("getPreferredHeight");
        DirectionsGeocoder instance = new DirectionsGeocoder();
        int expResult = 169;
        int result = instance.getPreferredHeight();
        assertEquals(expResult, result);
    }

    /**
     * Test of addText method, of class DirectionsGeocoder.
     */
    @Test
    public void testAddText() {
        System.out.println("addText");
        JTextArea messageBar = new JTextArea();
        String text = "";
        DirectionsGeocoder.addText(messageBar, text);
    }

    /**
     * Test of createRoute method, of class DirectionsGeocoder.
     */
    @Test
    public void testCreateRoute() {
        System.out.println("createRoute");
        JTextArea messageBar = new JTextArea();
        String newRouteName = "";
        DirectionsGeocoder instance = new DirectionsGeocoder();
        instance.createRoute(messageBar, newRouteName);
    }

    /**
     * Test of modifyRoute method, of class DirectionsGeocoder.
     */
    @Test
    public void testModifyRoute() {
        System.out.println("modifyRoute");
        Route route = new Route();
        JTextArea messageBar = new JTextArea();
        DirectionsGeocoder instance = new DirectionsGeocoder();
        instance.modifyRoute(route, messageBar);
    }

    /**
     * Test of openRoute method, of class DirectionsGeocoder.
     */
    @Test
    public void testOpenRoute() {
        System.out.println("openRoute");
        Route route = new Route();
        JTextArea messageBar = new JTextArea();
        DirectionsGeocoder instance = new DirectionsGeocoder();
        instance.openRoute(route, messageBar);
    }

    /**
     * Test of deleteRoute method, of class DirectionsGeocoder.
     */
    @Test
    public void testDeleteRoute() {
        System.out.println("deleteRoute");
        JTextArea messageBar = new JTextArea();
        DirectionsGeocoder instance = new DirectionsGeocoder();
        ManageDatabase mdb = new ManageDatabase();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        Route route = new Route();
        route.setRouteName("dummyTestName");
        mdb.createRoute(route.getRouteName(), "ahorvath", "Palma utca 6", "Andrassy ut 5", 1, dateFormat.format(date), true);
        
        instance.deleteRoute(route, messageBar);
    }

    /**
     * Test of readPoisFromDb method, of class DirectionsGeocoder.
     * 
     * The read ArrayList<Poi> result would represent the current database 
     * state.
     * As such I do not provide a sample to assertEquals to, because I only want
     * to check if the procedure is executed without errors.
     */
    @Test
    public void testReadPoisFromDb() {
        System.out.println("readPoisFromDb");
        DirectionsGeocoder instance = new DirectionsGeocoder();
        ArrayList<Poi> result = instance.readPoisFromDb();
    }

    /**
     * Test of loadAndRegisterCustomFonts method, of class DirectionsGeocoder.
     */
    @Test
    public void testLoadAndRegisterCustomFonts() {
        System.out.println("loadAndRegisterCustomFonts");
        DirectionsGeocoder.loadAndRegisterCustomFonts();
    }

    /**
     * Test of updateFromFieldText method, of class DirectionsGeocoder.
     */
    @Test
    public void testUpdateFromFieldText() {
        System.out.println("updateFromFieldText");
        String text = "";
        DirectionsGeocoder instance = new DirectionsGeocoder();
        instance.updateFromFieldText(text);
    }

    /**
     * Test of updateToFieldText method, of class DirectionsGeocoder.
     */
    @Test
    public void testUpdateToFieldText() {
        System.out.println("updateToFieldText");
        String text = "";
        DirectionsGeocoder instance = new DirectionsGeocoder();
        instance.updateToFieldText(text);
    }

    /**
     * Test of computeNearPois method, of class DirectionsGeocoder.
     */
    @Test
    public void testComputeNearPois() {
        System.out.println("computeNearPois");
        double radius = 0.01;
        DirectionsGeocoder instance = new DirectionsGeocoder();
        HashSet<Marker> expResult = new HashSet<>(0);
        HashSet<Marker> result = instance.computeNearPois(radius);
        assertEquals(expResult, result);
    }

    /**
     * Test of setNearPoisVisible method, of class DirectionsGeocoder.
     */
    @Test
    public void testSetNearPoisVisible() {
        System.out.println("setNearPoisVisible");
        DirectionsGeocoder instance = new DirectionsGeocoder();
        instance.setNearPoisVisible();
    }

    /**
     * Test of setNearPoisHided method, of class DirectionsGeocoder.
     */
    @Test
    public void testSetNearPoisHided() {
        System.out.println("setNearPoisHided");
        DirectionsGeocoder instance = new DirectionsGeocoder();
        instance.setNearPoisHided();
    }

    /**
     * Test of setOnMapPoisVisible method, of class DirectionsGeocoder.
     */
    @Test
    public void testSetOnMapPoisVisible() {
        System.out.println("setOnMapPoisVisible");
        DirectionsGeocoder instance = new DirectionsGeocoder();
        instance.setOnMapPoisVisible();
    }

    /**
     * Test of setOnMapPoisHided method, of class DirectionsGeocoder.
     */
    @Test
    public void testSetOnMapPoisHided() {
        System.out.println("setOnMapPoisHided");
        DirectionsGeocoder instance = new DirectionsGeocoder();
        instance.setOnMapPoisHided();
    }

    /**
     * Test of getCrossRoads method, of class DirectionsGeocoder.
     */
    @Test
    public void testGetCrossRoads() {
        System.out.println("getCrossRoads");
        DirectionsGeocoder instance = new DirectionsGeocoder();
        ArrayList<LatLng> expResult = new ArrayList(0);
        ArrayList<LatLng> result = instance.getCrossRoads();
        assertEquals(expResult, result);
    }

    /**
     * Test of setCrossRoads method, of class DirectionsGeocoder.
     */
    @Test
    public void testSetCrossRoads() {
        System.out.println("setCrossRoads");
        ArrayList<LatLng> crossRoads = new ArrayList<>(0);
        DirectionsGeocoder instance = new DirectionsGeocoder();
        instance.setCrossRoads(crossRoads);
    }
    
    /**
     * Test of createMarker method, of class DirectionsGeocoder.
     */
    @Test
    public void testCreateMarker() {
        System.out.println("createMarker");
        JTextArea messageBar = new JTextArea();
        String description = "";
        String markerType = "";
        DirectionsGeocoder instance = new DirectionsGeocoder();
        ArrayList<cycleroutesafety.MyMarker> result = instance.createMarker(messageBar, description, markerType);
        ManageDatabase mdb = new ManageDatabase();
        
        ArrayList<cycleroutesafety.MyMarker> expResultDataSet = mdb.readMarkers();
        boolean isEqual = expResultDataSet.equals(result);
        boolean expResult = true;
        assertEquals(expResult, isEqual);

        // removing the created "last" marker in the arrayList
        instance.allMarkers.remove(instance.getAllMarkers().get(instance.getAllMarkers().size()));
    }
}
