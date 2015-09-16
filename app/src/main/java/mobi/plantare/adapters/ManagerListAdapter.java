package mobi.plantare.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import mobi.plantare.R;

public class ManagerListAdapter extends RecyclerView.Adapter<ManagerListAdapter.MyViewHolder> {
    List<String> data = Collections.emptyList();
    private static int VIEW_TYPE_HEADER = 0x02;
    private static int VIEW_TYPE_CONTENT = 0x03;

    public ManagerListAdapter(List<String> data) {
        this.data = data;
    }

    public void updateList(List<String> updated) {
        data.clear();
        data.addAll(updated);
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
//        if (viewType == VIEW_TYPE_HEADER) {
//            view = inflater.inflate(R.layout.header_item, parent, false);
//        } else {
        view = inflater.inflate(R.layout.content_item, parent, false);
//        }
        return new MyViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return 1;//data.get(position);// instanceof Place ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
//        Place currentItem = data.get(position);

        final View itemView = holder.itemView;

//        final LayoutManager.LayoutParams params = (LayoutManager.LayoutParams) itemView.getLayoutParams();
//        params.setSlm(LinearSLM.ID);
//        params.headerEndMarginIsAuto = false;
//        params.headerStartMarginIsAuto = false;

        holder.title.setText("" + data.get(position));
//        params.setFirstPosition(currentItem.getSectionFirstPosition());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.listText);
        }
    }
}