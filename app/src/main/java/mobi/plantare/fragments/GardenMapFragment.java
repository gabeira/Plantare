package mobi.plantare.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

//import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Random;

import mobi.plantare.R;
import mobi.plantare.model.Plant;

/**
 * A placeholder fragment containing a simple view.
 * Created by gabriel on 7/1/15.
 */
public class GardenMapFragment extends Fragment implements //LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap map;
    private LatLng myLocationToPlant;
    private FirebaseAnalytics mFirebaseAnalytics;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

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
                            initializeMap();
                        }
                    });
        } else {
            initializeMap();
        }
        return rootView;
    }

    public void initializeMap() {
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
        } else {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
        }

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "123");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Map Started");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

//    @Override
//    public void onLocationChanged(Location location) {
//
//    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void plant() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if (myLocationToPlant != null) {
            int id = (new Random()).nextInt(90);

            String whatIPlanted = "Bouganvilleas";
            Log.d("Main", "Voce Plantou " + whatIPlanted);
            Toast.makeText(getActivity(), "Obrigado por plantar " + whatIPlanted + " a cidade agradece.", Toast.LENGTH_LONG).show();

            Plant plant = new Plant();
            plant.setName(whatIPlanted + id);
            plant.setType("Flor");
            plant.setWhen(Calendar.getInstance().getTimeInMillis());
            plant.setLatitude(myLocationToPlant.latitude);
            plant.setLongitude(myLocationToPlant.longitude);

            DatabaseReference myRef = database.getReference("plants_data");


            myRef.child("plants").child(plant.getName()).setValue(plant);

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
}