package gt.uvg.lab09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import gt.uvg.lab09.ui.theme.Lab09Theme
import gt.uvg.lab09.navigation.VersusNavigation
import gt.uvg.lab09.viewModel.VersusViewModel
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab09Theme {
                val versusViewModel: VersusViewModel = viewModel()
                val uiState by versusViewModel.uiState.collectAsStateWithLifecycle()

                VersusNavigation(
                    uiState = uiState,
                    onFavoriteClick = versusViewModel::toggleFavorite
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Lab09Theme {
    }
}