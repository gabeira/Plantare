package mobi.plantare.model;

import java.io.Serializable;

/**
 *  Created by gabeira@gmail.com on 6/23/16.
 */
public class Gardener implements Serializable {

    private String id;
    private String name;
    private String email;
    private String facebookUser;
    private long userSince;
    private long lastUse;
    private int numberPlantsPlanted;

    public Gardener() {
    }

    public Gardener(String id,String name, String email, String facebookUser, long userSince) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.facebookUser = facebookUser;
        this.userSince = userSince;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFacebookUser() {
        return facebookUser;
    }

    public void setFacebookUser(String facebookUser) {
        this.facebookUser = facebookUser;
    }

    public long getUserSince() {
        return userSince;
    }

    public void setUserSince(long userSince) {
        this.userSince = userSince;
    }

    public long getLastUse() {
        return lastUse;
    }

    public void setLastUse(long lastUse) {
        this.lastUse = lastUse;
    }

    public int getNumberPlantsPlanted() {
        return numberPlantsPlanted;
    }

    public void setNumberPlantsPlanted(int numberPlantsPlanted) {
        this.numberPlantsPlanted = numberPlantsPlanted;
    }
}
