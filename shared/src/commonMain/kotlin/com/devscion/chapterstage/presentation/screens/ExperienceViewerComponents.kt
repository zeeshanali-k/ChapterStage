package com.devscion.chapterstage.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.devscion.chapterstage.design.spacing
import com.devscion.chapterstage.design.stageColors
import com.devscion.chapterstage.presentation.components.BackButton
import com.devscion.chapterstage.presentation.components.StageButton
import com.devscion.chapterstage.presentation.components.StageButtonVariant
import com.devscion.chapterstage.presentation.components.StageCard
import com.devscion.chapterstage.presentation.components.StageLabel

@Composable
internal fun ViewerTopBar(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    onOpenExternal: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        BackButton(onClick = onBack)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = MaterialTheme.stageColors.textPrimary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                color = MaterialTheme.stageColors.textTertiary,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        val controlShape = MaterialTheme.shapes.large
        Surface(
            modifier = Modifier
                .size(38.dp)
                .clip(controlShape)
                .clickable(onClick = onOpenExternal),
            shape = controlShape,
            color = Color.White.copy(alpha = 0.04f),
            border = BorderStroke(1.dp, MaterialTheme.stageColors.line),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "EX",
                    color = MaterialTheme.stageColors.textSecondary,
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                )
            }
        }
    }
}

@Composable
internal fun BrowserChrome(
    publicUrl: String,
    modifier: Modifier = Modifier,
) {
    val chromeShape = MaterialTheme.shapes.medium

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(chromeShape)
            .background(MaterialTheme.stageColors.backgroundHigh)
            .border(BorderStroke(1.dp, MaterialTheme.stageColors.line), chromeShape)
            .padding(horizontal = MaterialTheme.spacing.medium, vertical = MaterialTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
    ) {
        listOf(MaterialTheme.stageColors.error, MaterialTheme.stageColors.warning, MaterialTheme.stageColors.success)
            .forEach { color ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.72f)),
                )
            }
        Surface(
            modifier = Modifier.weight(1f),
            shape = MaterialTheme.shapes.small,
            color = Color.White.copy(alpha = 0.03f),
        ) {
            Text(
                modifier = Modifier.padding(vertical = MaterialTheme.spacing.xSmall),
                text = publicUrl,
                color = MaterialTheme.stageColors.textTertiary,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
internal fun ViewerLoadingState(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            modifier = Modifier.padding(bottom = MaterialTheme.spacing.large),
        ) {
            repeat(4) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == 0) MaterialTheme.stageColors.primary else Color.White.copy(alpha = 0.1f),
                        ),
                )
            }
        }
        LoadingBlock(height = 180.dp)
        LoadingLine(widthFraction = 0.7f)
        LoadingLine(widthFraction = 0.9f)
        LoadingLine(widthFraction = 0.6f)
        LoadingBlock(height = 140.dp)
        Text(
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium),
            text = "Loading interactive chapter...",
            color = MaterialTheme.stageColors.textTertiary,
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
        )
    }
}

@Composable
internal fun ViewerErrorState(
    message: String?,
    onRetry: () -> Unit,
    onOpenExternal: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.extraLarge),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.stageColors.error.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, MaterialTheme.stageColors.error.copy(alpha = 0.3f)),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "RT",
                    color = MaterialTheme.stageColors.error,
                    style = MaterialTheme.typography.labelLarge,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Text(
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium),
            text = "Couldn't load the experience",
            color = MaterialTheme.stageColors.textPrimary,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Text(
            modifier = Modifier.padding(top = MaterialTheme.spacing.small),
            text = message ?: "The hosted chapter did not respond. Check your connection and try again.",
            color = MaterialTheme.stageColors.textSecondary,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = MaterialTheme.spacing.large),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        ) {
            StageButton(
                modifier = Modifier.weight(1f),
                text = "Retry",
                onClick = onRetry,
                leadingText = "RT",
            )
            StageButton(
                modifier = Modifier.weight(1f),
                text = "Open Link",
                onClick = onOpenExternal,
                variant = StageButtonVariant.Ghost,
                leadingText = "EX",
            )
        }
    }
}

@Composable
internal fun HostedExperienceReady(
    publicUrl: String,
    onOpenExternal: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(MaterialTheme.spacing.large),
        verticalArrangement = Arrangement.Center,
    ) {
        StageCard(
            accent = MaterialTheme.stageColors.success,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(MaterialTheme.spacing.large),
        ) {
            StageLabel(
                text = "READY",
                color = MaterialTheme.stageColors.success,
            )
            Text(
                modifier = Modifier.padding(top = MaterialTheme.spacing.medium),
                text = "Generated chapter",
                color = MaterialTheme.stageColors.textPrimary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                modifier = Modifier.padding(top = MaterialTheme.spacing.small),
                text = publicUrl,
                color = MaterialTheme.stageColors.textTertiary,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
            )
            StageButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = MaterialTheme.spacing.large),
                text = "Open Experience",
                onClick = onOpenExternal,
                leadingText = "EX",
            )
        }
    }
}

@Composable
private fun LoadingBlock(
    height: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .padding(bottom = MaterialTheme.spacing.medium)
            .clip(MaterialTheme.shapes.large)
            .background(Color.White.copy(alpha = 0.05f)),
    )
}

@Composable
private fun LoadingLine(
    widthFraction: Float,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth(widthFraction)
            .height(14.dp)
            .padding(bottom = MaterialTheme.spacing.medium)
            .clip(MaterialTheme.shapes.extraSmall)
            .background(Color.White.copy(alpha = 0.05f)),
    )
}
