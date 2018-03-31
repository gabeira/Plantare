package mobi.plantare.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import mobi.plantare.R
import mobi.plantare.adapters.ManagerListAdapter
import mobi.plantare.adapters.SocialListAdapter
import mobi.plantare.datasource.network.PlantsNetwork
import mobi.plantare.model.Plant
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MyPlantsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MyPlantsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyPlantsFragment : Fragment() {


    private var recyclerView: RecyclerView? = null
    private var adapter: ManagerListAdapter? = null

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_plants, container, false)
        //We can use this list to show another share option
        val recyclerView = view.findViewById<View>(R.id.recycler_view) as RecyclerView

        //Criando instância do adapter
        mAdapter = SocialListAdapter(activity!!, plants)
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
//        } else {
//            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        fun newInstance() = MyPlantsFragment()
    }

    private var mAdapter: SocialListAdapter? = null

    val plants: ArrayList<Plant>
        get() {
            //TODO Move this to PlantsNetwork
            val database = FirebaseDatabase.getInstance()
            val databasePlantsReference = database.getReference(PlantsNetwork.PLANTS_DATASET)

            val lista = ArrayList<Plant>()

            //https://firebase.google.com/docs/reference/js/firebase.database.Query
            val myTopPostsQuery = databasePlantsReference.orderByChild("gardenerId").equalTo(FirebaseAuth.getInstance().currentUser?.uid)

            myTopPostsQuery.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists() || dataSnapshot.value == null) {
                        Log.e("", "Failed to read value")
                    }
                    Log.e("Count", "Size: " + dataSnapshot.childrenCount)
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
//                    Log.w(SocialFragment.TAG, "Failed to read value.", error.toException())
                    //TODO Implement Error Scenario
                }
            })

            return lista
        }
}
