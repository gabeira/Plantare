package mobi.plantare

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ShareCompat
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_plant_detail.*

class PlantDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //TODO Fully implement Plant Detail Activity
        fab.setOnClickListener { view ->
            //TODO String to Share
            val stringToShare = StringBuilder()
                    .append("Add")
                    .append("\nPlant Details")
                    .append("\nTo share")

            startActivity(
                    Intent.createChooser(
                            ShareCompat.IntentBuilder.from(this)
                                    .setType("text/plain")
                                    .setText(stringToShare.toString())
                                    .intent, getString(R.string.action_share)
                    )
            )
        }
    }
}
