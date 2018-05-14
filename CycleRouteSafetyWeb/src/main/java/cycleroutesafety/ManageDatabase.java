package cycleroutesafety;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

/*
 * Represents one connection to the Database. It allows several data management 
 * tools to be run, like reading from & writing to DB or modifying existing rows
 * For production use, this would be implemented with user management in place.
 * 
 * @author Andor Horvath
 */
public class ManageDatabase {

    private Connection conn = null;
    private String query = null;
    private ResultSet rs;
    private final String dbName = "cycleroutes";
    private final String dbUser = "ahorvath";
    private final String dbPassword = "dummy";
    private final String dbDomain = ("jdbc:mysql://localhost/" + dbName + "?useLegacyDatetimeCode=false&serverTimezone=Europe/Paris");

    /**
     * Dummy solution for getting the row number of Markers table.
     *
     * @return number of rows in Markers table
     */
    public int countNumberOfMarkers() {
        int counter = 0;
        try {
            conn = DriverManager.getConnection(dbDomain, dbUser, dbPassword);
            query = "SELECT * from markers";
            PreparedStatement stm = conn.prepareStatement(query);
            rs = stm.executeQuery();
            counter = 0;
            rs.beforeFirst();
            while (rs.next()) {
                ++counter;
            }
            System.out.println("## DEBUG   MARKERS tabla sorainak szama: " + counter);
            conn.close();
        } catch (SQLException e) {
            logSQLException(e);
        }
        return counter;
    }

    /**
     * Reading all the Routes in the DB to the returned arrayList of Routes.
     *
     * @return an arrayList of Routes containing all the routes of the DB.
     *
     */
    public ArrayList<Route> readRoutes() {
        ArrayList<Route> allRoutesFromDb = new ArrayList<>();
        try {
            conn = DriverManager.getConnection(dbDomain, dbUser, dbPassword);
            query = "SELECT * from routes";
            PreparedStatement stm = conn.prepareStatement(query);
            rs = stm.executeQuery();
            rs.beforeFirst();
            while (rs.next()) {
                allRoutesFromDb.add(new Route(
                        rs.getInt("routeID"),
                        rs.getString("routeName"),
                        rs.getString("author"),
                        rs.getString("startPoint"),
                        rs.getString("finishPoint"),
                        rs.getInt("routeLength"),
                        rs.getString("lastUpdateTime")
                ));
            }
            conn.close();
            return allRoutesFromDb;
        } catch (SQLException e) {
            logSQLException(e);
        }
        return allRoutesFromDb;
    }

    /**
     * Reading all the pois in the DB to the returned arrayList of Pois.
     *
     * @return an arrayList of Pois containing all the pois of the DB.
     *
     */
    public ArrayList<Poi> readPois() {
        ArrayList<Poi> allPoisFromDb = new ArrayList<>();
        try {

            conn = DriverManager.getConnection(dbDomain, dbUser, dbPassword);
            query = "SELECT * from pois";
            PreparedStatement stm = conn.prepareStatement(query);
            rs = stm.executeQuery();
            rs.beforeFirst();
            while (rs.next()) {
                allPoisFromDb.add(new Poi(
                        rs.getInt("poiID"),
                        rs.getDouble("lat"),
                        rs.getDouble("lng"),
                        rs.getInt("markerID")
                ));
            }
            conn.close();
            return allPoisFromDb;
        } catch (SQLException e) {
            logSQLException(e);
        }
        return allPoisFromDb;
    }

    /**
     * Reading all the markers in the DB to the returned arrayList of Markers.
     *
     * @return an arrayList of Markers containing all the markers of the DB.
     *
     */
    public ArrayList<MyMarker> readMarkers() {
        ArrayList<MyMarker> allMarkersFromDb = new ArrayList<>();
        try {
            conn = DriverManager.getConnection(dbDomain, dbUser, dbPassword);
            query = "SELECT * from markers";
            PreparedStatement stm = conn.prepareStatement(query);
            rs = stm.executeQuery();
            rs.beforeFirst();
            while (rs.next()) {
                allMarkersFromDb.add(new MyMarker(
                        rs.getInt("markerID"),
                        rs.getString("description"),
                        rs.getString("markerType")
                ));
            }
            conn.close();
            return allMarkersFromDb;
        } catch (SQLException e) {
            logSQLException(e);
        }
        return allMarkersFromDb;
    }

