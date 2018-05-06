package mobi.plantare.fragments

import android.net.Uri
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
import mobi.plantare.adapters.SocialListAdapter
import mobi.plantare.datasource.network.PlantsNetwork
import mobi.plantare.model.Plant
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SocialFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SocialFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SocialFragment : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null
    private var mAdapter: SocialListAdapter? = null

    //Connect to database
    // This method is called once with the initial value and again
    // whenever data at this location is updated.
    //Failed to read value
    val plants: ArrayList<Plant>
        get() {
            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference(PlantsNetwork.PLANTS_DATASET)

            val lista = ArrayList<Plant>()

            val myOrderedQuery = myRef
                    .orderByChild("registerDate")

            myOrderedQuery.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists() || dataSnapshot.value == null) {
                        Log.e(TAG, "Failed to read value")
                    }
                    Log.e(TAG, "Size: " + dataSnapshot.childrenCount)
                    lista.clear()
                    for (dataSnap in dataSnapshot.children) {
                        val plant = dataSnap.getValue(Plant::class.java)
                        if (plant != null) {
                            lista.add(plant)
                        }
                    }
                    mAdapter?.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            })

            return lista
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_social, container, false)

        //Criando o adapter
        val recyclerView = layout.findViewById<View>(R.id.social_recycler_view) as RecyclerView

        //Criando instância do adapter
        mAdapter = SocialListAdapter(activity!!, plants)
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        return layout
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

    companion object {
        private val TAG = "Plants"
        @JvmStatic
        fun newInstance() = SocialFragment()

    }
}
