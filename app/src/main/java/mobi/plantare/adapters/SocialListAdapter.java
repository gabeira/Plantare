package mobi.plantare.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mobi.plantare.R;
import mobi.plantare.fragments.SocialFragment;
import mobi.plantare.model.Plant;

import static android.R.attr.data;


/**
 * Created by jbalves on 10/6/16.
 */

public class SocialListAdapter extends RecyclerView.Adapter <SocialListAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<Plant> lista;
    private Plant plant;

    //#1 Step
    public SocialListAdapter(Context context, ArrayList<Plant> lista) {
        this.lista = lista;
        this.context = context;
    }

    //#3 Step - Monta o layout na lista
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_social_list, null);
        return new ViewHolder(view);
    }

    //#4 Step - Recupera uma posição da lista no layout
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Recupera a referência da planta
        Plant plant = lista.get(position);

        //Seta os valores da Planta para o layout dentro do Holder
        holder
                .setName(plant.getName())
                .setDescription(plant.getType())
                .setImg(plant.getPhoto());
    }



    //#5 Step - Conta a quantidade de elementos existente na lista
    @Override
    public int getItemCount() {
        //tamanho da lista
        return lista.size();
    }

    //#2 Step - Mapeia os elementos do layout
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView namePlantView;
        private TextView descriptionPlantView;
        private ImageView imgPlantView;

        public ViewHolder(View itemView) {
            super(itemView);
            namePlantView = (TextView) itemView.findViewById(R.id.namePlant);
            descriptionPlantView = (TextView) itemView.findViewById(R.id.descriptionPlant);
            imgPlantView = (ImageView) itemView.findViewById(R.id.imgPlant);
        }

        public ViewHolder setName(String name){
            if (namePlantView == null) return this;
            namePlantView.setText(name);
            return this;
        }

        public ViewHolder setDescription(String description){
            if (descriptionPlantView == null) return this;
            descriptionPlantView.setText(description);
            return this;
        }

        public ViewHolder setImg(String img){
            if (imgPlantView == null) return this;

            //Convert from string to Bitmap
            //byte[] byteArray = Base64.decode(img, Base64.DEFAULT);
            //Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(img);

            Glide
                    .with(context)
                    .using(new FirebaseImageLoader())
                    .load(ref)
                    .into(imgPlantView);
            return this;
        }
    }

    public class FirebaseImageLoader implements StreamModelLoader<StorageReference> {

        @Override
        public DataFetcher<InputStream> getResourceFetcher(StorageReference model, int width, int height) {
            return new FirebaseStorageFetcher(model);
        }

        private class FirebaseStorageFetcher implements DataFetcher<InputStream> {

            private StorageReference mRef;

            FirebaseStorageFetcher(StorageReference ref) {
                mRef = ref;
            }

            @Override
            public InputStream loadData(Priority priority) throws Exception {
                return Tasks.await(mRef.getStream()).getStream();
            }

            @Override
            public void cleanup() {
                // No cleanup possible, Task does not expose cancellation
            }

            @Override
            public String getId() {
                return mRef.getPath();
            }

            @Override
            public void cancel() {
                // No cancellation possible, Task does not expose cancellation
            }
        }
    }
}
