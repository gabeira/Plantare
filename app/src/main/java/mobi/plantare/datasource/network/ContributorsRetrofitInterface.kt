package mobi.visamate.datasources.network

import mobi.plantare.model.AppContributor
import retrofit2.Call
import retrofit2.http.GET

/**
 * Created by gabriel on 21/2/18.
 * Interface to set Retrofit requests
 * http://square.github.io/retrofit/
 */
interface ContributorsRetrofitInterface {

    @GET("contributors")
    fun requestContributors(): Call<List<AppContributor>>

}