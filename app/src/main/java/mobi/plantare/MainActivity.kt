package mobi.plantare

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ShareCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.facebook.CallbackManager
import com.facebook.FacebookSdk
import com.facebook.share.widget.ShareDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import mobi.plantare.fragments.ContributorsFragment
import mobi.plantare.fragments.GardenMapFragment
import mobi.plantare.fragments.SocialFragment
import mobi.plantare.fragments.UserFragment

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {//}, FacebookCallback<Sharer.Result> {

    private var callbackManager: CallbackManager? = null
    private var shareDialog: ShareDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FacebookSdk.sdkInitialize(applicationContext)
        callbackManager = CallbackManager.Factory.create()
        shareDialog = ShareDialog(this)
//        shareDialog!!.registerCallback(callbackManager!!, this)

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        //Initialize first fragment, maybe use Garden
        supportFragmentManager.beginTransaction()
                .replace(R.id.content_main, SocialFragment())
                .commit()
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
//        super.onActivityResult(requestCode, resultCode, data)
//        callbackManager!!.onActivityResult(requestCode, resultCode, data)
//    }

//    fun shareOnFacebook() {
//
//        val mPlant = PlantareApp.getInstance().getLastPlant()
//
//        if (mPlant == null) {
//            Log.e("Plantare", "Last plant returns null")
//        } else if (mPlant!!.photo == null) {
//            Log.e("Plantare", "Don't have a plant photo to share")
//        } else if (ShareDialog.canShow(SharePhotoContent::class.java)) {
//
//            val byteArray = Base64.decode(mPlant!!.photo, Base64.DEFAULT)
//            val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
//
//            val photo = SharePhoto.Builder()
//                    .setBitmap(bmp)
//                    .build()
//
//            val hashTagBuiler = ShareHashtag.Builder()
//            hashTagBuiler.hashtag = "#plantare"
//            val hashTag = hashTagBuiler.build()
//
//            val content = SharePhotoContent.Builder()
//                    .addPhoto(photo)
//                    .setShareHashtag(hashTag)
//                    .build()
//
//            shareDialog!!.show(content, ShareDialog.Mode.AUTOMATIC)
//        } else {
//            Log.e("Plantare", "It's not possible share a photo on Facebook.\n" + "Verify if application have the necessary permission to perform this action.")
//        }
//    }

    //    public Fragment getActiveFragment(ViewPager container, int position) {
    //        String name = makeFragmentName(container.getId(), position);
    //        return getSupportFragmentManager().findFragmentByTag(name);
    //    }
    //
    //    private static String makeFragmentName(int viewId, int index) {
    //        return "android:switcher:" + viewId + ":" + index;
    //    }

//    override fun onSuccess(result: Sharer.Result) {
//        Toast.makeText(this, R.string.shared_success, Toast.LENGTH_LONG).show()
//        //TODO review this code
//        //        Fragment frag = getActiveFragment(mViewPager, 0);
//        //
//        //        if(frag == null){
//        //            Log.e("Plantare", "frag is null");
//        //        }else if(frag instanceof GardenMapFragment){
//        //            ((GardenMapFragment) frag).dismissDialog();
//        //        }
//    }

//    override fun onCancel() {
//        Log.e("Plantare", "The sharing on Facebook was cancelled")
//    }

//    override fun onError(error: FacebookException) {
//
//        if (error.message.contains("facebookErrorCode: 190") || error.message.contains("facebookErrorCode: 2500")) {
//            Toast.makeText(this, R.string.no_facebook_account_conected, Toast.LENGTH_LONG).show()
//        } else {
//            Toast.makeText(this, R.string.share_error, Toast.LENGTH_LONG).show()
//        }
//
//        Log.e("Plantare", "FacebookException on share action: " + error.message)
//    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_plantation) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.content_main, GardenMapFragment())
                    .commit()

        } else if (id == R.id.nav_social) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.content_main, SocialFragment.newInstance())
                    .commit()

        } else if (id == R.id.nav_user) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.content_main, UserFragment())
                    .commit()
        } else if (id == R.id.nav_share) {
            startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(this@MainActivity)
                    .setType("text/plain")
                    .setText(getString(R.string.play_store_app_url))
                    .intent, getString(R.string.action_share)))
        } else if (id == R.id.nav_contributors) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.content_main, ContributorsFragment.newInstance())
                    .commit()
        } else if (id == R.id.nav_info) {

        }

        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}