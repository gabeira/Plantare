package mobi.plantare.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import mobi.plantare.R
import mobi.plantare.adapters.LeaderBoardRecyclerViewAdapter
import mobi.plantare.datasource.network.PlantareUserNetwork
import mobi.plantare.model.PlantareUser
import java.util.*

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
class LeaderBoardFragment : Fragment() {

    private var mListener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_leaderboard, container, false)
        val recyclerView = view.findViewById<View>(R.id.list) as RecyclerView
        mAdapter = LeaderBoardRecyclerViewAdapter(activity!!, topUsersList)
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        return view
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            mListener = context
//        } else {
//            throw RuntimeException(context!!.toString() + " must implement OnListFragmentInteractionListener")
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
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: PlantareUser)
    }

    companion object {
        fun newInstance() = LeaderBoardFragment()
    }

    private var mAdapter: LeaderBoardRecyclerViewAdapter? = null

    val topUsersList: ArrayList<PlantareUser>
        get() {
            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference(PlantareUserNetwork.USER_DATASET)

            val lista = ArrayList<PlantareUser>()

            val myTopPostsQuery = myRef
                    .orderByChild("numberPlantsDonated")
            myTopPostsQuery.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists() || dataSnapshot.value == null) {
                        Log.e("LeaderBoardFragment", "Failed to read value")
                    }
                    lista.clear()
                    for (dataSnap in dataSnapshot.children) {
                        val plant = dataSnap.getValue(PlantareUser::class.java)
                        if (plant != null && plant.numberPlantsDonated > 0) {
                            lista.add(plant)
                        }
                    }
                    lista.reverse()
                    mAdapter?.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("LeaderBoardFragment", "Failed to read value.", error.toException())
                }
            })

            return lista
        }
}
