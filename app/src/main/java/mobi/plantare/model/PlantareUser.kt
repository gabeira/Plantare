package mobi.plantare.model


/**
 * Created by gabeira@gmail.com on 23/03/2018.
 * Plantare User Data Class
 */
data class PlantareUser(
        var uid: String = "",
        var name: String = "",
        var email: String = "",
        var emailVerified: Boolean = false,
        var photoUrl: String = "",
        var phoneNumber: String = "",
        var facebookUserId: String = "",
        var userSince: Long = 0,
        var deviceList: List<String> = mutableListOf(),
        var lastUse: Long = 0,
        var numberPlantsDonated: Int = 0,
        var numberPlantsPlanted: Int = 0)
