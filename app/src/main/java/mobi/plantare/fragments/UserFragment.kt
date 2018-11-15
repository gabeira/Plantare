package mobi.plantare.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import mobi.plantare.R
import mobi.plantare.datasource.network.PlantareUserNetwork

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [UserFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [UserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserFragment : Fragment() {

    private var mListener: OnUserLoginListener? = null

//    private var mCallbackManager: CallbackManager? = null

    private var txtUser: TextView? = null

    private var user: FirebaseUser? = null
    private var mAuth: FirebaseAuth? = null
//    private var mAuthListener: FirebaseAuth.AuthStateListener? = null

    //    private var facebookLoginButton: LoginButton? = null
//    private var facebookSignOutButton: Button? = null
    var sign_in_button: SignInButton? = null
    var sign_out_button: Button? = null
    var disconnect_button: Button? = null
    var sign_out_and_disconnect: LinearLayout? = null

    private var mGoogleSignInClient: GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Now this method is called in MainActivity
        //FacebookSdk.sdkInitialize(getActivity());

//        mCallbackManager = CallbackManager.Factory.create()

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(context!!, gso)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth!!.currentUser
        updateUI(currentUser)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user, container, false)

        txtUser = view.findViewById<View>(R.id.user) as TextView
//        facebookLoginButton = view.findViewById<View>(R.id.button_facebook_login) as LoginButton
//        facebookSignOutButton = view.findViewById<View>(R.id.button_facebook_signout) as Button
//        facebookSignOutButton!!.setOnClickListener { signOut() }

        sign_in_button = view.findViewById<SignInButton>(R.id.sign_in_button) as SignInButton
        sign_out_button = view.findViewById<Button>(R.id.sign_out_button) as Button
        disconnect_button = view.findViewById<Button>(R.id.disconnect_button) as Button
        sign_out_and_disconnect = view.findViewById<LinearLayout>(R.id.sign_out_and_disconnect) as LinearLayout
        sign_in_button?.setOnClickListener({ signIn() })
        sign_out_button?.setOnClickListener({ signOut() })
        disconnect_button?.setOnClickListener({ revokeAccess() })

        updateUI(user)

        //Initialize Facebook Login button
        //If use this method can't use setReadPermissions method
        //        facebookLoginButton.setPublishPermissions(Arrays.asList("publish_actions"));

        //If use this method can't use setPublishPermissions method
//        facebookLoginButton!!.setReadPermissions("email", "public_profile")

        //If using in a fragment
//        facebookLoginButton!!.fragment = this

        // Callback registration
//        facebookLoginButton!!.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
//            override fun onSuccess(loginResult: LoginResult) {
//                Log.d(TAG, "facebook:onSuccess:" + loginResult)
//                signInFirebaseUser(loginResult.accessToken)
//            }
//
//            override fun onCancel() {
//                Log.e(TAG, "facebook:onCancel")
//            }
//
//            override fun onError(error: FacebookException) {
//                Log.e(TAG, "facebook:onError", error)
//            }
//        })
        return view
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        try {
            mListener = activity as OnUserLoginListener?
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString()
                    + " must implement OnUserLoginListener");
        }

    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnUserLoginListener {
        fun updateUserInfo(name: String, email: String, photoUrl: String)
    }

//    private fun signInFirebaseUser(token: AccessToken) {
//        Log.d(TAG, "signInFirebaseUser:" + token)
//
//        val credential = FacebookAuthProvider.getCredential(token.token)
//        mAuth!!.signInWithCredential(credential).addOnCompleteListener { task ->
//            Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful)
//            // If sign in fails, display a message to the user. If sign in succeeds
//            // the auth state listener will be notified and logic to handle the
//            // signed in user can be handled in the listener.
//            val database = FirebaseDatabase.getInstance()
//            val myRef = database.reference
//            if (null != user) {
//                val gardener = PlantareUser()
//                gardener.id = user!!.uid
//                gardener.name = user!!.displayName
//                gardener.email = user!!.email
//                gardener.facebookUser = token.userId
//                gardener.lastUse = Calendar.getInstance(Locale.getDefault()).timeInMillis
//                myRef.child(GARDENER_DATASET).child(user!!.uid).setValue(gardener)
//                Log.d(TAG, "Login com " + gardener.name!!)
//            }
//
//            updateUI(user)
//            if (!task.isSuccessful) {
//                Log.w(TAG, "signInWithCredential", task.exception)
//                Toast.makeText(activity, "Authentication failed.",
//                        Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

//    fun signFacebookOut() {
//        mAuth!!.signOut()
//        LoginManager.getInstance().logOut()
//        FirebaseAuth.getInstance().signOut()
//        updateUI(null)
//    }

    private fun updateUI(user: FirebaseUser?) {
        //        hideProgressDialog();
        if (user != null) {
            Log.d(TAG, "updateUI:signed_in:" + user.uid + "  -  " + user.displayName)
            txtUser!!.text = user.displayName
//            facebookLoginButton!!.visibility = View.GONE
//            facebookSignOutButton!!.visibility = View.VISIBLE

            sign_in_button?.visibility = View.GONE
            sign_out_and_disconnect?.visibility = View.VISIBLE
            mListener?.updateUserInfo(user?.displayName!!, user?.email!!, user?.photoUrl.toString())

        } else {
            txtUser!!.text = getString(R.string.please_login_to_plant)
//            facebookLoginButton!!.visibility = View.VISIBLE
//            facebookSignOutButton!!.visibility = View.GONE

            sign_in_button?.visibility = View.VISIBLE
            sign_out_and_disconnect?.visibility = View.GONE
            mListener?.updateUserInfo("", "", "")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        mCallbackManager!!.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                account?.let {
                    firebaseAuthWithGoogle(it)
                }
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // [START_EXCLUDE]
                updateUI(null)
                // [END_EXCLUDE]
                Log.e(TAG, "Google sign in failed XXXX " + e.localizedMessage)
                Toast.makeText(context!!, "Google sign in failed XXXX " + e.localizedMessage, Toast.LENGTH_LONG).show()
            }

        }
    }

    companion object {
        private val TAG = UserFragment::class.java.simpleName
        private val GARDENER_DATASET = "gardener"
        private val RC_SIGN_IN = 9001

        @JvmStatic
        fun newInstance() = UserFragment()
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)
        //TODO showProgressDialog() for login

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener({ task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        val user = mAuth!!.currentUser

                        PlantareUserNetwork().saveUser(user!!)

                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
//                        Snackbar.make(context.findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                    //TODO hideProgressDialog() for login
                })
    }

    private fun signOut() {
        // Firebase sign out
        mAuth!!.signOut()

        // Google sign out
        mGoogleSignInClient!!.signOut().addOnCompleteListener({
            updateUI(null)
        })
    }

    private fun revokeAccess() {
        // Firebase sign out
        mAuth!!.signOut()

        // Google revoke access
        mGoogleSignInClient!!.revokeAccess().addOnCompleteListener({
            updateUI(null)
        })
    }

}