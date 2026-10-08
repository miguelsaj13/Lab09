package gt.uvg.lab09.data

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver

@Database(
    entities = [FavoriteProductEntity::class, OrderLineEntity::class],
    version = 1
)
abstract class StoreDatabase : RoomDatabase() {

    abstract fun storeDao(): StoreDao

    companion object {
        @Volatile
        private var instance: StoreDatabase? = null

        fun getInstance(context: Context): StoreDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder<StoreDatabase>(
                    context.applicationContext,
                    "versus-store.db"
                )
                    .setDriver(AndroidSQLiteDriver())
                    .build()
                    .also { instance = it }
            }
        }
    }
}