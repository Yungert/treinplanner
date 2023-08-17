package Data.Repository

import Data.api.NSApiClient
import Data.api.Resource
import Data.models.ReisAdviesModel
import Data.models.TreinRit
import Data.models.TreinRitDetail
import Data.models.Trip
import com.yungert.treinplanner.BuildConfig
import com.yungert.treinplanner.presentation.ui.model.PlaceResponse
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

    suspend fun fetchSingleTripById(reisadviesId: String): Flow<Resource<Trip>> {
        return flow {
            emit(Resource.Loading())
            val apiResult = nsApiClient.apiService.getSingleReisById(id = reisadviesId, authToken = apiKey)
            if(apiResult.isSuccessful) {
                if (apiResult.body() != null) {
                    emit(Resource.Success(apiResult.body()!!))
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun fetchDichtbijzijndeStation(lat: String, lng: String): Flow<Resource<PlaceResponse>> {
        return flow {
            emit(Resource.Loading())
            val apiResult = nsApiClient.apiService.getDichtbijzijndeStation(lat = lat, lng = lng, authToken = apiKey)
            if(apiResult.isSuccessful) {
                if (apiResult.body() != null) {
                    emit(Resource.Success(apiResult.body()!!))
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun fetchRitById(depatureUicCode: String, arrivalUicCode: String, reisId: String, dateTime: String): Flow<Resource<TreinRitDetail>> {
        return flow {
            emit(Resource.Loading())
            val apiResult = nsApiClient.apiService.getReis(departureUicCode = depatureUicCode, arrivalUicCode = arrivalUicCode, dateTime = dateTime, id = reisId, authToken = apiKey)
            if(apiResult.isSuccessful) {
                if (apiResult.body() != null) {
                    emit(Resource.Success(apiResult.body()!!))
                }
            }
        }.flowOn(Dispatchers.IO)
    }
}