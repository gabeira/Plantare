package mobi.plantare.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Log
import mobi.plantare.model.AppContributor
import mobi.plantare.repository.ContributorsRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by gabriel on 22/2/18.
 * Implementation of View Model for Contributors
 * https://developer.android.com/topic/libraries/architecture/viewmodel.html
 */
class ContributorsViewModel : ViewModel() {

    var observableContributorsList: MutableLiveData<List<AppContributor>> = MutableLiveData()

    init {
        loadContributors()
    }

    fun getContributorsObserver(): LiveData<List<AppContributor>> {
        return observableContributorsList
    }

    fun loadContributors() {
        ContributorsRepository().getContributors(object : Callback<List<AppContributor>> {
            override fun onResponse(
                call: Call<List<AppContributor>>,
                response: Response<List<AppContributor>>
            ) {
                observableContributorsList.value = response.body()
            }

            override fun onFailure(call: Call<List<AppContributor>>, t: Throwable) {
                Log.e("Error", "Err " + t.localizedMessage)
                //TODO Implement error handling for Contributors request
            }
        })
    }
}