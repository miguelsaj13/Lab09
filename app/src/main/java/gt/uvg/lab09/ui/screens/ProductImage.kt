package gt.uvg.lab09.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.background
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import androidx.compose.ui.platform.LocalContext

private enum class ImageState {
    Loading,
    Success,
    Error
}

@Composable
fun ProductImage(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    var imageState by remember(imageUrl) {
        mutableStateOf(ImageState.Loading)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Imagen del producto",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            onLoading = {
                imageState = ImageState.Loading
            },
            onSuccess = {
                imageState = ImageState.Success
            },
            onError = {
                imageState = ImageState.Error
            }
        )

        when (imageState) {
            ImageState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                )
            }

            ImageState.Success -> {
            }

            ImageState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.errorContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Imagen no disponible",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}