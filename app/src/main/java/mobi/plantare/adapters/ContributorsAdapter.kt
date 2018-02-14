package mobi.plantare.adapters

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import kotlinx.android.synthetic.main.item_contributor.view.*
import mobi.plantare.R
import mobi.plantare.fragments.ContributorsFragment.OnListFragmentInteractionListener
import mobi.plantare.model.AppContributor
import java.util.*

/**
 * [RecyclerView.Adapter] that can display a [AppContributor] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class ContributorsAdapter(private val mContext: Context, private var mValues: List<AppContributor>, private val mListener: OnListFragmentInteractionListener?) : RecyclerView.Adapter<ContributorsAdapter.ViewHolder>() {

    fun setContributors(items: List<AppContributor>) {
        mValues = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_contributor, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mUserName.text = item.userLogin
        holder.mGitHubUrl.text = item.gitHubHtmlUrl
        holder.mNumberContributions.text = String.format(Locale.getDefault(), "%d", item.contributions)

        Glide.with(mContext)
                .load(item.avatarUrl)
                .asBitmap()
                .centerCrop()
                .into(object : BitmapImageViewTarget(holder.mUserImage) {
                    override fun setResource(resource: Bitmap) {
                        val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(mContext.resources, resource)
                        circularBitmapDrawable.isCircular = true
                        holder.mUserImage.setImageDrawable(circularBitmapDrawable)
                    }
                })

        holder.mView.setOnClickListener {
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mUserImage: ImageView = mView.user_image
        val mNumberContributions: TextView = mView.number_contributions
        val mGitHubUrl: TextView = mView.user_name
        val mUserName: TextView = mView.github_url

        override fun toString(): String {
            return super.toString() + " '" + mUserName.text + "'"
        }
    }
}
