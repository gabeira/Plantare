package mobi.plantare.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jbalves on 10/6/16.
 */

public class SocialListAdapter extends RecyclerView.Adapter <SocialListAdapter.SocialViewHolder> {

    @Override
    public SocialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(SocialViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class SocialViewHolder extends RecyclerView.ViewHolder {

        public SocialViewHolder(View itemView) {
            super(itemView);
        }
    }
}
