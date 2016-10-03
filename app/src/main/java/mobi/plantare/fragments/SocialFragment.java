package mobi.plantare.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import mobi.plantare.R;
import mobi.plantare.adapters.SocialListAdapter;
import mobi.plantare.model.Plant;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SocialFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SocialFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SocialFragment extends Fragment {

    private ClusterManager<Plant> mclusterManager;

    private static final String PLANTS_DATASET = "plants";
    private static final String TAG = "Plants";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SocialFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SocialFragment newInstance(String param1, String param2) {
        SocialFragment fragment = new SocialFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SocialFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_social, container, false);

        ArrayList<Plant> lista = getPlants();
        //ArrayList<Plant> lista = new ArrayList<>();

        for (Plant plant : lista){
            Log.d("Debug",plant.getName());
        }


        //Criando o adapter
        SocialListAdapter adapter = new SocialListAdapter(getActivity(),lista);
        RecyclerView recyclerView = (RecyclerView) layout.findViewById(R.id.social_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return layout;
    }

    public ArrayList<Plant> getPlants(){
        //Connect to database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(PLANTS_DATASET);

        final ArrayList<Plant> lista = new ArrayList<>();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (!dataSnapshot.exists() || dataSnapshot.getValue() == null){
                    Log.e(TAG,"Failed to read value");
                }
                Log.e(TAG,"Size: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot dataSnap : dataSnapshot.getChildren()){
                    Plant plant = dataSnap.getValue(Plant.class);
                    //mclusterManager.addItem(plant);
                    lista.add(plant);
                    Log.e(TAG,"Plant (name): " + plant.getName());
                    Log.e(TAG,"Plant (type): " + plant.getType());
                    Log.e(TAG,"Plant (photo): " + plant.getPhoto());
                }
                //mclusterManager.cluster();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                //Failed to read value
                Log.w(TAG,"Failed to read value.",error.toException());
            }
        });

        return lista;

        /*
        Plant plant1 = new Plant();
        plant1.setName("Planta 01");
        plant1.setType("Descrição planta 01");

        Plant plant2 = new Plant();
        plant2.setName("Planta 02");
        plant2.setType("Descrição planta 02");

        Plant plant3 = new Plant();
        plant3.setName("Planta 03");
        plant3.setType("Descrição planta 03");

        Plant plant4 = new Plant();
        plant4.setName("Planta 04");
        plant4.setType("Descrição planta 04");

        Plant plant5 = new Plant();
        plant5.setName("Planta 05");
        plant5.setType("Descrição planta 05");

        lista.add(plant1);
        lista.add(plant2);
        lista.add(plant3);
        lista.add(plant4);
        lista.add(plant5);

        return lista;
        */
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
