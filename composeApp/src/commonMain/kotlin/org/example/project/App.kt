package org.example.project

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.example.project.theme.AppTheme

@Composable
fun MobileScreen() {
    val drawerState = rememberDrawerState(DrawerValue.Open)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(400.dp)
            ) {
                SidePanel(
                    modifier = Modifier.fillMaxHeight()
                        .padding(8.dp)
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
                    .padding(24.dp),
                onOpenDrawer = {
                    scope.launch {
                        if (drawerState.isOpen) drawerState.close() else drawerState.open()
                    }
                }
            )
        }
    }
}

@Composable
fun DesktopScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Row(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)
        ) {
            SidePanel(
                modifier = Modifier.fillMaxHeight()
                    .width(400.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(24.dp)
            )

            MainPanel(
                modifier = Modifier.fillMaxSize()
                    .weight(2f)
                    .padding(24.dp),
                onOpenDrawer = null,
            )
        }
    }
}

@Composable
fun App(windowSizeClass: WindowSizeClass) {
    AppTheme {
        when (windowSizeClass) {
            WindowSizeClass.Compact -> {
                MobileScreen()
            }

            WindowSizeClass.Medium -> {
                MobileScreen()
            }

            WindowSizeClass.Expanded -> {
                DesktopScreen()
            }
        }
    }
}