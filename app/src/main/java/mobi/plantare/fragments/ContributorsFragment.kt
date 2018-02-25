package mobi.plantare.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.firebase.analytics.FirebaseAnalytics

import org.json.JSONArray

import java.io.IOException
import java.util.ArrayList

import mobi.plantare.R
import mobi.plantare.adapters.ContributorsAdapter
import mobi.plantare.model.AppContributor
import mobi.plantare.viewmodel.ContributorsViewModel

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

    private var contributorsAdapter: ContributorsAdapter? = null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
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
        }
        val mLiveDataTimerViewModel = ViewModelProviders.of(this).get(ContributorsViewModel::class.java!!)
        mLiveDataTimerViewModel.getContributorsObserver().observe(this, Observer<List<AppContributor>> { listOfContributors ->
            contributorsAdapter!!.setContributors(listOfContributors!!)
        })
        return view
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
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
        fun onListFragmentInteraction(item: AppContributor)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ContributorsFragment()
    }
}
