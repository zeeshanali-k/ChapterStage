package com.devscion.chapterstage.design

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Shape

@OptIn(ExperimentalSharedTransitionApi::class)
class StageSharedMotionScope(
    val sharedTransitionScope: SharedTransitionScope,
    val animatedVisibilityScope: AnimatedVisibilityScope,
)

@OptIn(ExperimentalSharedTransitionApi::class)
private val LocalStageSharedMotionScope = staticCompositionLocalOf<StageSharedMotionScope?> { null }

@OptIn(ExperimentalSharedTransitionApi::class)
private val StageBoundsTransform = BoundsTransform { _, _ ->
    spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMediumLow,
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun StageSharedMotionProvider(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalStageSharedMotionScope provides StageSharedMotionScope(
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope,
        ),
        content = content,
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
fun Modifier.stageSharedBounds(
    sharedKey: String?,
    shape: Shape,
    zIndexInOverlay: Float = 0f,
): Modifier {
    if (sharedKey == null) return this

    return composed {
        val motionScope = LocalStageSharedMotionScope.current
        if (motionScope == null) {
            this
        } else {
            val baseModifier = this
            with(motionScope.sharedTransitionScope) {
                baseModifier.sharedBounds(
                    sharedContentState = rememberSharedContentState(key = sharedKey),
                    animatedVisibilityScope = motionScope.animatedVisibilityScope,
                    enter = SharedBoundsEnter,
                    exit = SharedBoundsExit,
                    boundsTransform = StageBoundsTransform,
                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                    clipInOverlayDuringTransition = OverlayClip(shape),
                    zIndexInOverlay = zIndexInOverlay,
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
fun Modifier.stageSharedElement(
    sharedKey: String?,
    shape: Shape,
    zIndexInOverlay: Float = 1f,
): Modifier {
    if (sharedKey == null) return this

    return composed {
        val motionScope = LocalStageSharedMotionScope.current
        if (motionScope == null) {
            this
        } else {
            val baseModifier = this
            with(motionScope.sharedTransitionScope) {
                baseModifier.sharedElement(
                    sharedContentState = rememberSharedContentState(key = sharedKey),
                    animatedVisibilityScope = motionScope.animatedVisibilityScope,
                    boundsTransform = StageBoundsTransform,
                    clipInOverlayDuringTransition = OverlayClip(shape),
                    zIndexInOverlay = zIndexInOverlay,
                )
            }
        }
    }
}

private val SharedBoundsEnter: EnterTransition = fadeIn(animationSpec = tween(durationMillis = 170))
private val SharedBoundsExit: ExitTransition = fadeOut(animationSpec = tween(durationMillis = 120))
