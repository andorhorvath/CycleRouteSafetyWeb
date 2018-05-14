/*
 * Copyright (c) 2000-2017 TeamDev Ltd. All rights reserved.
 * Use is subject to Apache 2.0 license terms.
 */
package cycleroutesafety;

import com.teamdev.jxmaps.ControlPosition;
import com.teamdev.jxmaps.DirectionsRequest;
import com.teamdev.jxmaps.DirectionsResult;
import com.teamdev.jxmaps.DirectionsRouteCallback;
import com.teamdev.jxmaps.DirectionsStatus;
import com.teamdev.jxmaps.GeocoderCallback;
import com.teamdev.jxmaps.GeocoderRequest;
import com.teamdev.jxmaps.GeocoderResult;
import com.teamdev.jxmaps.GeocoderStatus;
import com.teamdev.jxmaps.InfoWindow;
import com.teamdev.jxmaps.LatLng;
import com.teamdev.jxmaps.Map;
import com.teamdev.jxmaps.MapMouseEvent;
import com.teamdev.jxmaps.MapOptions;
import com.teamdev.jxmaps.MapStatus;
import com.teamdev.jxmaps.MapTypeControlOptions;
import com.teamdev.jxmaps.Marker;
import com.teamdev.jxmaps.TravelMode;
import com.teamdev.jxmaps.swing.MapView;
import com.teamdev.jxmaps.Icon;
import com.teamdev.jxmaps.DirectionsLeg;
import com.teamdev.jxmaps.DirectionsStep;
import com.teamdev.jxmaps.DirectionsRoute;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Defines the map manager tools, like from field, to field, control panels,
 * while also configures them. Initializes map shown. configures the map to a
 * default location when the program is starting up
 *
 * @author Vitaly Eremenko, modified by Andor Horvath for CycleRouteSafety
 */
public final class DirectionsGeocoder extends MapView implements ControlPanel {

    private static final Color FOREGROUND_COLOR = new Color(0xBB, 0xDE, 0xFB);
    // declaring a default center for the map to show while starting up
    private final JTextField fromField;
    private final JTextField toField;
    private final String defaultFrom;
    private final String defaultTo;
    public int actualMarkerType = -1;
    /**
     * Contains the persisted POIs and if there are any, it also contains the
     * "newly" dropped POIs, that are not persisted to the db yet
     */
    public ArrayList<Poi> allPois = new ArrayList<>();
    /**
     * Contains the persisted POI's every time
     */
    public ArrayList<Poi> persistedPois = new ArrayList<>();
    public ArrayList<Marker> jxMarkersOnMap = new ArrayList<>();
    public ArrayList<MyMarker> allMarkers;

    public ArrayList<LatLng> crossRoads = new ArrayList<>();
    public HashSet<Marker> nearPois = new HashSet<>();
    
    JPanel controlPanel;
    public Map map;
    /**
     * The constructor creates the map object, while also creating and
     * configuring the GUI to manage the map and it's services.
     */
    public DirectionsGeocoder() {
        defaultFrom = "Budapest, Pázmány Péter stny. 1a, 1117 Magyarország";
        defaultTo = defaultFrom;

        controlPanel = new JPanel();

        fromField = new JTextField(defaultFrom);
        toField = new JTextField(defaultTo);
        jxMarkersOnMap = new ArrayList<>();

        configureControlPanel();

        setOnMapReadyHandler((MapStatus status) -> {
            map = getMap();
            // Setting the map center
            //map.setCenter(new LatLng(41.85, -87.65));
            // Setting initial zoom value
            //map.setZoom(10.0);
            // Creating a map options object
            MapOptions options = new MapOptions();
            // Creating a map type control options object
            MapTypeControlOptions controlOptions = new MapTypeControlOptions();
            // Changing position of the map type control
            controlOptions.setPosition(ControlPosition.TOP_RIGHT);
            // Setting map type control options
            options.setMapTypeControlOptions(controlOptions);
            // Setting map options
            map.setOptions(options);

            // performing a Geocode for the default address to make the
            // map be centered on ELTE IK when starting up
            performGeocode(defaultFrom);

            allPois = readPoisFromDb();
            persistedPois = allPois;
            addAllPois(allPois, map);

            map.addEventListener("click", new MapMouseEvent() {
                @Override
                public void onEvent(com.teamdev.jxmaps.MouseEvent mouseEvent) {
                    // Creating a new marker
                    if (actualMarkerType != -1) {
                        final Marker marker = new Marker(map);
                        Poi newPoi = new Poi(allPois.get(allPois.size() - 1).getPoiID() + 1, mouseEvent.latLng().getLat(), mouseEvent.latLng().getLng(), actualMarkerType);
                        allPois.add(newPoi);
                        addPoi(newPoi, marker);
                    }
                }
            });
        });
    }

