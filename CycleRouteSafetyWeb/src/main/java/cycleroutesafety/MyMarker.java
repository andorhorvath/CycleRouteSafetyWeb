package cycleroutesafety;

/**
 * A MyMarker object generally showing a location on the map, representing a POI
 that is connected to it. A MyMarker be put to the map, removed, and onClick it
 shows some general information about the given POI that it represents.
 
 It has a
 - markerID for DB purposes
 - description that tells some information about the represented POI
 - markerType that tells info about the underlying POI like if it is a hazard,
 or an interesting place like a free-air pump or drinking water availability.
 * 
 * @author Andor Horvath
 */
public class MyMarker {

    private int markerID;
    private String description;
    private String markerType;

    public MyMarker() {

    }

    public MyMarker(int markerID,
            String description,
            String markerType) {
        this.markerID = markerID;
        this.description = description;
        // markerType contains the icon used for the marker
        this.markerType = markerType;
    }

    @Override
    public String toString() {
        return "[" + this.markerID + "; " + this.description + "; " + this.markerType + "]";
    }

    public void setMarkerDescription(String description) {
        this.description = description;
    }

    public void seMarkerType(String markerType) {
        this.markerType = markerType;
    }

    public int getMarkerID() {
        return this.markerID;
    }

    public String getMarkerDescription() {
        return this.description;
    }

    public String getMarkerType() {
        return this.markerType;
    }
}
