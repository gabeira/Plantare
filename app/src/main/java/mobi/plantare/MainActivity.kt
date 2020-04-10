package mobi.plantare

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import mobi.plantare.fragments.*
import mobi.plantare.view.utility.GlideApp

class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    UserFragment.OnUserLoginListener {

    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth?.currentUser
        if (currentUser != null) {
            updateUserInfo(
                currentUser.displayName!!,
                currentUser.email!!,
                currentUser.photoUrl.toString()
            )
            supportFragmentManager.beginTransaction()
                .replace(R.id.content_main, DonationPlantListFragment.newInstance())
                .commit()
        } else {
            updateUserInfo("", "", "")
            supportFragmentManager.beginTransaction()
                .replace(R.id.content_main, UserFragment.newInstance())
                .commit()
        }

        plante.setOnClickListener {
            val intent = Intent(applicationContext, PlantActivity::class.java)
            //            intent.putExtra(GardenMapFragment.LOCATION_TO_PLANT, myLocationToPlant)
            //TODO Implement add plant without location
            startActivityForResult(intent, GardenMapFragment.REQUEST_PLANT)
        }
    }

    override fun updateUserInfo(name: String, email: String, photoUrl: String) {
        val headerView = nav_view.getHeaderView(0)
        val userPhoto = headerView.findViewById<ImageView>(R.id.user_photo)
        val userName = headerView.findViewById<TextView>(R.id.user_name)
        val userEmail = headerView.findViewById<TextView>(R.id.user_email)
        userName?.text = name
        userEmail?.text = email

        GlideApp.with(applicationContext)
            .load(photoUrl)
            .centerInside()
            .circleCrop()
            .into(userPhoto)
    }

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

        if (id == R.id.nav_plants) {
            val currentUser = auth?.currentUser
            if (currentUser != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.content_main, DonationPlantListFragment.newInstance())
                    .commit()
            } else {
                updateUserInfo("", "", "")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.content_main, UserFragment.newInstance())
                    .commit()
            }

        } else if (id == R.id.nav_map) {
            val currentUser = auth?.currentUser
            if (currentUser != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.content_main, GardenMapFragment.newInstance())
                    .commit()
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.content_main, UserFragment.newInstance())
                    .commit()
            }

        } else if (id == R.id.nav_leader_board) {
            val currentUser = auth?.currentUser
            if (currentUser != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.content_main, LeaderBoardFragment.newInstance())
                    .commit()
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.content_main, UserFragment.newInstance())
                    .commit()
            }

        } else if (id == R.id.nav_my_plants) {
            val currentUser = auth?.currentUser
            if (currentUser != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.content_main, MyPlantsFragment.newInstance())
                    .commit()
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.content_main, UserFragment.newInstance())
                    .commit()
            }

        } else if (id == R.id.nav_share) {
            startActivity(
                Intent.createChooser(
                    ShareCompat.IntentBuilder.from(this@MainActivity)
                        .setType("text/plain")
                        .setText(getString(R.string.play_store_app_url))
                        .intent, getString(R.string.action_share)
                )
            )

        } else if (id == R.id.nav_contributors) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.content_main, ContributorsFragment.newInstance())
                .commit()

        } else if (id == R.id.nav_settings) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.content_main, UserFragment.newInstance())
                .commit()
        }

        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}