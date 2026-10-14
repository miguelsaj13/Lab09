package gt.uvg.lab09.viewModel

sealed interface RemoteUiState<out T> {

    data object Idle : RemoteUiState<Nothing>

    data object Loading : RemoteUiState<Nothing>

    data class Success<T>(
        val data: T
    ) : RemoteUiState<T>

    data object Empty : RemoteUiState<Nothing>

    data class Error(
        val message: String
    ) : RemoteUiState<Nothing>
}
