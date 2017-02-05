package mobi.plantare;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import mobi.plantare.fragments.GardenMapFragment;
import mobi.plantare.fragments.SocialFragment;
import mobi.plantare.fragments.UserFragment;
import mobi.plantare.model.Plant;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FacebookCallback<Sharer.Result> {

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Initialize first fragment, maybe use Garden
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_main, new UserFragment())
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void shareOnFacebook() {

        Plant mPlant = PlantareApp.getInstance().getLastPlant();

        if (mPlant == null) {
            Log.e("Plantare", "Last plant returns null");
        } else if (mPlant.getPhoto() == null) {
            Log.e("Plantare", "Don't have a plant photo to share");
        } else if (ShareDialog.canShow(SharePhotoContent.class)) {

            byte[] byteArray = Base64.decode(mPlant.getPhoto(), Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(bmp)
                    .build();

            ShareHashtag.Builder hashTagBuiler = new ShareHashtag.Builder();
            hashTagBuiler.setHashtag("#plantare");
            ShareHashtag hashTag = hashTagBuiler.build();

            SharePhotoContent content = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .setShareHashtag(hashTag)
                    .build();

            shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
        } else {
            Log.e("Plantare", "It's not possible share a photo on Facebook.\n" +
                    "Verify if application have the necessary permission to perform this action.");
        }
    }

//    public Fragment getActiveFragment(ViewPager container, int position) {
//        String name = makeFragmentName(container.getId(), position);
//        return getSupportFragmentManager().findFragmentByTag(name);
//    }
//
//    private static String makeFragmentName(int viewId, int index) {
//        return "android:switcher:" + viewId + ":" + index;
//    }

    @Override
    public void onSuccess(Sharer.Result result) {
        Toast.makeText(this, R.string.shared_success, Toast.LENGTH_LONG).show();
//TODO review this code
//        Fragment frag = getActiveFragment(mViewPager, 0);
//
//        if(frag == null){
//            Log.e("Plantare", "frag is null");
//        }else if(frag instanceof GardenMapFragment){
//            ((GardenMapFragment) frag).dismissDialog();
//        }
    }

    @Override
    public void onCancel() {
        Log.e("Plantare", "The sharing on Facebook was cancelled");
    }

    @Override
    public void onError(FacebookException error) {

        if (error.getMessage().contains("facebookErrorCode: 190") || error.getMessage().contains("facebookErrorCode: 2500")) {
            Toast.makeText(this, R.string.no_facebook_account_conected, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, R.string.share_error, Toast.LENGTH_LONG).show();
        }

        Log.e("Plantare", "FacebookException on share action: " + error.getMessage());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_plantation) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_main, new GardenMapFragment())
                    .commit();

        } else if (id == R.id.nav_social) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_main, new SocialFragment())
                    .commit();

        } else if (id == R.id.nav_user) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_main, new UserFragment())
                    .commit();
        } else if (id == R.id.nav_share) {
            startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(MainActivity.this)
                    .setType("text/plain")
                    .setText(getString(R.string.play_store_app_url))
                    .getIntent(), getString(R.string.action_share)));
        } else if (id == R.id.nav_info) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}