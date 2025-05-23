package org.example.project.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun ConfirmationDialog(
    title: String,
    subtitle: String? = null,
    onDismissRequest: () -> Unit,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier.size(320.dp, 240.dp)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(vertical = 12.dp, horizontal = 32.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.headlineSmall,
                )

                subtitle?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = onDecline,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    Text(
                        "No",
                        style = MaterialTheme.typography.titleLarge,
                    )
                }

                ElevatedButton(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    Text(
                        "Yes",
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }
        }
    }
}