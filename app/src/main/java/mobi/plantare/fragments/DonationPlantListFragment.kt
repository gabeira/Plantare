package mobi.plantare.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_donation_list.view.*
import mobi.plantare.PlantDetailActivity
import mobi.plantare.R
import mobi.plantare.adapters.PlantAdapter
import mobi.plantare.datasource.network.PlantsNetwork
import mobi.plantare.model.Plant
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [DonationPlantListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DonationPlantListFragment : Fragment() {

    private lateinit var mAdapter: PlantAdapter

    //Connect to database
    // This method is called once with the initial value and again
    // whenever data at this location is updated.
    //Failed to read value
    private val plants: ArrayList<Plant>
        get() {
            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference(PlantsNetwork.PLANTS_DATASET)

            val lista = ArrayList<Plant>()
            val myOrderedQuery = myRef.orderByChild("registerDate")

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
                    mAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            })
            return lista
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_donation_list, container, false)
        //Criando instância do adapter
        mAdapter = PlantAdapter(layout.context, plants, object : OnPlantItemInteractionListener {
            override fun onPlantListItemClick(plant: Plant) {
                activity?.startActivity(Intent(context, PlantDetailActivity::class.java))
            }
        })
        layout.recyclerViewPlants.adapter = mAdapter
        layout.recyclerViewPlants.layoutManager = LinearLayoutManager(layout.context)
        return layout
    }

    interface OnPlantItemInteractionListener {
        fun onPlantListItemClick(plant: Plant)
    }

    companion object {
        private val TAG = "Plants"
        @JvmStatic
        fun newInstance() = DonationPlantListFragment()
    }
}
