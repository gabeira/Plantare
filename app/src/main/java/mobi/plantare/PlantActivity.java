package mobi.plantare;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.UUID;

import mobi.plantare.fragments.GardenMapFragment;
import mobi.plantare.model.Plant;


public class PlantActivity extends AppCompatActivity {

    private final static String TAG = PlantActivity.class.getSimpleName();
    private final static int MY_PERMISSIONS_REQUEST_CAMERA = 5432;
    private static final int REQUEST_IMAGE_CAPTURE = 0;
    private static final int REQUEST_GALLERY_CODE = 1;

    private LatLng local;
    private EditText plantName;
    private EditText plantType;
    private ImageView plantImage;
    private Plant plant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant);
        setActionBar();
        plant = new Plant();

        local = getIntent().getParcelableExtra(GardenMapFragment.LOCATION_TO_PLANT);
        if (null != local) {
            Log.d(TAG, "local: " + local.latitude);
        } else {
            Log.e(TAG, "local NULLLL");
        }

        plantName = ((EditText) findViewById(R.id.plant_name));
        plantType = ((EditText) findViewById(R.id.plant_type));
        plantImage = ((ImageView) findViewById(R.id.plant_image));
    }

    private void setActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Plante sua Planta e registre aqui");
        }
    }

    public void pickFromGallery(View View) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY_CODE);
    }

    public void takePhoto(View View) {
        permission();
    }

    public void submit(View view) {
        try {
            if (local == null) {
                Toast.makeText(getApplicationContext(), "Erro: Localizacao sem valor, por favor tentar novamente", Toast.LENGTH_LONG).show();
            } else if (plantName == null || plantName.getText() == null || plantName.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Nome da Planta Obrigatorio", Toast.LENGTH_LONG).show();
                plantName.setError("Nome da Planta Obrigatorio");
            } else if (plantType == null || plantType.getText() == null || plantType.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Tipo da Planta Obrigatorio", Toast.LENGTH_LONG).show();
                plantType.setError("Tipo da Planta Obrigatorio");
            } else {
                //FirebaseDatabase database = FirebaseDatabase.getInstance();
                //DatabaseReference myRef = database.getReference();

                if (null != FirebaseAuth.getInstance().getCurrentUser()) {
                    plant.setGardenerName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    plant.setGardenerId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                } else {
                    plant.setGardenerName(Build.MANUFACTURER.toUpperCase() + " " + Build.MODEL);
                }
                Log.d(TAG, "Gardener: " + plant.getGardenerName());

                plant.setId(UUID.randomUUID().toString());
                plant.setName(plantName.getText().toString());
                plant.setType(plantType.getText().toString());
                plant.setWhen(Calendar.getInstance().getTimeInMillis());
                plant.setLatitude(local.latitude);
                plant.setLongitude(local.longitude);

                //myRef.child(GardenMapFragment.PLANTS_DATASET).child(plant.getId()).setValue(plant);

                //sharePlantOnFacebook();

                Log.d(TAG, "Voce Plantou " + plant.getName());

                Intent result = new Intent(this, GardenMapFragment.class);
                result.putExtra(GardenMapFragment.PLANTED_PLANT, plant);
                setResult(Activity.RESULT_OK, result);
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Erro:" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        InputStream stream = null;
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                /*ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
                if (imageBitmap != null) {
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 70, bYtE);
                }
                byte[] byteArray = bYtE.toByteArray();
                String imageBase64String = Base64.encodeToString(byteArray, Base64.DEFAULT);*/
                plantImage.setImageBitmap(imageBitmap);
                plantImage.setVisibility(View.VISIBLE);
                //plant.setPhoto(imageBase64String);

                Uri tempUri = getImageUri(getApplicationContext(), imageBitmap);
                plant.setPhoto(getRealPathFromURI(tempUri, this));
//                if (imageBitmap != null)
//                    imageBitmap.recycle();

            } catch (RuntimeException e) {
                Toast.makeText(getApplicationContext(),
                        "Error: " + e.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            try {
                stream = getContentResolver().openInputStream(data.getData());
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap imageBitmap = BitmapFactory.decodeStream(stream, new Rect(-1, -1, -1, -1), options);
                /*ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
                if (imageBitmap != null) {
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 70, bYtE);
                }
                byte[] byteArray = bYtE.toByteArray();
                String imageBase64String = Base64.encodeToString(byteArray, Base64.DEFAULT);*/
                plantImage.setImageBitmap(imageBitmap);
                plantImage.setVisibility(View.VISIBLE);
                //plant.setPhoto(imageBase64String);

                Uri selectedImage = data.getData();
                plant.setPhoto(getRealPathFromURI(selectedImage , this));
//                if (imageBitmap != null)
//                    imageBitmap.recycle();
            } catch (FileNotFoundException e) {
                Toast.makeText(getApplicationContext(),
                        "Error: " + e.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (RuntimeException e) {
                Toast.makeText(getApplicationContext(),
                        "Error: " + e.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void permission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the task you need to do.
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                } else {
                    // permission denied, Disable the functionality that depends on this permission.
                    findViewById(R.id.bt_camera).setVisibility(View.GONE);
                    pickFromGallery(null);
                }
            }
        }
    }

    public String getRealPathFromURI(Uri contentURI, Activity context) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = context.managedQuery(contentURI, projection, null,
                null, null);
        if (cursor == null)
            return null;
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if (cursor.moveToFirst()) {
            String s = cursor.getString(column_index);
            // cursor.close();
            return s;
        }
        // cursor.close();
        return null;
    }
}