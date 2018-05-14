package cycleroutesafety;

/**
 * A Point Of Interest object, generally to display interesting properties about
 * the given place "close" to the planned route like hazards, usable free
 * air-pumps, drinkwater fountains, etc.
 * It has a
 * - poiID for DB purposes
 * - destination to store where it is on the map, with an address
 * - markerID to connect to a Marker object that will be represented on the map
 * by this POI object.
 * 
 * @author Andor Horvath
 */
public class Poi {

    private int poiID;
    private double lat;
    private double lng;
    private int markerID;

    public Poi() {

    }

    public Poi(int poiID,
            double lat,
            double lng,
            int markerID) {
        this.poiID = poiID;
        this.lat = lat;
        this.lng = lng;
        this.markerID = markerID;
    }

    @Override
    public String toString() {
        return "[" + this.poiID + "; " + this.lat + "; " + this.lng + "; " + this.markerID + "]";
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setPoiType(int markerID) {
        this.markerID = markerID;
    }

    public int getPoiID() {
        return this.poiID;
    }

    public double getLat() {
        return this.lat;
    }

    public double getLng() {
        return this.lng;
    }

    public int getMarkerID() {
        return this.markerID;
    }
}
