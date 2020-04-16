package com.example.mediagallery.flickr.networking

import com.example.mediagallery.flickr.model.PhotosSearchResponse
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://wwww.flickr.com/"
private const val CONNECTION_TIMEOUT_MS: Long = 10

object WebClient {
    val client: ApiService by lazy {
        Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create()
                )
            )
            .client(
                OkHttpClient.Builder().connectTimeout(
                    CONNECTION_TIMEOUT_MS,
                    TimeUnit.SECONDS
                ).addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                }).build()
            )
            .build()
            .create(ApiService::class.java)
    }
}

interface ApiService {
    // Either add the api key to a file that is not being tracked with your version control system,
    // or add a gradle script to add it as a string resource (per Google's recommendation)
    //@GET("?method=flickr.photos.search&format=json&nojsoncallback=1&tags={search}&api_key=5e97710be9ddf23a1c64c5feadf3d036")
    @GET("services/rest/?")
    suspend fun fetchImages(@Query("method") method: String, @Query("api_key") api_key: String,
                            @Query("format") format: String, @Query("nojsoncallback") nojsoncallback: Int,
                            @Query("page") page: Int, @Query("text") text: String?): PhotosSearchResponse

}