package Data.Repository

import Data.api.NSApiClient
import Data.api.Resource
import Data.models.ReisAdviesModel
import com.yungert.treinplanner.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class NsApiRepository(private val nsApiClient: NSApiClient) {
    val apiKey = BuildConfig.API_KEY_NS
    suspend fun fetchReisAdviezen(vetrekStation: String, aankomstStation: String): Flow<Resource<ReisAdviesModel>> {
        return flow {
            emit(Resource.Loading())
            val apiResult = nsApiClient.apiService.getReisadviezen(startStation = vetrekStation, eindStation = aankomstStation, authToken = apiKey)
            if(apiResult.isSuccessful) {
                if (apiResult.body() != null) {
                    emit(Resource.Success(apiResult.body()!!))
                }
            }
        }.flowOn(Dispatchers.IO)
    }
}