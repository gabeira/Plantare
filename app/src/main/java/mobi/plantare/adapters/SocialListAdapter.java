package mobi.plantare.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import mobi.plantare.R;
import mobi.plantare.model.Plant;


/**
 * Created by jbalves on 10/6/16.
 */

public class SocialListAdapter extends RecyclerView.Adapter <SocialListAdapter.SocialViewHolder> {


    private LayoutInflater inflater;

    List<Plant> data = Collections.emptyList();

    public SocialListAdapter(Context context, List<Plant> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;

    }

    @Override
    public SocialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.custom_row, parent, false);
        SocialViewHolder holder = new SocialViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(SocialViewHolder holder, int position) {
        Plant current = data.get(position);
        holder.title.setText(current.getName());
        holder.icon.setImageResource(Integer.parseInt(current.getPhoto()));

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class SocialViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;

        public SocialViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.listText);
            icon = (ImageView) itemView.findViewById(R.id.listIcon);

        }
    }
}
