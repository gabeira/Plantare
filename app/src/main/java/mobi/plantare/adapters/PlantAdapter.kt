package mobi.plantare.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.item_plant.view.*
import mobi.plantare.R
import mobi.plantare.fragments.DonationPlantListFragment
import mobi.plantare.model.AppContributor
import mobi.plantare.model.Plant
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by jbalves on 10/6/16.
 */

class PlantAdapter(val context: Context,
                   val mValues: ArrayList<Plant>,
                   val plantListener: DonationPlantListFragment.OnPlantItemInteractionListener)
    : RecyclerView.Adapter<PlantAdapter.ViewHolder>() {

    //#3 Step - Monta o layout na lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_plant, parent, false)
        return ViewHolder(view)
    }

    //#4 Step - Recupera uma posição da lista no layout
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Recupera a referência da planta
        val plant = mValues[position]

        //Seta os valores da Planta para o layout dentro do Holder

        val df = SimpleDateFormat.getDateInstance()
        df.timeZone = TimeZone.getDefault()
        val plantedDate = df.format(plant!!.registerDate)

        holder.namePlantView.text = plant.type?.capitalize() + ",  " + plant.name?.capitalize()
        //TODO Extract String for internacionalization
        holder.descriptionPlantView.text = plant.gardenerName + " offered since " + plantedDate

        if (plant.photo == null) {
            holder.imgPlantView.setImageResource(R.mipmap.ic_launcher)
        } else {
            //Convert from string to Bitmap
            val imageBytesArray = Base64.decode(plant.photo, Base64.DEFAULT)
            val bmp = BitmapFactory.decodeByteArray(imageBytesArray, 0, imageBytesArray.size)
            holder.imgPlantView.setImageBitmap(bmp)
        }

        holder.itemView.setOnClickListener {
            plantListener.onPlantListItemClick(plant)
        }
    }


    //#5 Step - Conta a quantidade de elementos existente na lista
    override fun getItemCount(): Int {
        return mValues.size
    }

    //#2 Step - Mapeia os elementos do layout
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val namePlantView: TextView = itemView.namePlant
        val descriptionPlantView: TextView = itemView.descriptionPlant
        val imgPlantView: ImageView = itemView.imgPlant

    }
}