package gt.uvg.lab09.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface OrdersApi {

    @POST("order")
    suspend fun createOrder( //envía un pedido
        @Body order: OrderDto
    ): OrderDto

    @GET("order")
    suspend fun getOrders( //consulta pedidos
        @Query("sortBy") sortBy: String? = null,
        @Query("order") order: String? = null
    ): List<OrderDto>

    @GET("order/{id}") //consulta un pedido específico
    suspend fun getOrderById(
        @Path("id") id: String
    ): OrderDto
}