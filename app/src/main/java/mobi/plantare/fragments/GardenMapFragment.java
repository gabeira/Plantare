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

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import mobi.plantare.R;

/**
 * A placeholder fragment containing a simple view.
 * Created by gabriel on 7/1/15.
 */
public class GardenMapFragment extends Fragment implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap map;
    private LatLng myLocationToPlant;
    private Firebase myFirebaseRef;
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
         myFirebaseRef = new Firebase("https://plantare.firebaseio.com/");

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


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Planted");
//            query.whereEqualTo("playerName", "Dan Stemkoski");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> plantedList, ParseException e) {
                if (e == null) {
                    Log.d("score", "Retrieved " + plantedList.size() + " scores");
                    for (ParseObject p : plantedList) {
                        Log.d("score", "pp: " + p.getString("name"));
                        try {
                            LatLng ll = new LatLng(p.getParseGeoPoint("place").getLatitude(), p.getParseGeoPoint("place").getLongitude());

                            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
                                    .fromResource(R.drawable.ic_local_florist_white_48dp);
                            BitmapDescriptor bitmapDescriptor2 = BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);

                            map.addMarker(new MarkerOptions()
                                    .title("" + p.getString("name"))
                                    .icon(bitmapDescriptor2)
//                                    .snippet("The most populous city in Australia.")
                                    .position(ll));
                        } catch (Exception ex) {
                            Log.e("score", "Error: " + ex.getMessage());
                            e.printStackTrace();
                        }
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });

    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void plant() {

        if (myLocationToPlant != null) {

            String whatIPlanted = "Bouganvilleas";
            Log.d("Main", "Voce Plantou " + whatIPlanted);
            ParseObject testObject = new ParseObject("Planted");
            testObject.put("name", whatIPlanted);
            testObject.put("place", new ParseGeoPoint(myLocationToPlant.latitude, myLocationToPlant.longitude));
            testObject.saveInBackground();
            Toast.makeText(getActivity(), "Obrigado por plantar " + whatIPlanted + " a cidade agradece.", Toast.LENGTH_LONG).show();
            myFirebaseRef.child("planted").setValue(whatIPlanted);

        } else {
            myLocationToPlant = new LatLng(-3.1101645, -58.9629745);
            Toast.makeText(getActivity(), "Não foi possivel obter a sua localização para plantar...", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            MapFragment f = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
            if (f != null)
                getActivity().getFragmentManager().beginTransaction().remove(f).commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}