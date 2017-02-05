package mobi.plantare.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import mobi.plantare.PlantareApp;
import mobi.plantare.R;
import mobi.plantare.adapters.ManagerListAdapter;
import mobi.plantare.model.Gardener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {
    private static final String TAG = UserFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;
    private ManagerListAdapter adapter;

    private CallbackManager mCallbackManager;

    private TextView txtUser;
    private static String GARDENER_DATASET = "gardener";

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private LoginButton facebookLoginButton;
    private Button facebookSignOutButton;

    private SignInButton googleSignInButton;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance() {
        UserFragment fragment = new UserFragment();
        return fragment;
    }

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Now this method is called in MainActivity
        //FacebookSdk.sdkInitialize(getActivity());

        mCallbackManager = CallbackManager.Factory.create();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid() + "  -  " + user.getDisplayName());

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        txtUser = (TextView) view.findViewById(R.id.user);
        facebookLoginButton = (LoginButton) view.findViewById(R.id.button_facebook_login);
        facebookSignOutButton = (Button) view.findViewById(R.id.button_facebook_signout);
        facebookSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        updateUI(user);

        //Initialize Facebook Login button
        //If use this method can't use setReadPermissions method
//        facebookLoginButton.setPublishPermissions(Arrays.asList("publish_actions"));

        //If use this method can't use setPublishPermissions method
        facebookLoginButton.setReadPermissions("email", "public_profile");

        //If using in a fragment
        facebookLoginButton.setFragment(this);

        // Callback registration
        facebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                signInFirebaseUser(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "facebook:onError", error);
            }
        });

        //We can use this list to show another share option
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        List<String> itens = new ArrayList<>();
        itens.add("Teste 1");
        itens.add("Teste 2");
        itens.add("Teste 3");
        itens.add("Teste 4");
        itens.add("Teste 5");
        adapter = new ManagerListAdapter(itens);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        return view;
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
        void onFragmentInteraction(Uri uri);
    }

    private void signInFirebaseUser(final AccessToken token) {
        Log.d(TAG, "signInFirebaseUser:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();
                if (null != user) {
                    Gardener gardener = new Gardener();
                    gardener.setId(user.getUid());
                    gardener.setName(user.getDisplayName());
                    gardener.setEmail(user.getEmail());
                    gardener.setFacebookUser(token.getUserId());
                    gardener.setLastUse(Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
                    myRef.child(GARDENER_DATASET).child(user.getUid()).setValue(gardener);
                    Log.d(TAG, "Login com " + gardener.getName());
                }

                updateUI(user);
                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInWithCredential", task.getException());
                    Toast.makeText(getActivity(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
        updateUI(null);
    }

    private void updateUI(FirebaseUser user) {
//        hideProgressDialog();
        if (user != null) {
            Log.d(TAG, "updateUI:signed_in:" + user.getUid() + "  -  " + user.getDisplayName());
            txtUser.setText(user.getDisplayName());
            facebookLoginButton.setVisibility(View.GONE);
            facebookSignOutButton.setVisibility(View.VISIBLE);
        } else {
            txtUser.setText(getString(R.string.please_login_to_plant));
            facebookLoginButton.setVisibility(View.VISIBLE);
            facebookSignOutButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}