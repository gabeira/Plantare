package mobi.plantare;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.Parse;
//import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;

/**
 * Created by gabriel on 7/1/15.
 */
public class PlantareApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "D0N2G6GNjBwYha4KhHNrJQRxKJL9XzJpJhkvThta", "qQx2BswQ58DM9CcVFCXCPna4UDNPasX2gV3jeR7p");

        // Record info on Installation Class Table
        ParseInstallation.getCurrentInstallation().saveInBackground();

//        ParsePush.subscribeInBackground("", new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (e == null) {
//                    Log.d("com.parse.push", "Successfully subscribed to the broadcast channel.");
//                } else {
//                    Log.e("com.parse.push", "Failed to subscribe for push", e);
//                }
//            }
//        });

        FacebookSdk.sdkInitialize(getApplicationContext());

//        ParseFacebookUtils.initialize("");

    }

}
