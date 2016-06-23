package mobi.plantare;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by gabriel on 7/1/15.
 */
public class PlantareApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }

}
