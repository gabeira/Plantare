package mobi.plantare;

import android.app.Application;

import mobi.plantare.model.Plant;

/**
 * Created by gabriel on 7/1/15.
 */
public class PlantareApp extends Application {

    private static PlantareApp mAplicationInstance;
    private Plant plant;

    @Override
    public void onCreate() {
        super.onCreate();
        setInstance(this);
    }

    private static void setInstance(PlantareApp mPlantareApp) {
        mAplicationInstance = mPlantareApp;
    }

    public static PlantareApp getInstance() {
        return mAplicationInstance;
    }

    public void setLastPlant(final Plant plant) {
        this.plant = plant;
    }

    public Plant getLastPlant() {
        return plant;
    }


}
