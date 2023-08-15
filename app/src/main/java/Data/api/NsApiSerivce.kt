package Data.api

import Data.models.ReisAdviesModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NSApiService {
    @GET("reisinformatie-api/api/v3/trips/")
    suspend fun getReisadviezen(
        @Query("fromStation") startStation: String,
        @Query("toStation") eindStation: String,
        @Header("Ocp-Apim-Subscription-Key") authToken: String
    ): Response<ReisAdviesModel>

//    @GET("reisinformatie-api/api/v2/journey/")
//    suspend fun getReis(
//        @Query("train") id: String,
//        @Query("departureUicCode") departureUicCode: String,
//        @Query("arrivalUicCode") arrivalUicCode: String,
//        @Query("dateTime") dateTime: String,
//        @Header("Ocp-Apim-Subscription-Key") authToken: String
//    ): Response<TreinRit>
//
//    @GET("reisinformatie-api/api/v3/trips/trip/")
//    suspend fun getSingleReisById(
//        @Query("ctxRecon") id: String,
//        @Header("Ocp-Apim-Subscription-Key") authToken: String
//    ): Response<Trip>
//
//    @GET("places-api/v2/places/")
//    suspend fun getDichtbijzijndeStation(
//        @Query("lat") lat: String,
//        @Query("lng") lng: String,
//        @Query("limit") limit: Int = 3,
//        @Query("radius") radius: Int = 10000,
//        @Query("details") details: Boolean = false,
//        @Query("name") name: String = "Stations",
//        @Header("Ocp-Apim-Subscription-Key") authToken: String
//    ): Response<PlaceResponse>


}