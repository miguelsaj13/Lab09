package gt.uvg.lab09.data

import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException

suspend fun <T> repositoryResult(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (exception: CancellationException) {
    throw exception
} catch (exception: FirebaseFirestoreException) {
    val message = when (exception.code) {
        FirebaseFirestoreException.Code.PERMISSION_DENIED -> "No tienes permiso. Revisa la sesión y las reglas de Firestore."
        FirebaseFirestoreException.Code.UNAVAILABLE -> "No se pudo conectar con Firestore. Revisa tu conexión."
        FirebaseFirestoreException.Code.ABORTED -> "Hubo demasiadas solicitudes al mismo tiempo. Inténtalo nuevamente."
        FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> "Se agotó la cuota de Firestore. Inténtalo más tarde."
        else -> "No se pudo actualizar Firestore. Revisa tu conexión e inténtalo nuevamente."
    }
    Result.failure(IllegalStateException(message, exception))
} catch (exception: FirebaseAuthException) {
    Result.failure(IllegalStateException("No se pudo iniciar sesión. Revisa Authentication y tu conexión.", exception))
} catch (exception: IOException) {
    Result.failure(IllegalStateException("No se pudo conectar con el servidor. Revisa tu conexión.", exception))
} catch (exception: HttpException) {
    Result.failure(IllegalStateException("El servidor no pudo procesar la solicitud. Revisa Mis pedidos antes de reenviar un pedido.", exception))
} catch (exception: SerializationException) {
    Result.failure(IllegalStateException("No se pudo interpretar la respuesta del servidor.", exception))
} catch (exception: Exception) {
    Result.failure(exception)
}
