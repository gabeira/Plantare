package mobi.plantare

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_plant.*
import mobi.plantare.datasource.network.PlantareUserNetwork
import mobi.plantare.fragments.GardenMapFragment
import mobi.plantare.model.Plant
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.util.*


class PlantActivity : AppCompatActivity() {

    private var local: LatLng? = null
    private var plant: Plant = Plant()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant)
        setActionBar()

        local = intent.getParcelableExtra(GardenMapFragment.LOCATION_TO_PLANT)
        if (null != local) {
            Log.d(TAG, "local: " + local?.latitude)
        } else {
            Log.e(TAG, "local Null")
        }
    }

    private fun setActionBar() {
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = "Plante sua Planta e registre aqui"
        }
    }

    fun pickFromGallery(View: View?) {
        val intent = Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_GALLERY_CODE)
    }

    fun takePhoto(View: View) {
        permission()
    }

    fun submit(view: View) {
        try {
            if (local == null) {
                Toast.makeText(applicationContext, "Erro: Localizacao sem valor, por favor tentar novamente", Toast.LENGTH_LONG).show()
            } else if (plant_name == null || plant_name?.text == null || plant_name?.text.toString().isEmpty()) {
                Toast.makeText(applicationContext, "Nome da Planta Obrigatorio", Toast.LENGTH_LONG).show()
                plant_name?.error = "Nome da Planta Obrigatorio"
            } else if (plant_type == null || plant_type?.text == null || plant_type?.text.toString().isEmpty()) {
                Toast.makeText(applicationContext, "Tipo da Planta Obrigatorio", Toast.LENGTH_LONG).show()
                plant_type?.error = "Tipo da Planta Obrigatorio"
            } else {
                val database = FirebaseDatabase.getInstance()
                val myRef = database.reference

                if (null != FirebaseAuth.getInstance().currentUser) {
                    plant.gardenerName = FirebaseAuth.getInstance().currentUser?.displayName
                    plant.gardenerId = FirebaseAuth.getInstance().currentUser?.uid
                } else {
                    plant.gardenerName = Build.MANUFACTURER.toUpperCase() + " " + Build.MODEL
                }
                Log.d(TAG, "PlantareUser: " + plant.gardenerName)

                plant.id = UUID.randomUUID().toString()
                plant.name = plant_name?.text.toString()
                plant.type = plant_type?.text.toString()
                plant.registerDate = Calendar.getInstance().timeInMillis
                plant.latitude = local?.latitude!!
                plant.longitude = local?.longitude!!

                myRef.child(GardenMapFragment.PLANTS_DATASET).child(plant.id!!).setValue(plant)

                PlantareUserNetwork().increaseUserPlants(FirebaseAuth.getInstance().currentUser?.uid!!)

                //The plant and the photo is available for use by the application and
                //avoids the problem with the bundle
                //PlantareApp.getInstance().setLastPlant(plant);
                Log.d(TAG, "Voce Plantou " + plant.name)

                val result = Intent(this, GardenMapFragment::class.java)
                //If the photo is tool large, throws a exception
                //result.putExtra(GardenMapFragment.PLANTED_PLANT, plant);
                setResult(Activity.RESULT_OK, result)
                finish()
            }
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "Erro:" + e.localizedMessage, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var stream: InputStream? = null
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            try {
                val extras = data?.extras
                val imageBitmap = extras?.get("data") as Bitmap
                val bYtE = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 70, bYtE)
                val byteArray = bYtE.toByteArray()
                val imageBase64String = Base64.encodeToString(byteArray, Base64.DEFAULT)
                plant_image.setImageBitmap(imageBitmap)
                plant_image.visibility = View.VISIBLE
                plant.photo = imageBase64String
                //                if (imageBitmap != null)
                //                    imageBitmap.recycle();

            } catch (e: RuntimeException) {
                Toast.makeText(applicationContext,
                        "Error: " + e.localizedMessage,
                        Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }

        } else if (requestCode == REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            try {
                stream = contentResolver.openInputStream(data?.data)
                val options = BitmapFactory.Options()
                options.inSampleSize = 2
                val imageBitmap = BitmapFactory.decodeStream(stream, Rect(-1, -1, -1, -1), options)
                val bYtE = ByteArrayOutputStream()
                imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 70, bYtE)
                val byteArray = bYtE.toByteArray()
                val imageBase64String = Base64.encodeToString(byteArray, Base64.DEFAULT)
                plant_image.setImageBitmap(imageBitmap)
                plant_image.visibility = View.VISIBLE
                plant.photo = imageBase64String

                //                if (imageBitmap != null)
                //                    imageBitmap.recycle();
            } catch (e: FileNotFoundException) {
                Toast.makeText(applicationContext,
                        "Error: " + e.localizedMessage,
                        Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            } catch (e: RuntimeException) {
                Toast.makeText(applicationContext,
                        "Error: " + e.localizedMessage,
                        Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            } finally {
                if (stream != null) {
                    try {
                        stream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        }
    }

    private fun permission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA),
                    MY_PERMISSIONS_REQUEST_CAMERA)
        } else {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CAMERA -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the task you need to do.
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (takePictureIntent.resolveActivity(packageManager) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    }
                } else {
                    // permission denied, Disable the functionality that depends on this permission.
                    findViewById<View>(R.id.bt_camera).visibility = View.GONE
                    pickFromGallery(null)
                }
            }
        }
    }

    companion object {

        private val TAG = PlantActivity::class.java.simpleName
        private val MY_PERMISSIONS_REQUEST_CAMERA = 5432
        private val REQUEST_IMAGE_CAPTURE = 0
        private val REQUEST_GALLERY_CODE = 1
    }
}