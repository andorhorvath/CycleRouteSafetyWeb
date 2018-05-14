package cycleroutesafety;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import filechooser.components.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    //public static final int MIN_ZOOM = 0;
    //public static final int MAX_ZOOM = 21;
    //public static final int ZOOM_VALUE = 10; //map.html value
    //TODO: delete this public static Route[] allRoutes;
    public static ArrayList<Route> allRoutes;
    public static Color off = Color.lightGray;
    public static Color on = Color.white;
    public static File tempStaticMarkerFileStore = null;
    public static final double radiusOfClose = 0.01;
    
    public static void main(String[] args) {

        ManageDatabase manageDatabase = new ManageDatabase();

        JFrame frame = new JFrame();
        DirectionsGeocoder.loadAndRegisterCustomFonts();
        final DirectionsGeocoder map = new DirectionsGeocoder();
        frame.add(map, BorderLayout.CENTER);

        JTextArea messageBar = new JTextArea();
        //messageBar.setFont(Font.decode("MS Gothic"));

        DirectionsGeocoder.addText(messageBar, "Betöltés kész");

        JPanel messagePanel = new JPanel();
        messagePanel.add(messageBar);

        final JTextField routeNameBar = new JTextField();
        routeNameBar.setEditable(false);
        String text = "Nincs útvonal betöltve...";//map.textFromTo(map.getFromField().getText(), map.getToField().getText());
        routeNameBar.setText(text);

        JPanel routeNamePane = new JPanel(new FlowLayout());
        routeNamePane.add(new JLabel(" Betöltött útvonal: "));
        routeNamePane.add(routeNameBar);

        // building the Right panel to manage routes
        JButton createRoute = new JButton("Mentés új útvonalként");
        createRoute.setPreferredSize(new Dimension(150, 30));
        createRoute.setBorder(null);
        createRoute.setBackground(new Color(66, 133, 244));
        createRoute.setForeground(Color.WHITE);
        createRoute.setOpaque(true);

        JButton modifyRoute = new JButton("Módosítások mentése");
        modifyRoute.setPreferredSize(new Dimension(150, 30));
        modifyRoute.setBorder(null);
        modifyRoute.setForeground(Color.WHITE);
        modifyRoute.setBackground(new Color(66, 133, 244));
        modifyRoute.setOpaque(true);
        allRoutes = manageDatabase.readRoutes();

        Choice allRoutesList = new Choice();
        allRoutesList.setPreferredSize(new Dimension(200, 60));
        allRoutesList.setForeground(Color.WHITE);
        allRoutesList.setBackground(new Color(66, 133, 244));
        allRoutesList.add("----- Válassz! -----");
        // adding all route's name to the choice options
        for (int n = 0; n < allRoutes.size(); ++n) {
            allRoutesList.add(allRoutes.get(n).getRouteName());
            //TODO: possible optimization: read only the names from db just here
        }

        JButton openRoute = new JButton("Útvonal betöltése");
        openRoute.setPreferredSize(new Dimension(150, 30));
        openRoute.setBorder(null);
        openRoute.setForeground(Color.WHITE);
        openRoute.setBackground(new Color(66, 133, 244));
        openRoute.setOpaque(true);

        JButton deleteRoute = new JButton("Útvonal törlése");
        deleteRoute.setPreferredSize(new Dimension(150, 30));
        deleteRoute.setBorder(null);
        deleteRoute.setForeground(Color.WHITE);
        deleteRoute.setBackground(new Color(66, 133, 244));
        deleteRoute.setOpaque(true);

        // adding ActionListeners to route management JButtons
        createRoute.addActionListener((ActionEvent ae) -> {
            //persist currently displayed route to DB
            String routeSavingName = JOptionPane.showInputDialog(
                "Kérem adja meg milyen néven mentse az útvonalat: ");
            map.createRoute(messageBar, routeSavingName);
            routeNameBar.setText(routeSavingName);
            //re-read the DB for routes to update the program
            allRoutes = manageDatabase.readRoutes();
            allRoutesList.removeAll();
            allRoutesList.add("----- Válassz! -----");
            for (int n = 0; n < allRoutes.size(); ++n) {
                allRoutesList.add(allRoutes.get(n).getRouteName());
            }
            //chosing the last element
            allRoutesList.select(allRoutesList.getItem(allRoutesList.getItemCount() - 1));
        });

        modifyRoute.addActionListener((ActionEvent ae) -> {
            int selectedIndexInChoise = allRoutesList.getSelectedIndex();
            if (selectedIndexInChoise > 0) {
                map.modifyRoute(allRoutes.get(selectedIndexInChoise - 1), messageBar);
                allRoutes = manageDatabase.readRoutes();
                allRoutesList.removeAll();
                allRoutesList.add("----- Válassz! -----");
                for (int n = 0; n < allRoutes.size(); ++n) {
                    allRoutesList.add(allRoutes.get(n).getRouteName());
                }
                allRoutesList.select(selectedIndexInChoise);
                routeNameBar.setText(allRoutesList.getSelectedItem());
            }
        });

        openRoute.addActionListener((ActionEvent ae) -> {
            int selectedIndexInChoise = allRoutesList.getSelectedIndex();
            if (selectedIndexInChoise > 0) {
                routeNameBar.setText("");
                map.openRoute(allRoutes.get(selectedIndexInChoise - 1), messageBar);
                routeNameBar.setText(allRoutesList.getSelectedItem());
                // when first starting the program, it does not have any route
                // when loading a route, we need to init the nearPois set
                map.nearPois.clear();
                map.nearPois.addAll(map.computeNearPois(radiusOfClose));
            } else {
                DirectionsGeocoder.addText(messageBar, "Nincs mit megnyitni");
                JOptionPane.showMessageDialog(null, "Nincs mit megnyitni!");
                modifyRoute.setEnabled(false);
                routeNameBar.setText("");
            }
        });

        deleteRoute.addActionListener((ActionEvent ae) -> {
            // reading which route is displayed currently to delete that
            int selectedIndexInChoise = allRoutesList.getSelectedIndex();
            if (selectedIndexInChoise > 0) {
                map.deleteRoute(allRoutes.get(selectedIndexInChoise - 1), messageBar);
            } else {
                String statusText = "Nincs törlésre kijelölt elem.";
                DirectionsGeocoder.addText(messageBar, statusText);
                JOptionPane.showMessageDialog(null, statusText);
            }
            // re-reading the data from the DB
            allRoutes = manageDatabase.readRoutes();
            allRoutesList.removeAll();
            allRoutesList.add("----- Válassz! -----");
                for (selectedIndexInChoise = 0; selectedIndexInChoise < allRoutes.size(); ++selectedIndexInChoise) {
                    allRoutesList.add(allRoutes.get(selectedIndexInChoise).getRouteName());
                }
        });

        /*clearRoute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                //az első elem kiválasztása
                allRoutesList.select(allRoutesList.getItem(1));
                routeNameBar.setText("");
            }
        });*/
        
        allRoutesList.addItemListener((ItemEvent e) -> {
            int selectedIndexInChoice = allRoutesList.getSelectedIndex();
            if (selectedIndexInChoice > 0) {
                openRoute.setEnabled(true);
                modifyRoute.setEnabled(true);
                deleteRoute.setEnabled(true);
            } else {
                openRoute.setEnabled(false);
                modifyRoute.setEnabled(false);
                deleteRoute.setEnabled(false);
            }
        });

        // building the route-related tools' panel
        JPanel routeTools = new JPanel();
        routeTools.add(createRoute);
        routeTools.add(new JLabel("Műveletek meglévő útvonalakkal"));
        routeTools.add(allRoutesList);
        routeTools.add(openRoute);
        routeTools.add(modifyRoute);
        routeTools.add(deleteRoute);
        routeTools.setLayout(new GridLayout(routeTools.getComponentCount(), 1, 1, 1));

        // building the marker-related part of the GUI
        map.allMarkers = manageDatabase.readMarkers();
        JPanel allMarkersList = new JPanel();
        int cols = computeColsForMarkers(manageDatabase.countNumberOfMarkers());
        int rows = computeRowsForMarkers(manageDatabase.countNumberOfMarkers());
        allMarkersList.setLayout(new GridLayout(rows, cols, 1, 1));
        JButton[] markers = new JButton[map.allMarkers.size()];
        for (int n = 0; n < map.allMarkers.size(); ++n) {
            // mm is required to be able to pass the loop 
            // variable "inside" the actionListener, as that is a dynamic
            // content and would not take the loop variable.
            int mm = n;
            // building the buttons for existing markers, with their incon on
            // button. Also connecting the action handler that will make the 
            // buttons be able to PUT markers on the map
            markers[mm] = new JButton();
            markers[mm].setPreferredSize(new Dimension(50, 50));
            ImageIcon icon = new ImageIcon(DirectionsGeocoder.class.getResource(map.allMarkers.get(n).getMarkerType()));
            markers[mm].setIcon(icon);
            markers[mm].setBackground(off);
            markers[mm].setOpaque(true);
            markers[mm].addActionListener((ActionEvent ae) -> {
                if (markers[mm].getBackground().equals(off)) {
                    markers[mm].setBackground(on);
                    markers[mm].setSelected(true);
                    DirectionsGeocoder.addText(messageBar, "Marker lerakása: " + map.allMarkers.get(mm).getMarkerDescription());
                    // if a marker's button is pressed in, PUT-ing marker to map
                    // is possible, and the other buttons should change their
                    // visual
                    for (int j = 0; j < markers.length; ++j) {
                        if (j != mm) {
                            JButton m = markers[j];
                            m.setBackground(off);
                            m.setSelected(false);
                        }
                    }
                    map.actualMarkerType = map.allMarkers.get(mm).getMarkerID();
                } else {
                    markers[mm].setBackground(off);
                    markers[mm].setSelected(false);
                    map.actualMarkerType = -1;
                    DirectionsGeocoder.addText(messageBar, "Nincs kijelölve Marker");
                }
            });
        }
        // adding all markers to the list to be displayed
        
        int preferredHeight = map.allMarkers.size() / 4 + 1;
        Dimension preferredSize = new Dimension();
        preferredSize.setSize(100, preferredHeight);
        allMarkersList.setPreferredSize(preferredSize);
        
        for (int n = 0; n < map.allMarkers.size(); ++n) {
            allMarkersList.add(markers[n]);
        }

        // building the "new marker type creation" management tools
        JPanel addMarker = new JPanel();
        JLabel desc = new JLabel("Leírás");
        JTextField description = new JTextField();
        description.setPreferredSize(new Dimension(50, 15));
        JTextField markerType = new JTextField();
        markerType.setPreferredSize(new Dimension(150, 30));
        markerType.setEditable(false);
        
        final JFileChooser fileChooser = new JFileChooser();
        //Add a custom (images only) file filter and disable the default
        //(Accept All) file filter.
        fileChooser.addChoosableFileFilter(new ImageFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);
        //Add custom icons for file types.
        fileChooser.setFileView(new ImageFileView());
        //Add the preview pane.
        fileChooser.setAccessory(new ImagePreview(fileChooser));
                
        JButton browseMarkerFile = new JButton("Képfájl tallózása…");
        browseMarkerFile.setPreferredSize(new Dimension(100,15));
        browseMarkerFile.addActionListener((ActionEvent ae) -> {
            //file chooser is already have been set up
            //Show it.
            int returnVal = fileChooser.showDialog(frame, "Képfájl tallózása…");
            //Process the results.
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File newMarkerFile = fileChooser.getSelectedFile();
                messageBar.append("\nCsatolom a fájlt: " 
                        + newMarkerFile.getName() + ".\n");
                // addign res/ prefix, because the reader mechanism would not be
                // able to read them just with more string manipulations...
                markerType.setText(newMarkerFile.getName());
                tempStaticMarkerFileStore = newMarkerFile;
            } else {
                messageBar.append("\nA filecsatolásnál cancel történt.\n");
            }
            messageBar.setCaretPosition(messageBar.getDocument().getLength());

            //Reset the file chooser for the next time it's shown.
            fileChooser.setSelectedFile(null);
        });
        
        JButton saveMarker = new JButton("Mentés");
        addMarker.add(desc);
        addMarker.add(description);
        addMarker.add(browseMarkerFile);
        //addMarker.add(markerType);
        addMarker.add(saveMarker);
        addMarker.setLayout(new GridLayout(5, 1));
        addMarker.setVisible(false);

        JButton createMarker = new JButton("Új Markertípus…");
        createMarker.setPreferredSize(new Dimension(150, 15));  //was 30
        createMarker.setBorder(null);
        createMarker.setForeground(Color.WHITE);
        createMarker.setBackground(new Color(66, 133, 244));
        createMarker.setOpaque(true);

        createMarker.addActionListener((ActionEvent ae) -> {
            addMarker.setVisible(true);
        });

        saveMarker.addActionListener((ActionEvent ae) -> {
            allMarkersList.removeAll();
            addMarker.setVisible(false);
            // saving the markerType with the res prefix because markerType
            // gets only the fileName and the goal directory should be constant
            map.allMarkers = map.createMarker(messageBar, description.getText(), "res/" + markerType.getText());
            // copy the browsed file to the res directory for later re-use
            // avoiding false clicks, when no file ia browsed (impossible by 
            // theory the if is not required, but still...)
            if ( tempStaticMarkerFileStore != null ) {
                // copy the file to the res directory
                // getting workingDir/res dir's PATH to pass it to the copy call 
                Path resDirPath = Paths.get(System.getProperty("user.dir"), "src", "cycleroutesafety");                
                Path from = tempStaticMarkerFileStore.toPath();
                Path to = resDirPath.resolve("res/" + tempStaticMarkerFileStore.toPath().getFileName());

                try {
                    //copying the tempStored file to the workingDir/res dir
                    Files.copy(from, to);
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            } // else skip, as there is no file browsed yet...
            
            JButton[] markersArray = new JButton[map.allMarkers.size()];
            for (int n = 0; n < map.allMarkers.size(); ++n) {
                int mm = n;
                markersArray[mm] = new JButton();
                markersArray[mm].setPreferredSize(new Dimension(50, 50));
                ImageIcon icon = new ImageIcon(DirectionsGeocoder.class.getResource(map.allMarkers.get(n).getMarkerType()));
                markersArray[mm].setIcon(icon);
                markersArray[mm].setBackground(off);
                markersArray[mm].setOpaque(true);
                markersArray[mm].addActionListener((ActionEvent ae1) -> {
                    if (markersArray[mm].getBackground().equals(off)) {
                        markersArray[mm].setBackground(on);
                        markersArray[mm].setSelected(true);
                        DirectionsGeocoder.addText(messageBar, "Marker lerakása: " + map.allMarkers.get(mm).getMarkerDescription());
                        for (int j = 0; j < markersArray.length; ++j) {
                            if (j != mm) {
                                JButton markerButton = markersArray[j];
                                markerButton.setBackground(off);
                                markerButton.setSelected(false);
                            }
                        }
                        map.actualMarkerType = map.allMarkers.get(mm).getMarkerID();
                    } else {
                        markersArray[mm].setBackground(off);
                        markersArray[mm].setSelected(false);
                        map.actualMarkerType = -1;
                        DirectionsGeocoder.addText(messageBar, "Nincs kijelölve Marker");
                    }
                });
            }

            for (int i = 0; i < markersArray.length; ++i) {
                allMarkersList.add(markersArray[i]);
            }
            allMarkersList.repaint();
            allMarkersList.revalidate();
            allMarkersList.setVisible(true);
        });

        JButton saveThenLoadPois = new JButton("Poi lista mentése");
        saveThenLoadPois.setPreferredSize(new Dimension(150, 30));
        saveThenLoadPois.setBorder(null);
        saveThenLoadPois.setForeground(Color.WHITE);
        saveThenLoadPois.setBackground(new Color(66, 133, 244));
        saveThenLoadPois.setOpaque(true);

        saveThenLoadPois.addActionListener((ActionEvent ae) -> {
            //8 persistedPois == allPois
            manageDatabase.refreshPois(map.allPois);
            map.persistedPois = map.readPoisFromDb();
            String text1 = "Poik mentésre kerültek";
            DirectionsGeocoder.addText(messageBar, text1);
            JOptionPane.showMessageDialog(null, text1);
        });

        String showAll = "Összes Poi mutatása";
        String hideAll = "Összes Poi elrejtése";
        JButton toggleAllPois = new JButton(hideAll);
        toggleAllPois.setPreferredSize(new Dimension(150, 30));
        toggleAllPois.setBorder(null);
        toggleAllPois.setForeground(Color.WHITE);
        toggleAllPois.setBackground(new Color(66, 133, 244));
        toggleAllPois.setOpaque(true);

        String notJustNear = "Távoli Poik is látszódjanak";
        String justNear = "Csak közeli Poik mutatása";
        JButton showOnlyNear = new JButton(justNear);
        showOnlyNear.setPreferredSize(new Dimension(150, 30));
        showOnlyNear.setBorder(null);
        showOnlyNear.setForeground(Color.WHITE);
        showOnlyNear.setBackground(new Color(66, 133, 244));
        showOnlyNear.setOpaque(true);

        toggleAllPois.addActionListener((ActionEvent ae) -> {
            if (toggleAllPois.getText().equals(showAll)) {
                map.showAllPois();
                toggleAllPois.setText(hideAll);
                // changing the other button's state is important, because 
                // without it, clicking on showOnlyNear would not result changes
                showOnlyNear.setText(justNear);
            } else {
                map.hideAllPois();
                toggleAllPois.setText(showAll);
            }
        });

        showOnlyNear.addActionListener((ActionEvent ae) -> {
            if (showOnlyNear.getText().equals(notJustNear)) {
                // show EVERY POI on map
                map.showAllPois();
                showOnlyNear.setText(justNear);
                // changing the other button is also required here to make it
                // logical. If farer (all) POIs are showed, it's not logical
                // to display "Show all POIs" on the other button
                if (toggleAllPois.getText().equals(showAll)) {
                    toggleAllPois.setText(hideAll);
                }
            } else {
                // show only the close POIs on map, but only if there was change
                // then we need to recount the NearPois
                if (map.arePoisNeedDbPersist(map.allPois, map.persistedPois)) {
                    map.nearPois.clear();
                    map.nearPois.addAll(map.computeNearPois(0.05));
                }
                map.setOnMapPoisHided();
                map.setNearPoisVisible();
                showOnlyNear.setText(notJustNear);
            }
        });
        // building of the MyMarker management JPanel with the previously defined
        // marker-related JButtons
        JPanel poiTools = new JPanel();
        poiTools.add(new JLabel("Markerek kezelése: "));
        poiTools.add(allMarkersList);
        poiTools.add(createMarker);
        poiTools.add(saveThenLoadPois);
        poiTools.add(toggleAllPois);
        poiTools.add(showOnlyNear);
        poiTools.setLayout(new GridLayout(poiTools.getComponentCount(), 1, 1, 1));
        //Dimension poiToolsDimension = new Dimension(5,9);
        //poiTools.setPreferredSize(poiToolsDimension);

        // adding all 3 type of tools to the RIGHT sidepanel    
        JPanel allTools = new JPanel(new GridLayout(2, 1));
        //allTools.add(routeTools);
        allTools.add(poiTools);
        allTools.add(addMarker);
        map.controlPanel.add(routeTools, BorderLayout.SOUTH);
        /*
        final DirectionsGeocoder map = new DirectionsGeocoder();
        frame.add(map, BorderLayout.CENTER);
        
        */
        
        //TODO: don't allow other buttons to be pushed. Redesign needed...
        /*browser.addTitleListener(new TitleListener() {
            @Override
            public void onTitleChange(TitleEvent te) {
                if(browser.getTitle().equals("Google Térkép")){
                    createRoute.setForeground(Color.LIGHT_GRAY);
                    createRoute.setEnabled(false);
                }
                else{
                    createRoute.setForeground(Color.WHITE);
                    createRoute.setEnabled(true);
                }
            }
        });*/

        // building the FRAME from previously defined components
        frame.setBounds(0, 0, 900, 400);
        frame.setPreferredSize(new Dimension(1200, 600));
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        routeNameBar.setPreferredSize(new Dimension(frame.getWidth(), 30));
        frame.add(routeNamePane, BorderLayout.NORTH);
        frame.add(allTools, BorderLayout.EAST);
        messageBar.setLineWrap(true);
        messageBar.setEditable(false);
        JScrollPane scrollV = new JScrollPane(messageBar);
        scrollV.setPreferredSize(new Dimension(900, 60));
        scrollV.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        frame.add(scrollV, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);

        WindowListener exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                if (map.arePoisNeedDbPersist(map.allPois, map.persistedPois)) {
                    Object[] options = {"Igen", "Nem"};
                    int confirm = JOptionPane.showOptionDialog(
                            null, "Kilépés előtt szeretnéd menteni a felhelyezett Poikat?",
                            "Exit Confirmation", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    if (confirm == 0) {
                        manageDatabase.refreshPois(map.allPois);
                    }
                }
                System.exit(0);
            }
        };
        frame.addWindowListener(exitListener);

        frame.pack();
        frame.setVisible(true);

        new OptionsWindow(map, new Dimension(300, 100)) {
            @Override
            public void initContent(JWindow contentWindow) {
                frame.add(map.controlPanel, BorderLayout.WEST);
            }
        };
    }
    
    static public Integer computeColsForMarkers(int numberOfMarkers) {
        return 4;
    }
    
    static public Integer computeRowsForMarkers(int numberOfMarkers) {
        int cols = computeColsForMarkers(numberOfMarkers);
        int rows;
        if (numberOfMarkers % cols == 0) {
            // if the markers will fill all available space of the final line
            // in the GUI, then we allocate
            rows = numberOfMarkers / cols;
        } else {
            rows = numberOfMarkers / cols + 1;
        }
        return rows;
    }
}
