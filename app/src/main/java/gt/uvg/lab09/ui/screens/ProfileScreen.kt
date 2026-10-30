package gt.uvg.lab09.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gt.uvg.lab09.model.Profile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profile: Profile,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text("Fabricante")
                },
                navigationIcon = {
                    TextButton(
                        onClick = onBackClick
                    ) {
                        Text("Regresar")
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = profile.name,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = profile.role,
                style = MaterialTheme.typography.titleMedium
            )

            Text("Ubicación: ${profile.location}")

            HorizontalDivider()

            Text(profile.description)
        }
    }
}
