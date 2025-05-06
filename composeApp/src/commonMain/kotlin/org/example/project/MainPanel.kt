package org.example.project

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import minio_multiplatform.composeapp.generated.resources.Res
import minio_multiplatform.composeapp.generated.resources.doc
import minio_multiplatform.composeapp.generated.resources.excel
import minio_multiplatform.composeapp.generated.resources.external_hard_drive
import minio_multiplatform.composeapp.generated.resources.folder
import minio_multiplatform.composeapp.generated.resources.menu_24dp
import minio_multiplatform.composeapp.generated.resources.notifications_24dp
import minio_multiplatform.composeapp.generated.resources.pdf
import minio_multiplatform.composeapp.generated.resources.person_24dp
import minio_multiplatform.composeapp.generated.resources.powerpoint
import minio_multiplatform.composeapp.generated.resources.search_24dp
import minio_multiplatform.composeapp.generated.resources.zip_folder
import org.example.project.data.dirEntries
import org.example.project.data.formatFileSize
import org.example.project.data.formatLastModified
import org.example.project.data.getFileIcon
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import java.util.Locale

@Composable
fun MainPanel(
    modifier: Modifier,
    onOpenDrawer: (() -> Unit)?
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(32.dp),
        modifier = modifier,
    ) {
        TopBar(onOpenDrawer = onOpenDrawer)

        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            HardDrive2(
                name = "Hard Drive #1",
                storage = "64 Gb / 128 Gb",
                icon = Res.drawable.external_hard_drive
            )

            HardDrive2(
                name = "Hard Drive #2",
                storage = "83 Gb / 512 Gb",
                icon = Res.drawable.external_hard_drive
            )
        }

        QuickAccess()

        MyFiles()
    }
}

@Composable
fun TopBar(
    onOpenDrawer: (() -> Unit)?,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            onOpenDrawer?.let {
                IconButton(
                    onClick = onOpenDrawer,
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.menu_24dp),
                        contentDescription = "Menu",
                    )
                }
            }

            var searchText by remember { mutableStateOf("") }

            TextField(
                value = searchText,
                onValueChange = { newText ->
                    searchText = newText
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.search_24dp),
                        contentDescription = "Search Bar",
                        modifier = Modifier.size(24.dp)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                placeholder = {
                    Text(
                        "Search for files",
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.colors().copy(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    errorContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    focusedIndicatorColor = Color.Transparent, // removes underline when focused
                    unfocusedIndicatorColor = Color.Transparent, // removes underline when not focused
                    disabledIndicatorColor = Color.Transparent // removes underline when disabled
                ),
                textStyle = MaterialTheme.typography.titleLarge,
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = {},
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.notifications_24dp),
                    contentDescription = "Notifications",
                    modifier = Modifier.size(28.dp)
                )
            }

            IconButton(
                onClick = {},
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.person_24dp),
                    contentDescription = "User Profile",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun QuickAccess() {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Quick Access",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
        )

        val filterItems = mutableMapOf(
            "DOCX" to Res.drawable.doc,
            "Excel" to Res.drawable.excel,
            "PDF" to Res.drawable.pdf,
            "PPT" to Res.drawable.powerpoint,
            "Zip" to Res.drawable.zip_folder,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            var selectedFilter by remember { mutableStateOf("") }

            filterItems.forEach { (label, icon) ->
                FilterItem(
                    label = label,
                    icon = icon,
                    isSelected = selectedFilter == label,
                    onClick = {
                        selectedFilter = label
                    },
                )
            }
        }
    }
}

@Composable
fun MyFiles() {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            "My Files",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ){
            items(count = dirEntries.size) { index ->
                val dirEntry = dirEntries[index]

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Image(
                        painter = painterResource(getFileIcon(dirEntry.iconHint ?: "")),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )

                    Text(
                        dirEntry.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Text(
                        formatLastModified(dirEntry.lastModified),
                        style = MaterialTheme.typography.titleMedium,
                    )

                    Text(
                        formatFileSize(dirEntry.size),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
}

@Composable
fun getContainerColor(isSelected: Boolean, isHovered: Boolean): Color {
    if (isSelected) {
        return MaterialTheme.colorScheme.primary
    }

    if (isHovered) {
        return MaterialTheme.colorScheme.primaryContainer
    }
    return MaterialTheme.colorScheme.surfaceContainer
}

@Composable
fun FilterItem(
    label: String,
    icon: DrawableResource,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val localInteractionSource = remember { MutableInteractionSource() }
    val isHovered by localInteractionSource.collectIsHoveredAsState()
    val containerColor = getContainerColor(isSelected, isHovered)
    val contentColor = if (isSelected || isHovered) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.scrim
    }
    val elevation = if (isSelected) 10.dp else 0.dp

    Card(
        modifier = Modifier.size(78.dp)
            .clickable(
                interactionSource = null,
                indication = null,
                onClick = onClick,
            )
            .hoverable(localInteractionSource)
            .pointerHoverIcon(PointerIcon.Hand),
        colors = CardDefaults.cardColors().copy(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation,
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                label.uppercase(Locale.getDefault()),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }
    }
}

@Composable
fun HardDrive2(
    name: String,
    storage: String,
    icon: DrawableResource = Res.drawable.external_hard_drive,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val cardContainerColor = if (isHovered) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.background
    }
    val contentColor = if (isHovered) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.scrim.copy(alpha = 0.8f)
    }

    Card(
        modifier = Modifier
            .hoverable(interactionSource)
            .pointerHoverIcon(PointerIcon.Hand),
        colors = CardDefaults.cardColors().copy(
            containerColor = cardContainerColor,
            contentColor = contentColor,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 28.dp)
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(56.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(48.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Text(
                        storage,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                CircularProgressIndicator(
                    progress = { 0.6f },
                    color = contentColor,
                    trackColor = cardContainerColor,
                )
            }
        }
    }
}