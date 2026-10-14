package gt.uvg.lab09.network

import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType

object RetrofitInstance {

    private const val BASE_URL =
        "https://6ac7cff775a4ce3fe7224429.mockapi.io/api/v1/"

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(
            json.asConverterFactory(
                "application/json".toMediaType()
            )
        )
        .build()

    val ordersApi: OrdersApi = retrofit.create(OrdersApi::class.java)
}
