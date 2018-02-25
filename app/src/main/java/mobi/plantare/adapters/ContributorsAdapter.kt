package mobi.plantare.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.item_contributor.view.*
import mobi.plantare.R
import mobi.plantare.fragments.ContributorsFragment.OnListFragmentInteractionListener
import mobi.plantare.model.AppContributor
import mobi.plantare.view.utility.GlideApp
import java.util.*
import android.support.v4.content.ContextCompat.startActivity


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
        holder.mUserName.text = item.login.capitalize()
        holder.mGitHubUrl.text = item.html_url
        holder.mNumberContributions.text = String.format(Locale.getDefault(), "%d", item.contributions)

        GlideApp.with(mContext)
                .load(item.avatar_url)
                .centerInside()
                .circleCrop()
                .into(holder.mUserImage)

        holder.mView.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(item.html_url)
            mContext.startActivity(i)
        }
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mUserImage: ImageView = mView.user_image
        val mNumberContributions: TextView = mView.number_contributions
        val mUserName: TextView = mView.user_name
        val mGitHubUrl: TextView = mView.github_url

        override fun toString(): String {
            return super.toString() + " '" + mUserName.text + "'"
        }
    }
}
