package gt.uvg.lab09

import android.app.Application

class VersusApplication : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}