    /**
     * Creates a POI while also making it visible on the map object with a
 JxMaps-MyMarker. Also binding the onClick action's event listener.
     *
     * @param poi
     * @param jxMapsMarker
     */
    public void addPoi(Poi poi, Marker jxMapsMarker) {
        //setting the MyMarker's type & 
        String actualMarkerPicture = getTypeOfMarker(poi.getMarkerID());
        Icon icon = new Icon();
        icon.loadFromStream(DirectionsGeocoder.class.getResourceAsStream(actualMarkerPicture), "png");
        jxMapsMarker.setIcon(icon);
        jxMapsMarker.setPosition(new LatLng(poi.getLat(), poi.getLng()));

        jxMapsMarker.addEventListener("click", new MapMouseEvent() {
            @Override
            public void onEvent(com.teamdev.jxmaps.MouseEvent mouseEvent) {
                // Removing marker from the map
                ArrayList<Poi> stayPois = new ArrayList<>();
                for (int n = 0; n < allPois.size(); ++n) {
// TODO: possible optimization
                    if (!(allPois.get(n).getLat() == jxMapsMarker.getPosition().getLat()
                            && allPois.get(n).getLng() == jxMapsMarker.getPosition().getLng())) {
                        stayPois.add(allPois.get(n));
                    }
                }
                allPois = stayPois;
                jxMapsMarker.remove();
            }
        });
        jxMarkersOnMap.add(jxMapsMarker);
    }

    /**
     * Adding all poi objects of input parameter pois to map.
     *
     * @param pois
     * @param map
     */
    public void addAllPois(ArrayList<Poi> pois, Map map) {
        // not using Java 8 functional operators, as their performance impact
        // may be higher. See references:
        // 3-reasons-why-you-shouldnt-replace-your-for-loops-by-stream-foreach
        for (Poi loopingPoi : pois) {
            final Marker marker = new Marker(map);
            addPoi(loopingPoi, marker);
        }
    }

    /**
     * Looping through map's pois and setting their visibility to false, hiding
     * them on the map.
     */
    public void hideAllPois() {
        for (int n = 0; n < jxMarkersOnMap.size(); ++n) {
            jxMarkersOnMap.get(n).setVisible(false);
        }
    }

    /**
     * Looping through map's pois and setting their visibility to true, making
     * them be displayed on the map.
     */
    public void showAllPois() {
        for (int n = 0; n < jxMarkersOnMap.size(); ++n) {
            jxMarkersOnMap.get(n).setVisible(true);
        }
    }


    /**
     * When exiting from the program with newly planted POIs, this is called to
     * decide whether the question window should be popped, asking for saving
     * them to the DB.
     *
     * Returns true/false if the size of the two input ArrayList of Poi's size
     * is different, or if it's the same, then it checks if any of their Poi's
     * place is modified. If so, returns true. Otherwise returns false.
     *
     * @param poiArrayOne
     * @param poiArrayTwo
     * @return
     */
    public boolean arePoisNeedDbPersist(ArrayList<Poi> poiArrayOne, ArrayList<Poi> poiArrayTwo) {
        boolean isPersistNeeded = false;
        if (poiArrayOne.size() != poiArrayTwo.size()) {
            isPersistNeeded = true;
        } else {
            for (int n = 0; n < poiArrayOne.size() && !isPersistNeeded; ++n) {
                if (poiArrayOne.get(n).getLat() != poiArrayTwo.get(n).getLat()) {
                    isPersistNeeded = true;
                }
                if (poiArrayOne.get(n).getLng() != poiArrayTwo.get(n).getLng()) {
                    isPersistNeeded = true;
                }
                if (poiArrayOne.get(n).getMarkerID() != poiArrayTwo.get(n).getMarkerID()) {
                    isPersistNeeded = true;
                }
            }
        }
        return isPersistNeeded;
    }

