package gt.uvg.lab09

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import gt.uvg.lab09.data.FirebaseAuthRepository
import gt.uvg.lab09.data.FirestoreProductRepository
import gt.uvg.lab09.data.LocalSettings
import gt.uvg.lab09.data.RetrofitOrdersRepository
import gt.uvg.lab09.data.RoomCartRepository
import gt.uvg.lab09.data.RoomFavoritesRepository
import gt.uvg.lab09.data.StoreDatabase
import gt.uvg.lab09.network.RetrofitInstance
import gt.uvg.lab09.repository.AuthRepository
import gt.uvg.lab09.repository.CartRepository
import gt.uvg.lab09.repository.FavoritesRepository
import gt.uvg.lab09.repository.OrdersRepository
import gt.uvg.lab09.repository.PreferencesRepository
import gt.uvg.lab09.repository.ProductRepository

class AppContainer(context: Context) {
    private val dao = StoreDatabase.getInstance(context).storeDao()
    private val settings = LocalSettings(context)
    val authRepository: AuthRepository = FirebaseAuthRepository(FirebaseAuth.getInstance())
    val productRepository: ProductRepository = FirestoreProductRepository(FirebaseFirestore.getInstance(), authRepository)
    val favoritesRepository: FavoritesRepository = RoomFavoritesRepository(dao)
    val preferencesRepository: PreferencesRepository = settings
    val ordersRepository: OrdersRepository = RetrofitOrdersRepository(RetrofitInstance.ordersApi)
    val cartRepository: CartRepository = RoomCartRepository(dao, settings, productRepository, ordersRepository)
}
