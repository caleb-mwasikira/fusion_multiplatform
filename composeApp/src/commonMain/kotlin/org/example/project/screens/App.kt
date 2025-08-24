package org.example.project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.example.project.data.AppViewModel
import org.example.project.screens.widgets.MainPanel
import org.example.project.screens.widgets.SidePanel
import org.example.project.screens.widgets.WindowSizeClass
import org.example.project.theme.AppTheme

@Composable
fun MobileScreen(
    appViewModel: AppViewModel,
    onUploadDirectory: () -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(400.dp)
            ) {
                val pairedDevices by appViewModel.pairedDevices.collectAsState()

                SidePanel(
                    pairedDevices = pairedDevices,
                    modifier = Modifier.fillMaxHeight()
                        .padding(8.dp),
                    onUploadDirectory = {
                        onUploadDirectory()
                        scope.launch {
                            drawerState.close()
                        }
                    },
                )
            }
        },
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            MainPanel(
                modifier = Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .padding(12.dp),
                appViewModel = appViewModel,
                onOpenDrawer = {
                    scope.launch {
                        if (drawerState.isOpen) drawerState.close() else drawerState.open()
                    }
                },
            )
        }
    }
}

@Composable
fun DesktopScreen(
    appViewModel: AppViewModel,
    onUploadDirectory: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Row(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)
        ) {
            val pairedDevices by appViewModel.pairedDevices.collectAsState()

            SidePanel(
                pairedDevices = pairedDevices,
                modifier = Modifier.fillMaxHeight()
                    .width(400.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp),
                onUploadDirectory = onUploadDirectory,
            )

            MainPanel(
                modifier = Modifier.fillMaxSize()
                    .weight(2f)
                    .padding(12.dp),
                onOpenDrawer = null,
                appViewModel = appViewModel,
            )
        }
    }
}

@Composable
fun App(
    windowSizeClass: WindowSizeClass,
    appViewModel: AppViewModel,
    onUploadDirectory: () -> Unit,
) {
    AppTheme {
        when (windowSizeClass) {
            WindowSizeClass.Compact, WindowSizeClass.Medium -> {
                MobileScreen(
                    appViewModel = appViewModel,
                    onUploadDirectory = onUploadDirectory,
                )
            }

            WindowSizeClass.Expanded -> {
                DesktopScreen(
                    appViewModel = appViewModel,
                    onUploadDirectory = onUploadDirectory,
                )
            }
        }
    }
}