    /**
     * Reads one Route from the DB, if the given routeID is exists in the DB.
     * Otherwise it returns a new, empty Route object that is just constructed.
     *
     * @param routeID
     * @return the route from the DB that has the given ID
     */
    public Route readOneRouteById(int routeID) {
        try {
            conn = DriverManager.getConnection(dbDomain, dbUser, dbPassword);
            query = "SELECT * from routes WHERE routeID = " + routeID;
            PreparedStatement stm = conn.prepareStatement(query);
            rs = stm.executeQuery();
            Route readRoute = new Route(
                    rs.getInt("routeID"),
                    rs.getString("routeName"),
                    rs.getString("author"),
                    rs.getString("startPoint"),
                    rs.getString("finishPoint"),
                    rs.getInt("routeLength"),
                    rs.getString("lastUpdateTime")
            );
            System.out.println(readRoute.toString());
            conn.close();
            return readRoute;
        } catch (SQLException e) {
            logSQLException(e);
        }
        Route emptyRoute = new Route();
        return emptyRoute;
    }

    /**
     * Reads the markerType string from the given markerID's MyMarker from the DB.
     * If SQLexception is happening, it returns an empty string.
     *
     * @param markerID
     * @return markerType String of the given marker
     */
    public String readOneMarkerTypeById(int markerID) {
        try {
            conn = DriverManager.getConnection(dbDomain, dbUser, dbPassword);
            query = "SELECT * from markers WHERE markerID = " + markerID;
            PreparedStatement stm = conn.prepareStatement(query);
            rs = stm.executeQuery();
            String readMarker = rs.getString("markerType");
            conn.close();
            return readMarker;
        } catch (SQLException e) {
            logSQLException(e);
        }
        String myMarker = "";
        return myMarker;
    }

    /**
     * Creates a new Route object according to the parameters, then persists it
     * to the DB with a new ID.
     *
     * @param routeName
     * @param author
     * @param startPoint
     * @param finishPoint
     * @param routeLength
     * @param lastUpdateTime
     * @param plannedRoute
     */
    public void createRoute(
            String routeName,
            String author,
            String startPoint,
            String finishPoint,
            int routeLength,
            String lastUpdateTime,
            boolean plannedRoute) {
        try {
            conn = DriverManager.getConnection(dbDomain, dbUser, dbPassword);
            query = "INSERT INTO routes(routeName, "
                    + "author, "
                    + "startPoint, "
                    + "finishPoint, "
                    + "routeLength, "
                    + "lastUpdateTime) "
                    + "VALUES('" + routeName + "', "
                    + "'" + author + "', "
                    + "'" + startPoint + "', "
                    + "'" + finishPoint + "', "
                    + "'" + routeLength + "', "
                    + "'" + lastUpdateTime + "')";
            PreparedStatement stm = conn.prepareStatement(query);
            stm.executeUpdate();
            query = "select last_insert_id() as last_id from routes";
            stm = conn.prepareStatement(query);
            rs = stm.executeQuery();
            System.out.println(rs.getString("last_id"));
            conn.close();
        } catch (SQLException e) {
            logSQLException(e);
        }
    }

    /**
     * Creates a new MyMarker object according to the parameters, then persists it
 to the DB with a new ID.
     *
     * @param description
     * @param markerType
     */
    public void createMarker(
            String description,
            String markerType) {
        try {
            conn = DriverManager.getConnection(dbDomain, dbUser, dbPassword);
            query = "INSERT INTO markers(description, "
                    + "markerType) "
                    + "VALUES('" + description + "', "
                    + "'" + markerType + "')";
            PreparedStatement stm = conn.prepareStatement(query);
            stm.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            logSQLException(e);
        }
    }

    /**
     * Inserts all the POIs (with the newly created ones at the map) to the DB.
     * The input arrayList should contain all the POIs that is present (not just
     * showed) in the map.
     * 
     * @param newPois
     */
    public void refreshPois(ArrayList<Poi> newPois) {
        try {
            conn = DriverManager.getConnection(dbDomain, dbUser, dbPassword);
            query = "TRUNCATE pois";
            PreparedStatement stm = conn.prepareStatement(query);
            stm.executeUpdate();
            for (int n = 0; n < newPois.size(); ++n) {
                query = "INSERT INTO pois(lat, "
                        + "lng, "
                        + "markerID) "
                        + "VALUES('" + newPois.get(n).getLat() + "', "
                        + "'" + newPois.get(n).getLng() + "', "
                        + "'" + newPois.get(n).getMarkerID() + "')";
                stm = conn.prepareStatement(query);
                stm.executeUpdate();
            }
            conn.close();
        } catch (SQLException e) {
            logSQLException(e);
        }
    }

