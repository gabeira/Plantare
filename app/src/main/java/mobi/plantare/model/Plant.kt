package mobi.plantare.model

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.maps.android.clustering.ClusterItem

import java.io.Serializable

/**
 * Created by gabeira@gmail.com on 6/23/16.
 */
@IgnoreExtraProperties
class Plant : Serializable, ClusterItem {

    var id: String? = null
    var latitude: Double = 0.toDouble()
    var longitude: Double = 0.toDouble()
    var gardenerId: String? = null
    var gardenerName: String? = null
    var name: String? = null
    var type: String? = null

    var registerDate: Long = 0
    var photo: String? = null
    var isActive: Boolean = false

    constructor() {
        isActive = false
    }

    constructor(lat: Double, lng: Double) {
        latitude = lat
        longitude = lng
        isActive = false
    }

    @Exclude
    override fun getPosition(): LatLng {
        return LatLng(latitude, longitude)
    }

    @Exclude
    override fun getTitle(): String {
        return "Title" //TODO Implement CLusterItem Title for Maps
    }

    @Exclude
    override fun getSnippet(): String {
        return "Snippet" //TODO Implement CLusterItem Snippet for Maps
    }
}
