package mobi.plantare.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_my_plants.view.*
import mobi.plantare.PlantActivity
import mobi.plantare.PlantDetailActivity
import mobi.plantare.R
import mobi.plantare.adapters.MyPlantAdapter
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

    private var mListener: OnMyPlantItemInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_plants, container, false)
        //We can use this list to show another share option

        //Criando instância do adapter
        mAdapter = MyPlantAdapter(view.context, plants, object : OnMyPlantItemInteractionListener {
            override fun onEditItemClick(plant: Plant) {
                //TODO Implement Edit plant
                activity?.startActivity(Intent(context, PlantActivity::class.java))
            }

            override fun onDeleteItemClick(plant: Plant) {
                deleteThisPlant(plant)
            }
        })
        view.recyclerViewPlants.adapter = mAdapter
        view.recyclerViewPlants.layoutManager = LinearLayoutManager(activity)

        return view
    }

    private fun deleteThisPlant(plant: Plant) {
        //TODO Improve delete plant
        val database = FirebaseDatabase.getInstance()
        val databasePlantsReference = database.getReference(PlantsNetwork.PLANTS_DATASET)
        databasePlantsReference.child(plant.id!!).removeValue()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnMyPlantItemInteractionListener) {
            mListener = context
//        } else {
//            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }


    interface OnMyPlantItemInteractionListener {
        fun onEditItemClick(plant: Plant)
        fun onDeleteItemClick(plant: Plant)
    }

    companion object {
        fun newInstance() = MyPlantsFragment()
    }

    private lateinit var mAdapter: MyPlantAdapter

    private val plants: ArrayList<Plant>
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
                    mAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
//                    Log.w(DonationPlantListFragment.TAG, "Failed to read value.", error.toException())
                    //TODO Implement Error Scenario
                }
            })

            return lista
        }
}
