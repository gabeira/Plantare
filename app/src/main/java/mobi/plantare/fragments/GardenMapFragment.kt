package mobi.plantare.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crash.FirebaseCrash
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import mobi.plantare.PlantActivity
import mobi.plantare.R
import mobi.plantare.model.Plant
import java.text.SimpleDateFormat
import java.util.*

/**
 * A placeholder fragment containing a simple view.
 * Created by gabriel on 7/1/15.
 */
class GardenMapFragment : Fragment(), LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private var mMap: GoogleMap? = null
    private var myLocationToPlant: LatLng? = null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var mLocationRequest: LocationRequest? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mClusterManager: ClusterManager<Plant>? = null
    private var mShareDialog: AlertDialog? = null

    private val isNecessaryToExplainToUserWhyTheLocationPermissionIsNecessary: Boolean
        get() = ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                Manifest.permission.ACCESS_COARSE_LOCATION)

    private val isPermissionToLocationAlreadyGranted: Boolean
        get() = ActivityCompat.checkSelfPermission(context!!, Manifest.permission
                .ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context!!, Manifest.permission
                .ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private val lastKnowLocation: Location?
        get() {
            var currentLocation: Location? = null
            try {
                currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
            } catch (e: SecurityException) {
                Log.e(TAG, " Permission to Location is not Granted")
            }

            return currentLocation
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context!!)

        val plant = rootView.findViewById<View>(R.id.plante) as FloatingActionButton
        plant.setOnClickListener { plant() }

        handleMap()
        createLocationRequest()
        handleGoogleAPI()
        return rootView
    }

    private fun handleGoogleAPI() {
        mGoogleApiClient = GoogleApiClient.Builder(context!!)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
        mGoogleApiClient!!.connect()
    }

    private fun createMap() {
        (activity!!.fragmentManager.findFragmentById(R.id.map) as MapFragment)
                .getMapAsync { googleMap ->
                    mMap = googleMap
                    setMapStyle()
                    initializeMap()
                }
    }

    private fun setMapStyle() {
        //Set the Map Style from https://mapstyle.withgoogle.com
        val mapStyleOptions = MapStyleOptions.loadRawResourceStyle(activity!!, R.raw.map_style)
        mMap!!.setMapStyle(mapStyleOptions)
    }

    fun initializeMap() {
        mMap!!.uiSettings.isZoomControlsEnabled = true
        mMap!!.uiSettings.isCompassEnabled = true

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "123")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Map Started")
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image")
        mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    override fun onConnected(bundle: Bundle?) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient!!.isConnected)
        //TODO: 2 check if here is the best place to call this method
        checkPermissions()
    }

    override fun onConnectionSuspended(i: Int) {
        Log.w(TAG, " onConnectionSuspended." + i)
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.w(TAG, " onConnectionFailed." + connectionResult.toString())
    }

    fun plant() {

        if (myLocationToPlant != null) {
            val intent = Intent(context, PlantActivity::class.java)
            intent.putExtra(LOCATION_TO_PLANT, myLocationToPlant)
            startActivityForResult(intent, REQUEST_PLANT)
        } else {
            val error = "Não foi possivel obter a sua localização para plantar..."
            Toast.makeText(activity, error, Toast.LENGTH_LONG).show()
            FirebaseCrash.log(error)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            val f = activity!!.fragmentManager.findFragmentById(R.id.map) as MapFragment
            if (f != null)
                activity!!.fragmentManager.beginTransaction().remove(f).commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun getPlants() {

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(PLANTS_DATASET)

        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (!dataSnapshot.exists() || dataSnapshot.value == null) {
                    Log.e(TAG, "Failed to read value.")
                }
                Log.e(TAG, "Size: " + dataSnapshot.childrenCount)
                for (dataSnap in dataSnapshot.children) {
                    val plant = dataSnap.getValue(Plant::class.java)
                    mClusterManager!!.addItem(plant)
                    Log.e(TAG, "Added Plant : " + plant!!.name!!)
                }
                mClusterManager!!.cluster()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    protected fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = INTERVAL
        mLocationRequest!!.fastestInterval = FASTEST_INTERVAL
        mLocationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    }

    protected fun checkToStartLocationUpdates() {
        if (mGoogleApiClient!!.isConnected && isPermissionToLocationAlreadyGranted) {
            startLocationUpdates()
            Log.d(TAG, "Location update started ..............: ")
        }
    }

    protected fun stopLocationUpdates() {
        if (mGoogleApiClient!!.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this@GardenMapFragment)
            Log.d(TAG, "Location update stopped ..............: ")
        }
    }

    /*
     * Called when the Activity becomes visible.
     */
    override fun onStart() {
        super.onStart()
        // Connect the client.
        mGoogleApiClient!!.connect()
    }

    /*
     * Called when the Activity is no longer visible.
     */
    override fun onStop() {
        // Disconnecting the client invalidates it.
        mGoogleApiClient!!.disconnect()
        super.onStop()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        checkToStartLocationUpdates()
        Log.d(TAG, "Location update resumed .....................")
    }

    override fun onLocationChanged(location: Location) {
        myLocationToPlant = LatLng(location.latitude, location.longitude)
    }

    fun checkPermissions() {
        if (!isPermissionToLocationAlreadyGranted) {
            requestToUserLocationPermission()
        } else {
            locationPermissionsGranted()
        }
    }

    private fun locationPermissionsGranted() {
        checkToStartLocationUpdates()
        //TODO: 3 check if here is the best place to call this method
        prepareMapToPlant()
    }

    private fun prepareMapToPlant() {
        if (null != mMap) {
            setUpMapWithLocation()
            setUpMapClusterManager()
            getPlants()
        }
    }

    private fun handleMap() {
        if (mMap == null) {
            createMap()
        } else {
            initializeMap()
        }
    }

    private fun requestToUserLocationPermission() {
        if (isNecessaryToExplainToUserWhyTheLocationPermissionIsNecessary) {
            showExplanationToUserAsynchronously()
        } else {
            requestPermissionsWithoutExplanation()
        }
    }

    private fun requestPermissionsWithoutExplanation() {
        this.requestPermissions(
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION)
    }


    //TODO: 1 Implement this method
    private fun showExplanationToUserAsynchronously() {
        // Show an explanation to the user *asynchronously* -- don't block
        // this thread waiting for the user's response! After the user
        // sees the explanation, try again to request the permission.
        //                Snackbar.make(getContext().findViewById(android.R.id.content), "Sem localizacao", Snackbar.LENGTH_INDEFINITE)
        //                        .setAction(android.R.string.ok, new View.OnClickListener() {
        //                            @Override
        //                            public void onClick(View view) {
        //                                ActivityCompat.requestPermissions(this,
        //                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
        //                                        MY_PERMISSIONS_REQUEST_LOCATION);
        //                            }
        //                        }).show();
    }

    private fun geLastKnowLatLng(): LatLng? {
        var latLng: LatLng? = null
        if (isPermissionToLocationAlreadyGranted) {
            val currentLocation = lastKnowLocation
            if (currentLocation != null) {
                latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
            }
        }
        return latLng
    }

    private fun setMyLocationEnabledOnMap() {
        try {
            mMap!!.isMyLocationEnabled = true
        } catch (e: SecurityException) {
            Log.e(TAG, " Permission to Location is not Granted")
        }

    }

    private fun startLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this)
        } catch (e: SecurityException) {
            Log.e(TAG, " Permission to Location is not Granted")
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Log.d(TAG, "onRequestPermissionsResult")
        when (requestCode) {

            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                val notNull = grantResults != null && grantResults != null
                if (notNull && grantResults.size < 1 || !isPermissionToLocationAlreadyGranted) {
                    Toast.makeText(context, R.string.unknown_location, Toast.LENGTH_LONG).show()
                } else {
                    locationPermissionsGranted()
                }
            }
        }
    }

    private fun setUpMapWithLocation() {
        myLocationToPlant = geLastKnowLatLng()
        setMyLocationEnabledOnMap()
        mMap!!.uiSettings.isMyLocationButtonEnabled = true
        if (myLocationToPlant != null) {
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocationToPlant, 16f))
        }
    }

    private fun setUpMapClusterManager() {
        mClusterManager = ClusterManager(activity!!, mMap)
        mClusterManager!!.renderer = PlantRenderer()
        mMap!!.setOnCameraIdleListener(mClusterManager)
        mMap!!.setOnMarkerClickListener(mClusterManager)
        mMap!!.setOnInfoWindowClickListener(mClusterManager)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            //plant = (Plant) data.getSerializableExtra(PLANTED_PLANT);
//            val plant = PlantareApp.getInstance().getLastPlant()
//            showConfirmPlantDialog(activity!!, plant)
            //Toast.makeText(getContext(), "Obrigado por plantar " + planted.getName() + " a cidade agradece.", Toast.LENGTH_LONG).show();
        }
    }

    private fun showConfirmPlantDialog(context: Context, plant: Plant) {

        val inflater = (context as Activity).layoutInflater
        val view = inflater.inflate(R.layout.dialog_confirm_plant, null)

        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.tx_thanks_to_plant_title))
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setView(view)

        mShareDialog = builder.create()
        mShareDialog!!.show()

        val message = view.findViewById<View>(R.id.tx_plant_name) as TextView
        message.text = plant.name

        view.findViewById<View>(R.id.bt_share).setOnClickListener {
            //            (activity as MainActivity).shareOnFacebook()
        }

        view.findViewById<View>(R.id.bt_not_now).setOnClickListener { mShareDialog!!.dismiss() }
    }

    fun dismissDialog() {
        if (mShareDialog == null) {
            Log.e("Plantare", "Share mShareDialog is null")
        } else if (mShareDialog!!.isShowing) {
            mShareDialog!!.dismiss()
        } else {
            Log.e("Plantare", "Share mShareDialog is not showing")
        }
    }

    private inner class PlantRenderer internal constructor() : DefaultClusterRenderer<Plant>(context, mMap, mClusterManager) {

        override fun onBeforeClusterItemRendered(plant: Plant?, markerOptions: MarkerOptions?) {
            val df = SimpleDateFormat.getDateInstance()
            df.timeZone = TimeZone.getDefault()
            val plantedDate = df.format(plant!!.`when`)
            var gardener: String? = ""
            if (plant.gardenerName != null)
                gardener = plant.gardenerName

            markerOptions!!.title("" + plant.name!!)
                    .snippet(gardener + " plantou " + plant.type + " em " + plantedDate)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.iplanted2))
        }

        override fun shouldRenderAsCluster(cluster: Cluster<Plant>): Boolean {
            return cluster.size > 1
        }
    }

    companion object {

        val PLANTS_DATASET = "plants"
        val REQUEST_PLANT = 10
        val LOCATION_TO_PLANT = "location_to_plant"
        val MY_PERMISSIONS_REQUEST_LOCATION = 0

        private val TAG = "Plants"
        private val INTERVAL = (1000 * 60).toLong()
        private val FASTEST_INTERVAL = (1000 * 10).toLong()

        @JvmStatic
        fun newInstance() = GardenMapFragment()
    }
}