package mobi.plantare.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import mobi.plantare.R
import mobi.plantare.adapters.ManagerListAdapter
import mobi.plantare.model.Gardener
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [UserFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [UserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserFragment : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null

    private var recyclerView: RecyclerView? = null
    private var adapter: ManagerListAdapter? = null

    private var mCallbackManager: CallbackManager? = null

    private var txtUser: TextView? = null

    private var user: FirebaseUser? = null
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null

    private var facebookLoginButton: LoginButton? = null
    private var facebookSignOutButton: Button? = null

    private val googleSignInButton: SignInButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Now this method is called in MainActivity
        //FacebookSdk.sdkInitialize(getActivity());

        mCallbackManager = CallbackManager.Factory.create()

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            user = firebaseAuth.currentUser
            if (user != null) {
                // User is signed in
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user!!.uid + "  -  " + user!!.displayName)

            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out")
            }
            // ...
        }

    }

    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(mAuthListener!!)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth!!.removeAuthStateListener(mAuthListener!!)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user, container, false)

        txtUser = view.findViewById<View>(R.id.user) as TextView
        facebookLoginButton = view.findViewById<View>(R.id.button_facebook_login) as LoginButton
        facebookSignOutButton = view.findViewById<View>(R.id.button_facebook_signout) as Button
        facebookSignOutButton!!.setOnClickListener { signOut() }

        updateUI(user)

        //Initialize Facebook Login button
        //If use this method can't use setReadPermissions method
        //        facebookLoginButton.setPublishPermissions(Arrays.asList("publish_actions"));

        //If use this method can't use setPublishPermissions method
        facebookLoginButton!!.setReadPermissions("email", "public_profile")

        //If using in a fragment
        facebookLoginButton!!.fragment = this

        // Callback registration
        facebookLoginButton!!.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult)
                signInFirebaseUser(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.e(TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.e(TAG, "facebook:onError", error)
            }
        })

        //We can use this list to show another share option
        recyclerView = view.findViewById<View>(R.id.recycler_view) as RecyclerView
        val itens = ArrayList<String>()
        itens.add("Teste 1")
        itens.add("Teste 2")
        itens.add("Teste 3")
        itens.add("Teste 4")
        itens.add("Teste 5")
        adapter = ManagerListAdapter(itens)
        recyclerView!!.adapter = adapter
        recyclerView!!.layoutManager = LinearLayoutManager(this.activity)

        return view
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        try {
            mListener = activity as OnFragmentInteractionListener?
        } catch (e: ClassCastException) {
            //            throw new ClassCastException(activity.toString()
            //                    + " must implement OnFragmentInteractionListener");
        }

    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    private fun signInFirebaseUser(token: AccessToken) {
        Log.d(TAG, "signInFirebaseUser:" + token)

        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth!!.signInWithCredential(credential).addOnCompleteListener { task ->
            Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful)
            // If sign in fails, display a message to the user. If sign in succeeds
            // the auth state listener will be notified and logic to handle the
            // signed in user can be handled in the listener.
            val database = FirebaseDatabase.getInstance()
            val myRef = database.reference
            if (null != user) {
                val gardener = Gardener()
                gardener.id = user!!.uid
                gardener.name = user!!.displayName
                gardener.email = user!!.email
                gardener.facebookUser = token.userId
                gardener.lastUse = Calendar.getInstance(Locale.getDefault()).timeInMillis
                myRef.child(GARDENER_DATASET).child(user!!.uid).setValue(gardener)
                Log.d(TAG, "Login com " + gardener.name!!)
            }

            updateUI(user)
            if (!task.isSuccessful) {
                Log.w(TAG, "signInWithCredential", task.exception)
                Toast.makeText(activity, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun signOut() {
        mAuth!!.signOut()
        LoginManager.getInstance().logOut()
        FirebaseAuth.getInstance().signOut()
        updateUI(null)
    }

    private fun updateUI(user: FirebaseUser?) {
        //        hideProgressDialog();
        if (user != null) {
            Log.d(TAG, "updateUI:signed_in:" + user.uid + "  -  " + user.displayName)
            txtUser!!.text = user.displayName
            facebookLoginButton!!.visibility = View.GONE
            facebookSignOutButton!!.visibility = View.VISIBLE
        } else {
            txtUser!!.text = getString(R.string.please_login_to_plant)
            facebookLoginButton!!.visibility = View.VISIBLE
            facebookSignOutButton!!.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mCallbackManager!!.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private val TAG = UserFragment::class.java.simpleName
        private val GARDENER_DATASET = "gardener"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment UserFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(): UserFragment {
            return UserFragment()
        }
    }
}