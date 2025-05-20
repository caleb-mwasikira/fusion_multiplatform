package org.example.project.platform_specific

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.example.project.data.DirEntry
import org.jetbrains.compose.resources.painterResource

@Composable
expect fun FilesGrid(
    files: List<DirEntry>,
    changeWorkingDir: (String) -> Unit,
)

@Composable
fun FileItemCard(
    file: DirEntry,
    modifier: Modifier,
    isSelected: Boolean = false,
    content: @Composable (BoxScope.() -> Unit),
) {
    val localInteractionSource = remember { MutableInteractionSource() }
    val isHovered by localInteractionSource.collectIsHoveredAsState()
    val containerColor =
        if (isHovered) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.background

    Box(
        modifier = modifier.hoverable(localInteractionSource),
    ) {
        Card(
            colors = CardDefaults.cardColors().copy(
                containerColor = containerColor,
                contentColor = MaterialTheme.colorScheme.scrim,
            ),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
                    .padding(12.dp),
            ) {
                Image(
                    painter = painterResource(file.fileType.icon),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )

                Text(
                    file.name,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.scrim
                    },
                    modifier = Modifier
                        .background(
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                containerColor
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                )
            }
        }

        Box(
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            content()
        }
    }
}
