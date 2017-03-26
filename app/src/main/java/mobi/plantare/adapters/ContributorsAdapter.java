package mobi.plantare.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.List;
import java.util.Locale;

import mobi.plantare.R;
import mobi.plantare.fragments.ContributorsFragment.OnListFragmentInteractionListener;
import mobi.plantare.model.AppContributor;

/**
 * {@link RecyclerView.Adapter} that can display a {@link AppContributor} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class ContributorsAdapter extends RecyclerView.Adapter<ContributorsAdapter.ViewHolder> {

    private List<AppContributor> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final Context mContext;

    public ContributorsAdapter(Context context, List<AppContributor> items, OnListFragmentInteractionListener listener) {
        mContext = context;
        mValues = items;
        mListener = listener;
    }

    public void setContributors(List<AppContributor> items) {
        mValues = items;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contributor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mUserName.setText(mValues.get(position).getUserLogin());
        holder.mGitHubUrl.setText(mValues.get(position).getGitHubHtmlUrl());
        holder.mNumberContributions.setText(
                String.format(Locale.getDefault(), "%d", mValues.get(position).getContributions()));

        Glide.with(mContext).load(mValues.get(position).getAvatarUrl()).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.mUserImage) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                holder.mUserImage.setImageDrawable(circularBitmapDrawable);
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final ImageView mUserImage;
        final TextView mNumberContributions;
        final TextView mGitHubUrl;
        final TextView mUserName;
        AppContributor mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mUserImage = (ImageView) view.findViewById(R.id.user_image);
            mNumberContributions = (TextView) view.findViewById(R.id.number_contributions);
            mUserName = (TextView) view.findViewById(R.id.user_name);
            mGitHubUrl = (TextView) view.findViewById(R.id.github_url);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mUserName.getText() + "'";
        }
    }
}
