package mobi.plantare.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;

/**
 * Created by gabeira@gmail.com on 6/23/16.
 */
public class Plant implements Serializable, ClusterItem {

    private String id;
    private double latitude;
    private double longitude;
    private String gardenerId;
    private String gardenerName;
    private String name;
    private String type;
    private long when;
    private String photo;

    public Plant() {
    }

    public Plant(double lat, double lng) {
        latitude = lat;
        longitude = lng;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

//    public LatLng getWhere() {
//        return new LatLng(latitude, longitude);
//    }

    public String getGardenerId() {
        return gardenerId;
    }

    public void setGardenerId(String gardenerId) {
        this.gardenerId = gardenerId;
    }


    public String getGardenerName() {
        return gardenerName;
    }

    public void setGardenerName(String gardenerName) {
        this.gardenerName = gardenerName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getWhen() {
        return when;
    }

    public void setWhen(long when) {
        this.when = when;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
