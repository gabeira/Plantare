package mobi.plantare.datasource.network

import com.google.firebase.database.FirebaseDatabase


/**
 * Created by gabriel on 31/3/18.
 * PlantsNetwork
 */
class PlantsNetwork {


    val database = FirebaseDatabase.getInstance()
    val databasePlantsReference = database.reference.child(PLANTS_DATASET)

    fun savePlant() {
        //TODO move from PlantActivity
    }

    fun deletePlant(id: String, userIdToUpdate: String) {
        databasePlantsReference.child(id).removeValue()
        PlantareUserNetwork().decreaseUserPlants(userIdToUpdate)
    }

    companion object {
        val PLANTS_DATASET = "plants"
    }
}