    /**
     * Modify an existing Route's DB entry with the values in it's parameters.
     * 
     * @param routeID
     * @param routeName
     * @param author
     * @param startPoint
     * @param finishPoint
     * @param routeLength
     * @param lastUpdateTime
     * @param plannedRoute
     */
    public void modifyRoute(int routeID,
            String routeName,
            String author,
            String startPoint,
            String finishPoint,
            int routeLength,
            String lastUpdateTime,
            boolean plannedRoute) {
        try {
            conn = DriverManager.getConnection(dbDomain, dbUser, dbPassword);
            query = "UPDATE routes SET "
                    + "routeName='" + routeName + "', "
                    + "author='" + author + "', "
                    + "startPoint='" + startPoint + "', "
                    + "finishPoint='" + finishPoint + "', "
                    + "routeLength='" + routeLength + "', "
                    + "lastUpdateTime='" + lastUpdateTime + "' "
                    + "WHERE routeID=" + routeID + "";
            PreparedStatement stm = conn.prepareStatement(query);
            System.out.println(query);
            if (stm.executeUpdate() != 1) {
                System.out.println("Már van ilyen érték az adatbázisban, így"
                        + " nem tudom menteni az adatbázisba.\nKérem készítsen"
                        + " másik útvonalat, ami különböző.");
            }
            conn.close();
        } catch (SQLException e) {
            logSQLException(e);
        }
    }

    /**
     * Modify an existing Poi's DB entry with the values in it's parameters.
     * 
     * @param poiID
     * @param lat
     * @param lng
     * @param markerID
     */
    public void modifyPoi(int poiID,
            double lat,
            double lng,
            int markerID) {
        try {
            conn = DriverManager.getConnection(dbDomain, dbUser, dbPassword);
            query = "UPDATE pois SET "
                    + "routeID='" + poiID + "', "
                    + "lat='" + lat + "', "
                    + "lat='" + lng + "', "
                    + "markerID='" + markerID + "', "
                    + "WHERE poiID=" + poiID + "";
            PreparedStatement stm = conn.prepareStatement(query);
            stm.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            logSQLException(e);
        }
    }

    /**
     * Deletes an entry from the DB, where the route ID is equal to the one 
     * given as the parameter.
     * 
     * @param routeID
     */
    public void deleteRoute(int routeID) {
        try {
            conn = DriverManager.getConnection(dbDomain, dbUser, dbPassword);
            query = "DELETE FROM routes "
                    + "WHERE routeID = " + routeID + "";
            PreparedStatement stm = conn.prepareStatement(query);
            stm.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            logSQLException(e);
        }
    }

    /**
     * Deletes an entry from the DB, where the poi ID is equal to the one 
     * given as the parameter.
     * 
     * @param poiID
     */
    public void deletePoi(int poiID) {
        try {
            conn = DriverManager.getConnection(dbDomain, dbUser, dbPassword);
            query = "DELETE FROM pois"
                    + "WHERE poiID = " + poiID + "";
            PreparedStatement stm = conn.prepareStatement(query);
            stm.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            logSQLException(e);
        }
    }

    /**
     * Deletes an entry from the DB, where the marker ID is equal to the one 
     * given as the parameter.
     * 
     * @param markerId
     */
    public void deleteMarker(int markerId) {
        try {
            conn = DriverManager.getConnection(dbDomain, dbUser, dbPassword);
            query = "DELETE FROM markers"
                    + "WHERE markerId = " + markerId + "";
            PreparedStatement stm = conn.prepareStatement(query);
            stm.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            logSQLException(e);
        }
    }

    /**
     * get the last row's markerID value
     * 
     * @return 
     */
    public int getLastRowMarkerId() {
        int readMarkerId = 0;
        try {
            conn = DriverManager.getConnection(dbDomain, dbUser, dbPassword);
            query = "SELECT * FROM markers"
                    + "ORDER BY markerID DESC LIMIT 1";
            PreparedStatement stm = conn.prepareStatement(query);
            rs = stm.executeQuery();
            readMarkerId = rs.getInt("markerID");
            conn.close();
        } catch (SQLException e) {
            logSQLException(e);
        }
        return readMarkerId;
    }
    
    /**
     * prints an error message to standard out for debugging purposes. The goal
     * of the function is to give a little more context than the usually thrown
     * sqlException message.
     *
     * @param exceptionCatched
     */
    protected void logSQLException(SQLException exceptionCatched) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        System.out.println(today + " " + now + " FATAL  Database related error. SQLException is: " + exceptionCatched.getMessage());
        // throw FATAL, let it die...
    }
}
