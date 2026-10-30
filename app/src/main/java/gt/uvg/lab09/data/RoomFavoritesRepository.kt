package gt.uvg.lab09.data

import gt.uvg.lab09.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class RoomFavoritesRepository(private val dao: StoreDao) : FavoritesRepository {
    private val mutex = Mutex()

    override fun observeFavoriteIds(): Flow<Set<Int>> =
        dao.observeFavorites().map { favorites -> favorites.map { it.productId }.toSet() }

    override suspend fun toggleFavorite(productId: Int): Result<Unit> = repositoryResult {
        mutex.withLock {
            if (dao.isFavorite(productId)) dao.deleteFavorite(productId)
            else dao.insertFavorite(FavoriteProductEntity(productId))
        }
    }
}


