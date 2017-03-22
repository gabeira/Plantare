package mobi.plantare.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobi.plantare.R;
import mobi.plantare.adapters.ContributorsAdapter;
import mobi.plantare.model.AppContributor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ContributorsFragment extends Fragment {

    private String BASE_URL = "https://api.github.com/repos/gabeira/Plantare/contributors";
    private ContributorsAdapter contributorsAdapter;
    private FirebaseAnalytics mFirebaseAnalytics;
    private OkHttpClient client = new OkHttpClient();


    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContributorsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ContributorsFragment newInstance(int columnCount) {
        ContributorsFragment fragment = new ContributorsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contributors, container, false);
        mFirebaseAnalytics.setCurrentScreen(getActivity(), "Contributors", null);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            contributorsAdapter = new ContributorsAdapter(getContext(), new ArrayList<AppContributor>(), mListener);
            recyclerView.setAdapter(contributorsAdapter);
            requestDataSync();
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
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
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(AppContributor item);
    }

    void requestDataSync() {
        Request request = new Request.Builder()
                .url(BASE_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("", "Failure " + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                Log.e("", "Response code: " + response.code());
                if (response.isSuccessful()) {
                    final String orders = response.body().string();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray jsonObjects = new JSONArray(orders);
                                final List<AppContributor> contributorList = new ArrayList<>();
                                for (int i = 0; i < jsonObjects.length(); i++) {
                                    JSONObject jsonObject = jsonObjects.getJSONObject(i);
                                    AppContributor appContributor = new AppContributor(
                                            jsonObject.getString("login"),
                                            jsonObject.getString("avatar_url"),
                                            jsonObject.getString("html_url"),
                                            jsonObject.getInt("contributions"));
                                    contributorList.add(appContributor);
                                }
                                contributorsAdapter.setContributors(contributorList);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }
}
