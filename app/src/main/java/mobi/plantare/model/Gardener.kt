package mobi.plantare.model

import java.io.Serializable

/**
 * Created by gabeira@gmail.com on 6/23/16.
 */
class Gardener : Serializable {

    var id: String? = null
    var name: String? = null
    var email: String? = null
    var facebookUser: String? = null
    var userSince: Long = 0
    var lastUse: Long = 0
    var numberPlantsPlanted: Int = 0

    constructor() {}

    constructor(id: String, name: String, email: String, facebookUser: String, userSince: Long) {
        this.id = id
        this.name = name
        this.email = email
        this.facebookUser = facebookUser
        this.userSince = userSince
    }
}
