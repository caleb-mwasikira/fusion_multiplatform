package org.example.project.screens.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import minio_multiplatform.composeapp.generated.resources.Res
import minio_multiplatform.composeapp.generated.resources.add_24dp
import minio_multiplatform.composeapp.generated.resources.chevron_left_24dp
import minio_multiplatform.composeapp.generated.resources.chevron_right_24dp
import minio_multiplatform.composeapp.generated.resources.external_hard_drive
import minio_multiplatform.composeapp.generated.resources.menu_24dp
import minio_multiplatform.composeapp.generated.resources.notifications_24dp
import minio_multiplatform.composeapp.generated.resources.search_24dp
import minio_multiplatform.composeapp.generated.resources.send_24dp
import minio_multiplatform.composeapp.generated.resources.visibility_24dp
import minio_multiplatform.composeapp.generated.resources.visibility_off_24dp
import org.example.project.data.AppViewModel
import org.example.project.data.UIMessages
import org.example.project.dto.FileType
import org.example.project.dto.getFileIcon
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import java.util.Locale

@Composable
fun MainPanel(
    modifier: Modifier,
    appViewModel: AppViewModel,
    onOpenDrawer: (() -> Unit)?
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(32.dp),
        modifier = modifier.padding(8.dp),
    ) {
        val scope = rememberCoroutineScope()

        TopBar(
            onClearSearchBar = {
                appViewModel.refreshCurrentDir()
            },
            onSearchFile = { file ->
                scope.launch {
                    appViewModel.search(file)
                }
            },
            onOpenDrawer = onOpenDrawer,
        )

        var pairWithNewDevice by remember { mutableStateOf(false) }
        val pairedDevices by appViewModel.pairedDevices.collectAsState()
        val networkDevices by appViewModel.networkDevices.collectAsState()

        if (pairWithNewDevice) {
            SelectPairingDevice(
                networkDevices = networkDevices,
                onPairWithDevice = { device ->
                    scope.launch {
                        appViewModel.pairWithNewDevice(device)
                    }
                },
                refreshNetworkDevices = {
                    scope.launch {
                        appViewModel.getNetworkDevices()
                    }
                },
                onDismissRequest = {
                    pairWithNewDevice = false
                },
            )
        }

        if (pairedDevices.isEmpty()) {
            ElevatedButton(
                onClick = {
                    pairWithNewDevice = true
                },
                colors = ButtonDefaults.elevatedButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(8.dp)
                    .pointerHoverIcon(PointerIcon.Hand),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.add_24dp),
                        contentDescription = "Pair With New Device",
                        modifier = Modifier.padding(4.dp)
                            .size(24.dp)
                    )

                    Text(
                        "Pair With New Device",
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                pairedDevices.forEach { device ->
                    DeviceCardExpanded(
                        device = device,
                        icon = Res.drawable.external_hard_drive,
                        onClick = {},
                    )
                }
            }
        }

        val selectedFileFilter by appViewModel.selectedFileFilter.collectAsState()
        QuickAccess(
            selectedFileFilter = selectedFileFilter,
            selectFileFilter = { filter ->
                appViewModel.selectFileFilter(filter)
            }
        )

        Box {
            Box {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    FilesActionBar(
                        appViewModel = appViewModel
                    )

                    val files by appViewModel.files.collectAsState()
                    val scope = rememberCoroutineScope()

                    FilesGrid(
                        appViewModel = appViewModel,
                    )
                }
            }

            Box(
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                var message by remember { mutableStateOf<UIMessages?>(null) }

                LaunchedEffect(Unit) {
                    appViewModel.uiMessages.collectLatest {
                        message = it
                        delay(3000)
                        message = null
                    }
                }

                message?.let { msg ->
                    val containerColor = when (msg) {
                        is UIMessages.Info -> MaterialTheme.colorScheme.primary
                        is UIMessages.Warn, is UIMessages.Error -> MaterialTheme.colorScheme.secondary
                    }

                    Column {
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically { it },
                            exit = slideOutVertically { it },
                        ) {
                            Card(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = containerColor,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                            ) {
                                Text(
                                    msg.message,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(
                                        horizontal = 24.dp,
                                        vertical = 16.dp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopBar(
    onClearSearchBar: () -> Unit,
    onSearchFile: (String) -> Unit,
    onOpenDrawer: (() -> Unit)?,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            onOpenDrawer?.let {
                IconButton(
                    onClick = onOpenDrawer,
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.menu_24dp),
                        contentDescription = "Menu",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            var text by remember { mutableStateOf("") }

            LaunchedEffect(text) {
                if (text.isEmpty()) {
                    onClearSearchBar()
                }
            }

            TextField(
                value = text,
                onValueChange = { newText ->
                    text = newText
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.search_24dp),
                        contentDescription = "Search Bar",
                        modifier = Modifier.size(24.dp)
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (text.isNotEmpty()) {
                                onSearchFile(text)
                            }
                        },
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.send_24dp),
                            contentDescription = "Search",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                shape = RoundedCornerShape(8.dp),
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
                textStyle = MaterialTheme.typography.headlineSmall,
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = {},
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.notifications_24dp),
                    contentDescription = "Notifications",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun QuickAccess(
    selectedFileFilter: FileType?,
    selectFileFilter: (FileType?) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Quick Access",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
        )

        val ignoredFileTypes = listOf(FileType.UNKNOWN, FileType.FOLDER)
        val items = FileType.entries
            .filter { fileType -> fileType !in ignoredFileTypes }
            .toList()

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            items.forEach { fileType ->
                val isSelected = selectedFileFilter == fileType

                FilterItem(
                    label = fileType.name,
                    icon = getFileIcon(fileType),
                    isSelected = isSelected,
                    onClick = {
                        // If user clicks on an already selected item, we deselect it
                        if (isSelected) {
                            selectFileFilter(null)
                            return@FilterItem
                        }

                        selectFileFilter(fileType)
                    },
                )
            }
        }
    }
}

@Composable
fun FilesActionBar(
    appViewModel: AppViewModel,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
            .padding(8.dp),
    ) {
        Text(
            "My Files",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    appViewModel.gotoPreviousDir()
                },
            ) {
                Icon(
                    painter = painterResource(Res.drawable.chevron_left_24dp),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }

            IconButton(
                onClick = {
                    appViewModel.gotoNextDir()
                },
            ) {
                Icon(
                    painter = painterResource(Res.drawable.chevron_right_24dp),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }

            IconButton(
                onClick = {
                    appViewModel.toggleHiddenFiles()
                },
            ) {
                val hidingHiddenFiles by appViewModel.hidingHiddenFiles.collectAsState()
                val resource = if (hidingHiddenFiles) {
                    Res.drawable.visibility_24dp
                } else {
                    Res.drawable.visibility_off_24dp
                }
                Icon(
                    painter = painterResource(resource),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
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
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else if (isHovered) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }
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
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                label.uppercase(Locale.getDefault()),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
