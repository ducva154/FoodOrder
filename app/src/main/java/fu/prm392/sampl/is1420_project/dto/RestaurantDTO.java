package fu.prm392.sampl.is1420_project.dto;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

public class RestaurantDTO implements Serializable {
    private String restaurantID;
    private String name;
    private String location;
    private GeoPoint geoPoint;
    private String geoHash;
    private String image;
    private double rate;
    private String status;

    public RestaurantDTO() {
    }

    public RestaurantDTO(String restaurantID, String name, String location, GeoPoint geoPoint, String geoHash, String image, double rate, String status) {
        this.restaurantID = restaurantID;
        this.name = name;
        this.location = location;
        this.geoPoint = geoPoint;
        this.geoHash = geoHash;
        this.image = image;
        this.rate = rate;
        this.status = status;
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(String restaurantID) {
        this.restaurantID = restaurantID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public void setGeoHash(String geoHash) {
        this.geoHash = geoHash;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "RestaurantDTO{" +
                "restaurantID='" + restaurantID + '\'' +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", geoPoint=" + geoPoint +
                ", geoHash='" + geoHash + '\'' +
                ", image='" + image + '\'' +
                ", rate=" + rate +
                ", status='" + status + '\'' +
                '}';
    }
}
