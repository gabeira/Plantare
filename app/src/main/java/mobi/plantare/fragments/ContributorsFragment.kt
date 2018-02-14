package mobi.plantare.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.firebase.analytics.FirebaseAnalytics

import org.json.JSONArray
import org.json.JSONObject

import java.io.IOException
import java.util.ArrayList

import mobi.plantare.R
import mobi.plantare.adapters.ContributorsAdapter
import mobi.plantare.model.AppContributor
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

/**
 * A fragment representing a list of Items.
 *
 *
 * Activities containing this fragment MUST implement the [OnListFragmentInteractionListener]
 * interface.
 */
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
class ContributorsFragment : Fragment() {

    private val BASE_URL = "https://api.github.com/repos/gabeira/Plantare/contributors"
    private var contributorsAdapter: ContributorsAdapter? = null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private val client = OkHttpClient()
    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_contributors, container, false)
        mFirebaseAnalytics?.setCurrentScreen(activity!!, "Contributors", null)

        // Set the adapter
        if (view is RecyclerView) {
            val context = view.getContext()
            view.layoutManager = LinearLayoutManager(context)
            contributorsAdapter = ContributorsAdapter(getContext()!!, ArrayList(), listener)
            view.adapter = contributorsAdapter
            requestDataSync()
        }
        return view
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
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
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: AppContributor)
    }

    internal fun requestDataSync() {
        val request = Request.Builder()
                .url(BASE_URL)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("", "Failure " + e.localizedMessage)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                Log.e("", "Response code: " + response.code())
                if (response.isSuccessful) {
                    val orders = response.body()!!.string()
                    activity!!.runOnUiThread {
                        try {
                            val jsonObjects = JSONArray(orders)
                            val contributorList = ArrayList<AppContributor>()
                            for (i in 0 until jsonObjects.length()) {
                                val jsonObject = jsonObjects.getJSONObject(i)
                                val appContributor = AppContributor(
                                        jsonObject.getString("login"),
                                        jsonObject.getString("avatar_url"),
                                        jsonObject.getString("html_url"),
                                        jsonObject.getInt("contributions"))
                                contributorList.add(appContributor)
                            }
                            contributorsAdapter!!.setContributors(contributorList)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = ContributorsFragment()
    }
}