    /**
     * Returns the given MyMarker's type value according to the MyMarker's ID
     *
     * @param markerID
     * @return markerType value of particular MyMarker object
     */
    public String getTypeOfMarker(int markerID) {
        String typeText = "";
        Boolean isFoundAlready = false;
//TODO: possible optimization of loop
//2        for (int n = 0; n < allMarkers.length && !isFoundAlready; ++n) {
//2            if (markerID == allMarkers[n].getMarkerID()) {
        for (int n = 0; n < allMarkers.size() && !isFoundAlready; ++n) {
            if (markerID == allMarkers.get(n).getMarkerID()) {
                typeText = allMarkers.get(n).getMarkerType();
            }
        }
        return typeText;
    }

    public JTextField getFromField() {
        return fromField;
    }

    public JTextField getToField() {
        return fromField;
    }

    @Override
    public JComponent getControlPanel() {
        return controlPanel;
    }

    /**
     * Setting up the Navigational panel that let the user run new from-to
     * routes, as it is done in googlemaps API. This configures both the view
     * and controller parts of the panel.
     *
     *
     */
    @Override
    public void configureControlPanel() {
        // setting the view properties like color, size, etc.
        controlPanel.setBackground(Color.white);
        controlPanel.setLayout(new BorderLayout());

        JPanel navigationControlPanel = new JPanel(new GridBagLayout());
        navigationControlPanel.setBackground(new Color(61, 130, 248));

        Font robotoPlain13 = new Font("Roboto", 0, 13);
        fromField.setForeground(FOREGROUND_COLOR);
        toField.setForeground(FOREGROUND_COLOR);

        fromField.setFont(robotoPlain13);
        toField.setFont(robotoPlain13);

        fromField.setOpaque(false);
        toField.setOpaque(false);

        fromField.setBorder(new UnderscoreBorder());
        toField.setBorder(new UnderscoreBorder());

        // adding ActionListener that would perform the Directions calculation
        // when hitting enter or search icon on the two fields
        fromField.addActionListener((ActionEvent ae) -> {
            calculateDirection();
            updateFromFieldText(fromField.getText());
        });
        toField.addActionListener((ActionEvent ae) -> {
            calculateDirection();
            updateToFieldText(toField.getText());
        });

        // setting the "usual googlemaps order" of the icons accompanying the
        // navigational pane
        JLabel fromIcon = new JLabel(new ImageIcon(DirectionsGeocoder.class.getResource("res/from.png")));
        JLabel dotsIcon = new JLabel(new ImageIcon(DirectionsGeocoder.class.getResource("res/dots.png")));
        JLabel toIcon = new JLabel(new ImageIcon(DirectionsGeocoder.class.getResource("res/to.png")));
        JLabel changeIcon = new JLabel(new ImageIcon(DirectionsGeocoder.class.getResource("res/change.png")));
        changeIcon.setToolTipText("Reverse starting point and destination");
        changeIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        changeIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String from = fromField.getText();
                String to = toField.getText();
                fromField.setText(to);
                toField.setText(from);
                calculateDirection();
            }
        });

        // building up the contents of the panel with the previous "items"
        navigationControlPanel.add(fromIcon, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(22, 30, 0, 0), 0, 0));
        navigationControlPanel.add(dotsIcon, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 33, 0, 0), 0, 0));
        navigationControlPanel.add(toIcon, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(6, 30, 25, 0), 0, 0));

        navigationControlPanel.add(fromField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(19, 22, 0, 0), 0, 0));
        navigationControlPanel.add(toField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(3, 22, 0, 0), 0, 0));

        navigationControlPanel.add(changeIcon, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 22, 0, 22), 0, 0));

        this.controlPanel.add(navigationControlPanel, BorderLayout.NORTH);
    }

    /**
     * Returns a pre-defined value, 169 for the PreferredHeight.
     *
     * @return 169
     */
    @Override
    public int getPreferredHeight() {
        return 169;
    }

    /**
     * UnderscoreBorder is used for the "lines" under the text written to the
     * navigational panel, under each address input field.
     */
    class UnderscoreBorder extends AbstractBorder {

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(FOREGROUND_COLOR);//
            g.drawLine(0, height - 1, width, height - 1);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(0, 0, 5, 0);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.bottom = 5;
            return insets;
        }
    }

    /**
     * With the two input fields of the navigational panel filled, this call
     * will manage getting the data from these fields, file a request to the
     * googlemaps API, receiving the answer then rendering it in the map object
     * while also moving and zooming the map as required according to the from
     * and to addresses.
     */
    public void calculateDirection() {
        // Getting the associated map object
        final Map map = getMap();
        // Creating a directions request
        DirectionsRequest request = new DirectionsRequest();
        // Setting of the origin location to the request
        request.setOriginString(fromField.getText());
        // Setting of the destination location to the request
        request.setDestinationString(toField.getText());
        // Setting of the travel mode
        request.setTravelMode(TravelMode.DRIVING);
        // Calculating the route between locations
        getServices().getDirectionService().route(request, new DirectionsRouteCallback(map) {
            @Override
            public void onRoute(DirectionsResult result, DirectionsStatus status) {
                // Checking of the operation status
                if (status == DirectionsStatus.OK) {
                    // Drawing the calculated route on the map
                    map.getDirectionsRenderer().setDirections(result);

                    // I am saving the Latlng array that is a part of the returned
                    // DirectionsResult object as I would need this information when
                    // I want to show only the close POIs to the planned route.
                    // DirectionsRoute[] is the returned answer after the geocoding
                    //   >> DirectionsRoute[0] is the firts route planned, the "best"
                    //     >> legs[] is the polylines between each waypoint of the route
                    //       >> steps[] are the straight lines
                    //         >> LatLng[] are each crossings' LatLng coordinates
                    DirectionsRoute[] routes = result.getRoutes();
                    if (routes.length > 0) {
                        for (DirectionsLeg eachLeg : routes[0].getLegs()) {
                            DirectionsStep[] stepsOfOneLeg = eachLeg.getSteps();
                            for (DirectionsStep eachStep : stepsOfOneLeg) {
                                for (LatLng loopingTurnPoints : eachStep.getPath()) {
                                    crossRoads.add(loopingTurnPoints);
                                }
                            }
                        }
                    }

                } else {
                    JOptionPane.showMessageDialog(DirectionsGeocoder.this,
                            "Hiba lépett fel az útvonaltervezéskor. Kérem ellenőrizze, hogy\n"
                            + "- van-e működő internetkapcsolat,\n"
                            + "- a googlemaps szolgáltatás elérhető-e\n"
                            + "- a telepítési előfeltételek teljesülnek-e\n"
                            + "A program futása tovább folytatódik.");
                }
            }
        });
    }

    /**
     * This will append the input parameter text to the input parameter message
     * bar. Basically it is used for "alerting" the user about that what happens
     * in the program actually according to it's use.
     *
     * @param messageBar
     * @param text
     */
    public static void addText(JTextArea messageBar, String text) {
        messageBar.setText(messageBar.getText() + "\n" + text);
    }

    /**
     * Creates a route according to the current state of the UI, saving the
     * route to the database. It also sends a status message to the message bar
     * on the map.
     *
     * @param messageBar
     * @param newRouteName
     */
    public void createRoute(JTextArea messageBar, String newRouteName) {
        ManageDatabase manageDatabase = new ManageDatabase();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String textFrom = fromField.getText();
        String textTo = toField.getText();

        manageDatabase.createRoute(newRouteName,
                "ahorvath", textFrom, textTo, 1, dateFormat.format(date), true);
        String text = "Sikeres mentés: " + newRouteName;
        addText(messageBar, text);
        JOptionPane.showMessageDialog(null, text);
    }

    /**
     * Creates a MyMarker with it's type and description fields specified by the
     * input parameters. It will also take message bar for input, where it will
     * be able to write some status messages about it's work.
     *
     * @param messageBar
     * @param description
     * @param markerType
     * @return
     */
    public ArrayList<MyMarker> createMarker(JTextArea messageBar, String description, String markerType) {
        ManageDatabase manageDatabase = new ManageDatabase();
        manageDatabase.createMarker(description, markerType);
        String text = "Sikeres mentés: " + markerType;
        addText(messageBar, text);
        JOptionPane.showMessageDialog(null, text);
        return manageDatabase.readMarkers();
    }

    /**
     * Modify an existing, opened route that is currently displayed on the map.
     * Saving the new from and to fields. It also displays it's status messages
     * for the given messageBar.
     *
     * @param route
     * @param messageBar
     */
    public void modifyRoute(Route route, JTextArea messageBar) {
        ManageDatabase manageDatabase = new ManageDatabase();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String textFrom = fromField.getText();
        String textTo = toField.getText();

// getter setter a fejlecre
        manageDatabase.modifyRoute(route.getRouteID(), route.getRouteName(),
                "ahorvath", textFrom, textTo, 1, dateFormat.format(date), true);
        String text = "Sikeres útvonal-felülírás történt: " + route.getRouteName() + " útvonalra";
        addText(messageBar, text);
        JOptionPane.showMessageDialog(null, text);
    }

    /**
     * Opens a route to display it on the currently shown map. It will also
     * display it's status messages on the message bar it got as input
     * parameter.
     *
     * @param route
     * @param messageBar
     */
    public void openRoute(Route route, JTextArea messageBar) {
        fromField.setText(route.getStartPoint());
        toField.setText(route.getFinishPoint());
        calculateDirection();
        String text = "Sikeres betöltés: " + route.getRouteName();
        addText(messageBar, text);
        JOptionPane.showMessageDialog(null, text);
    }

    /**
     * Deletes the currently shown route from the map and also from the DB.
     * Calling this function will erase everything about the currently open
     * route, making it impossible to recover it's data.
     *
     * @param route
     * @param messageBar
     */
    public void deleteRoute(Route route, JTextArea messageBar) {
        ManageDatabase manageDatabase = new ManageDatabase();
        manageDatabase.deleteRoute(route.getRouteID());
        String text = "Sikeres törlés:\n"
                + route.getRouteName()
                + "\n(ID " + route.getRouteID() + ")";
        addText(messageBar, text);
        JOptionPane.showMessageDialog(null, text);
    }

    /**
     * Reads the all the pois from the database, and puts them to the returned
     * arrayList object of Poi objects.
     *
     * @return array list of Pois stored in the database
     */
    public ArrayList<Poi> readPoisFromDb() {
        ManageDatabase manageDatabase = new ManageDatabase();
        return manageDatabase.readPois();
    }

    static void loadAndRegisterCustomFonts() {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, DirectionsGeocoder.class.getResourceAsStream("res/Roboto-Bold.ttf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, DirectionsGeocoder.class.getResourceAsStream("res/Roboto-Medium.ttf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, DirectionsGeocoder.class.getResourceAsStream("res/Roboto-Regular.ttf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, DirectionsGeocoder.class.getResourceAsStream("res/Roboto-Thin.ttf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, DirectionsGeocoder.class.getResourceAsStream("res/Roboto-Light.ttf")));
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void performGeocode(String text) {
        // Getting the associated map object
        final Map map = getMap();
        // Creating a geocode request
        GeocoderRequest request = new GeocoderRequest();
        // Setting address to the geocode request
        request.setAddress(text);

        // Geocoding position by the entered address
        getServices().getGeocoder().geocode(request, new GeocoderCallback(map) {
            @Override
            public void onComplete(GeocoderResult[] results, GeocoderStatus status) {
                // Checking operation status
                if ((status == GeocoderStatus.OK) && (results.length > 0)) {
                    // Getting the first result
                    GeocoderResult result = results[0];
                    // Getting a location of the result
                    LatLng location = result.getGeometry().getLocation();
                    map.setZoom(12.0);
                    // Setting the map center to result location
                    map.setCenter(location);
                    // Creating an information window
                    InfoWindow infoWindow = new InfoWindow(map);
                    // Putting the address and location to the content of the information window
                    infoWindow.setContent("<b>" + result.getFormattedAddress() + "</b><br>" + location.toString());
                    // Moving the information window to the result location
                    infoWindow.setPosition(location);
                    // Showing of the information window
                    infoWindow.open(map);
                }
            }
        });
    }

    /**
     * Updates the navigation panel's from address while also geocoding it, and
     * returning the results of the route planning to this new address. The new
     * address that will be geocoded is taken from the inputfield as a string.
     *
     * @param text
     */
    public void updateFromFieldText(String text) {
        // Getting the associated map object
        final Map map = getMap();
        // Creating a geocode request
        GeocoderRequest request = new GeocoderRequest();
        // Setting address to the geocode request
        request.setAddress(text);

        // Geocoding position by the entered address
        getServices().getGeocoder().geocode(request, new GeocoderCallback(map) {
            @Override
            public void onComplete(GeocoderResult[] results, GeocoderStatus status) {
                // Checking operation status
                if ((status == GeocoderStatus.OK) && (results.length > 0)) {
                    GeocoderResult result = results[0];
                    fromField.setText(result.getFormattedAddress());
                }
            }
        });
    }

    /**
     * Updates the navigation panel's to address while also geocoding it, and
     * returning the results of the route planning to this new address. The new
     * address that will be geocoded is taken from the inputfield as a string.
     *
     * @param text
     */
    public void updateToFieldText(String text) {
        // Getting the associated map object
        final Map map = getMap();
        // Creating a geocode request
        GeocoderRequest request = new GeocoderRequest();
        // Setting address to the geocode request
        request.setAddress(text);

        // Geocoding position by the entered address
        getServices().getGeocoder().geocode(request, new GeocoderCallback(map) {
            @Override
            public void onComplete(GeocoderResult[] results, GeocoderStatus status) {
                // Checking operation status
                if ((status == GeocoderStatus.OK) && (results.length > 0)) {
                    GeocoderResult result = results[0];
                    toField.setText(result.getFormattedAddress());
                }
            }
        });
    }

    /**
     * from the currently planned route's each step, loops through all the
     * crossings and show up only the POIs that are in the radius of each
     * crossroads.
     *
     * @param radius
     * @return an arrayList of Pois, containg all the POIs that are close to the
     * road
     */
    public HashSet<Marker> computeNearPois(double radius) {
        HashSet<Marker> nearMarkers = new HashSet<>();

        for (LatLng eachCrossRoad : this.crossRoads) {
            double xrLat = eachCrossRoad.getLat();
            double xrLng = eachCrossRoad.getLng();
            for (int n = 0; n < jxMarkersOnMap.size(); ++n) {
                LatLng poiLatLng = jxMarkersOnMap.get(n).getPosition();
                //TODO: possible optimization, use streams? https://zeroturnaround.com/rebellabs/java-8-explained-applying-lambdas-to-java-collections/
                if (poiLatLng.getLat() >= xrLat - radius
                        && poiLatLng.getLng() >= xrLng - radius
                        && poiLatLng.getLat() <= xrLat + radius
                        && poiLatLng.getLng() <= xrLng + radius) {
                    nearMarkers.add(jxMarkersOnMap.get(n));
                }
            }
        }
        return nearMarkers;
    }
    
    public void setNearPoisVisible() {
        for (Marker eachNearPoi : nearPois) {
            eachNearPoi.setVisible(true);
        }
    }
    
    public void setNearPoisHided() {
        for (Marker eachNearPoi : nearPois) {
            eachNearPoi.setVisible(false);
        }
    }
    
    public void setOnMapPoisVisible() {
        for (Marker eachOnMapPoi : jxMarkersOnMap) {
            eachOnMapPoi.setVisible(true);
        }
    }
    
    public void setOnMapPoisHided() {
        for (Marker eachOnMapPoi : jxMarkersOnMap) {
            eachOnMapPoi.setVisible(false);
        }
    }
    
    public ArrayList<LatLng> getCrossRoads() {
        return crossRoads;
    }

    public void setCrossRoads(ArrayList<LatLng> crossRoads) {
        this.crossRoads = crossRoads;
    }

    public ArrayList<MyMarker> getAllMarkers() {
        return allMarkers;
    }

    public void setAllMarkers(ArrayList<MyMarker> allMarkers) {
        this.allMarkers = allMarkers;
    }

    public MyMarker getOneMarkerByMarkerID(int markerId) {
        MyMarker resultMarker = new MyMarker();
        boolean found = false;
        for (int n = 0; n < this.allMarkers.size() && !found; ++n) {
            if (allMarkers.get(n).getMarkerID() == markerId) {
                found = true;
                resultMarker = allMarkers.get(n);
            }
        }
        return resultMarker;
    }
}
