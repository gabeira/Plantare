package mobi.plantare.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_plant.view.*
import mobi.plantare.R
import mobi.plantare.fragments.LeaderBoardFragment.OnListFragmentInteractionListener
import mobi.plantare.model.PlantareUser
import mobi.plantare.view.utility.GlideApp

/**
 * [RecyclerView.Adapter] that can display a [PlantareUser] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class LeaderBoardRecyclerViewAdapter(
    private val context: Context,
    private val mValues: ArrayList<PlantareUser>
) : RecyclerView.Adapter<LeaderBoardRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_plant, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val plantareUser = mValues[position]
        holder.namePlantView.text = plantareUser.name
        holder.descriptionPlantView.text = "Offering ${plantareUser.numberPlantsDonated} plants."
        GlideApp.with(context)
            .load(plantareUser.photoUrl)
            .centerInside()
            .circleCrop()
            .into(holder.imgPlantView)
    }


    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namePlantView: TextView = itemView.namePlant
        val descriptionPlantView: TextView = itemView.descriptionPlant
        val imgPlantView: ImageView = itemView.imgPlant
    }
}