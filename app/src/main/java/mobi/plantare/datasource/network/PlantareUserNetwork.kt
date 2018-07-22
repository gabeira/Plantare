package mobi.plantare.datasource.network

import android.os.Build
import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import mobi.plantare.BuildConfig
import mobi.plantare.model.PlantareUser
import org.apache.commons.lang3.StringUtils
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by gabriel on 23/3/18.
 * PlantareUserNetwork
 */
class PlantareUserNetwork {


    val database = FirebaseDatabase.getInstance()
    val databaseUserReference = database.reference.child(USER_DATASET)

    fun increaseUserPlants(userIdToUpdate: String?) {
//        updateUserPlantsNumber(true, userIdToUpdate)
    }

    fun decreaseUserPlants(userIdToUpdate: String?) {
//        updateUserPlantsNumber(false, userIdToUpdate)
    }

    private fun updateUserPlantsNumber(up: Boolean, userIdToUpdate: String?) {

        //TODO Verify why this single update is not working

        //https://firebase.google.com/docs/database/android/read-and-write#save_data_as_transactions
//        databaseUserReference.child(userIdToUpdate!!).runTransaction(object : Transaction.Handler {
//            override fun doTransaction(mutableData: MutableData): Transaction.Result? {
//
//                val p = mutableData.getValue<PlantareUser>(PlantareUser::class.java)
//                        ?: return Transaction.success(mutableData)
//
//                if (up) {
//                    p.numberPlantsDonated++
//                } else {
//                    if (p.numberPlantsDonated > 0)
//                        p.numberPlantsDonated--
//                }
//                // Set value and report transaction success
//                mutableData.value = p
//                return Transaction.success(mutableData)
//            }
//
//            override fun onComplete(databaseError: DatabaseError, b: Boolean,
//                                    dataSnapshot: DataSnapshot) {
//                // Transaction completed
//                Log.d("", "postTransaction:onComplete:$databaseError")
//            }
//        })
    }

    fun saveUser(firebaseUser: FirebaseUser) {

        val deviceList = mutableListOf<String>()
        deviceList.add(StringUtils.capitalize(Build.MANUFACTURER) + " - " + Build.MODEL)

        val user = PlantareUser(
                firebaseUser.uid!!,
                firebaseUser.displayName!!,
                firebaseUser.email!!,
                firebaseUser.isEmailVerified!!,
                firebaseUser.photoUrl!!.toString(),
                firebaseUser.phoneNumber + "",
                "",
                Calendar.getInstance(Locale.getDefault()).timeInMillis,
                deviceList,
                Calendar.getInstance(Locale.getDefault()).timeInMillis,
                0,
                0
        )


        databaseUserReference.child(user.uid).setValue(user)
    }

    private fun saveMyDeviceInfo() {
        //TODO save user device information
//        val hashMap = HashMap()
//        hashMap.put("Android", Build.VERSION.RELEASE)
//        hashMap.put("Android SDK", Build.VERSION.SDK_INT)
//        hashMap.put("App version", BuildConfig.VERSION_NAME)
//        hashMap.put("App build", BuildConfig.VERSION_CODE)
//        hashMap.put("Brand", StringUtils.capitalize(Build.BRAND) + " - " + StringUtils.capitalize(Build.DEVICE))
//        hashMap.put("Model", StringUtils.capitalize(Build.MANUFACTURER) + " - " + Build.MODEL)
//        hashMap.put("Last update", SimpleDateFormat.getDateTimeInstance().format(Calendar.getInstance().timeInMillis))
//        DEVICE_DATA_REF.setValue(hashMap)
    }

    companion object {

        val USER_DATASET = "users"

    }
}