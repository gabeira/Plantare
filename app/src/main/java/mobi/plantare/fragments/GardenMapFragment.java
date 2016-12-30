package mobi.plantare.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import mobi.plantare.MainActivity;
import mobi.plantare.PlantActivity;
import mobi.plantare.PlantareApp;
import mobi.plantare.R;
import mobi.plantare.model.Plant;

/**
 * A placeholder fragment containing a simple view.
 * Created by gabriel on 7/1/15.
 */
public class GardenMapFragment extends Fragment
        implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap map;
    private LatLng myLocationToPlant;
    private FirebaseAnalytics mFirebaseAnalytics;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    private ClusterManager<Plant> mClusterManager;
    private AlertDialog mShareDialog;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "Plants";
    private static final long INTERVAL = 1000 * 60;
    private static final long FASTEST_INTERVAL = 1000 * 10;
    public static final String PLANTS_DATASET = "plants";
    public static final int REQUEST_PLANT = 10;
    public static final String LOCATION_TO_PLANT = "location_to_plant";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
//    public static GardenMapFragment newInstance(int sectionNumber) {
//        GardenMapFragment fragment = new GardenMapFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//        fragment.setArguments(args);
//        return fragment;
//    }
    public GardenMapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

//        FloatingActionButton fabi = (FloatingActionButton) rootView.findViewById(R.id.fabutton);
        Button plant = (Button) rootView.findViewById(R.id.plante);
        plant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plant();
            }
        });
        if (map == null) {
            ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map))
                    .getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            map = googleMap;

                            //Set the Map Style from https://mapstyle.withgoogle.com
                            MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style);
                            map.setMapStyle(mapStyleOptions);

                            initializeMap();
                        }
                    });
        } else {
            initializeMap();
        }

        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        return rootView;
    }

    public void initializeMap() {
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "123");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Map Started");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void plant() {

        if (myLocationToPlant != null) {
            Intent intent = new Intent(getContext(), PlantActivity.class);
            intent.putExtra(LOCATION_TO_PLANT, myLocationToPlant);
            startActivityForResult(intent, REQUEST_PLANT);
        } else {
            String error = "Não foi possivel obter a sua localização para plantar...";
            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
            FirebaseCrash.log(error);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            MapFragment f = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
            if (f != null)
                getActivity().getFragmentManager().beginTransaction().remove(f).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getPlants() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(PLANTS_DATASET);

// Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (!dataSnapshot.exists() || dataSnapshot.getValue() == null) {
                    Log.e(TAG, "Failed to read value.");
                }
                Log.e(TAG, "Size: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                    Plant plant = dataSnap.getValue(Plant.class);
                    mClusterManager.addItem(plant);
                    Log.e(TAG, "Added Plant : " + plant.getName());
                }
                mClusterManager.cluster();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
//            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            checkPermissions();
            Log.d(TAG, "Location update started ..............: ");
        }
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, GardenMapFragment.this);
            Log.d(TAG, "Location update stopped ..............: ");
        }
    }

    /*
     * Called when the Activity becomes visible.
     */
    @Override
    public void onStart() {
        super.onStart();
        // Connect the client.
        mGoogleApiClient.connect();
    }

    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    public void onStop() {
        // Disconnecting the client invalidates it.
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        startLocationUpdates();
        Log.d(TAG, "Location update resumed .....................");
    }

    @Override
    public void onLocationChanged(Location location) {
        myLocationToPlant = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an expanation to the user *asynchronously* -- don't block
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
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        } else {
            Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            myLocationToPlant = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
//                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//                mLastUpdateTime = java.text.DateFormat.getTimeInstance().format(new Date());
            if (null != map) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocationToPlant, 16));

                mClusterManager = new ClusterManager<Plant>(getActivity(), map);
                mClusterManager.setRenderer(new PlantRenderer());
                map.setOnCameraChangeListener(mClusterManager);
                map.setOnMarkerClickListener(mClusterManager);
                map.setOnInfoWindowClickListener(mClusterManager);
                getPlants();
            }
        }

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 0;

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length < 1 ||
                        (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

                    Toast.makeText(getContext(), "Sem localizacao", Toast.LENGTH_LONG).show();
                    return;
                }
                Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                myLocationToPlant = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
//                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

                if (null != map) {
                    map.setMyLocationEnabled(true);
                    map.getUiSettings().setMyLocationButtonEnabled(true);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocationToPlant, 16));

                    mClusterManager = new ClusterManager<Plant>(getActivity(), map);
                    mClusterManager.setRenderer(new PlantRenderer());
                    map.setOnCameraChangeListener(mClusterManager);
                    map.setOnMarkerClickListener(mClusterManager);
                    map.setOnInfoWindowClickListener(mClusterManager);
                    getPlants();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            //plant = (Plant) data.getSerializableExtra(PLANTED_PLANT);
            Plant plant = PlantareApp.getInstance().getLastPlant();
            showConfirmPlantDialog(getActivity(), plant);
            //Toast.makeText(getContext(), "Obrigado por plantar " + planted.getName() + " a cidade agradece.", Toast.LENGTH_LONG).show();
        }
    }

    private class PlantRenderer extends DefaultClusterRenderer<Plant> {
        public PlantRenderer() {
            super(getContext(), map, mClusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(Plant plant, MarkerOptions markerOptions) {
            DateFormat df = SimpleDateFormat.getDateInstance();
            df.setTimeZone(TimeZone.getDefault());
            String plantedDate = df.format(plant.getWhen());
            String gardener = "";
            if (plant.getGardenerName() != null)
                gardener = plant.getGardenerName();

            markerOptions.title("" + plant.getName())
                    .snippet(gardener + " plantou " + plant.getType() + " em " + plantedDate)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.iplanted2));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            return cluster.getSize() > 1;
        }
    }


    private void showConfirmPlantDialog(@NonNull final Context context, @NonNull final Plant plant){

        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_confirm_plant, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.tx_thanks_to_plant_title));
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setView(view);

        mShareDialog = builder.create();
        mShareDialog.show();

        TextView message = (TextView) view.findViewById(R.id.tx_plant_name);
        message.setText(plant.getName());

        view.findViewById(R.id.bt_share).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                ((MainActivity) getActivity()).shareOnFacebook();
            }
        });

        view.findViewById(R.id.bt_not_now).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                mShareDialog.dismiss();
            }
        });
    }

    public void dismissDialog(){
        if(mShareDialog == null){
            Log.e("Plantare","Share mShareDialog is null");
        }else if(mShareDialog.isShowing()){
            mShareDialog.dismiss();
        }else{
            Log.e("Plantare","Share mShareDialog is not showing");
        }
    }
}