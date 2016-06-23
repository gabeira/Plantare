package mobi.plantare.model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gabeira@gmail.com on 6/23/16.
 */
public class Plant implements Serializable {

    private double latitude;
    private double longitude;
    private Gardener gardener;
    private String name;
    private String type;
    private long when;
    private List pictures;

    public Plant() {
    }

    public Plant(double latitude, double longitude, Gardener gardener, String name, String type, long when, List pictures) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.gardener = gardener;
        this.name = name;
        this.type = type;
        this.when = when;
        this.pictures = pictures;
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

    public LatLng getWhere() {
        return new LatLng(latitude, longitude);
    }

    public Gardener getGardener() {
        return gardener;
    }

    public void setGardener(Gardener gardener) {
        this.gardener = gardener;
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

    public List getPictures() {
        return pictures;
    }

    public void setPictures(List pictures) {
        this.pictures = pictures;
    }
}
