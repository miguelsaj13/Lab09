package gt.uvg.lab09.data

import com.google.firebase.auth.FirebaseAuth
import gt.uvg.lab09.repository.AuthRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class FirebaseAuthRepository(private val auth: FirebaseAuth) : AuthRepository {
    private val mutex = Mutex()

    override suspend fun ensureSession(): Result<Unit> = repositoryResult {
        mutex.withLock {
            if (auth.currentUser == null) {
                check(withTimeoutOrNull(15_000L) { auth.signInAnonymously().await() } != null) { "No se pudo iniciar sesión. Revisa tu conexión y reintenta." }
            }
            check(auth.currentUser != null) { "No se pudo iniciar la sesión anónima." }
        }
    }
}
