package com.devscion.chapterstage.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devscion.chapterstage.design.ChapterStageTheme
import com.devscion.chapterstage.design.stageColors
import com.devscion.chapterstage.design.spacing
import com.devscion.chapterstage.presentation.components.ChapterStageLogo
import com.devscion.chapterstage.presentation.components.PlanetSystemArtwork
import com.devscion.chapterstage.presentation.components.StageButton
import com.devscion.chapterstage.presentation.components.StageButtonVariant
import com.devscion.chapterstage.presentation.components.StageScreen

@Composable
fun SplashScreen(
    onCreateChapter: () -> Unit,
    onViewDemo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    StageScreen(
        modifier = modifier,
        scroll = false,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.widthIn(max = MaterialTheme.spacing.maxPaneWidth),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PlanetSystemArtwork()
            ChapterStageLogo()
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
            Text(
                text = "Books become\ninteractive lessons",
                color = MaterialTheme.stageColors.textPrimary,
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                modifier = Modifier.padding(
                    top = MaterialTheme.spacing.medium,
                    start = MaterialTheme.spacing.medium,
                    end = MaterialTheme.spacing.medium,
                ),
                text = buildAnnotatedString {
                    append("Powered by ")
                    withStyle(SpanStyle(color = MaterialTheme.stageColors.primaryText, fontWeight = FontWeight.SemiBold)) {
                        append("Band")
                    }
                    append(" multi-agent collaboration - a studio of specialists that build a chapter experience for you.")
                },
                color = MaterialTheme.stageColors.textSecondary,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))
            StageButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Create Chapter Experience",
                onClick = onCreateChapter,
                large = true,
                trailingText = "->",
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            StageButton(
                modifier = Modifier.fillMaxWidth(),
                text = "View demo flow",
                onClick = onViewDemo,
                variant = StageButtonVariant.Ghost,
                leadingText = ">",
            )
        }
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    ChapterStageTheme {
        SplashScreen(
            onCreateChapter = {},
            onViewDemo = {},
        )
    }
}

