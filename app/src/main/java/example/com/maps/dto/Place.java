package example.com.maps.dto;

/**
 * Created by Android1 on 7/23/2015.
 */
public class Place {
    private String placeIcon;
    private String placeName;
    private String placeVicinity;
    private double latitude;
    private double longitude;

    public Place() {
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getVicinity() {
        return placeVicinity;
    }

    public void setVicinity(String vicinity) {
        this.placeVicinity = vicinity;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PLACE INFO >>").append("\n");
        sb.append("Place Icon: ").append(placeIcon).append("\n");
        sb.append("Place Name: ").append(placeName).append("\n");
        sb.append("Vicinity: ").append(placeVicinity).append("\n");
        sb.append("Latitude: ").append(latitude).append("\n");
        sb.append("Longitude: ").append(longitude).append("\n");
        sb.append("\n");
        return sb.toString();
    }


    public String getPlaceIcon() {
        return placeIcon;
    }

    public void setPlaceIcon(String placeIcon) {
        this.placeIcon = placeIcon;
    }
}