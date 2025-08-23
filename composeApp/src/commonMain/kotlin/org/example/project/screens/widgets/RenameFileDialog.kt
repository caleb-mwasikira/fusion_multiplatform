package org.example.project.screens.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.example.project.dto.DirEntry
import org.example.project.dto.isDirectory

@Composable
fun RenameFileDialog(
    file: DirEntry,
    onDismissRequest: () -> Unit,
    onAccept: (String) -> Unit,
    onDecline: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier.width(480.dp)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var newFilename by remember { mutableStateOf(file.name) }

            Text(
                "Rename ${if (file.isDirectory()) "Directory" else "File"}",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
            )

            BasicTextField(
                value = newFilename,
                onValueChange = { newValue ->
                    newFilename = newValue
                },
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 12.dp),
                singleLine = true,
                textStyle = MaterialTheme.typography.titleLarge,
            )
            Box(
                modifier = Modifier.fillMaxWidth()
                    .height(1.dp)
                    .background(Color.Gray)
            )

            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = onDecline,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "No",
                        style = MaterialTheme.typography.titleLarge,
                    )
                }

                ElevatedButton(
                    onClick = {
                        if (newFilename == file.name) {
                            onDismissRequest()
                        } else {
                            onAccept(newFilename)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(8.dp),
                    colors = ButtonDefaults.elevatedButtonColors().copy(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    shape = RoundedCornerShape(8.dp),
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