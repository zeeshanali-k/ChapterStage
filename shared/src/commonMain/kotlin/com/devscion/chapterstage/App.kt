package com.devscion.chapterstage

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.devscion.chapterstage.design.ChapterStageTheme
import com.devscion.chapterstage.navigation.AgentTraceRoute
import com.devscion.chapterstage.navigation.CreateChapterRoute
import com.devscion.chapterstage.navigation.ExperienceViewerRoute
import com.devscion.chapterstage.navigation.GenerationProgressRoute
import com.devscion.chapterstage.navigation.GenerationSettingsRoute
import com.devscion.chapterstage.navigation.HomeRoute
import com.devscion.chapterstage.navigation.SplashRoute
import com.devscion.chapterstage.presentation.model.ChapterSourceDraft
import com.devscion.chapterstage.presentation.model.ChapterStageDemoContent
import com.devscion.chapterstage.presentation.model.GenerationSettingsDraft
import com.devscion.chapterstage.presentation.model.GenerationSnapshot
import com.devscion.chapterstage.presentation.model.SourceMode
import com.devscion.chapterstage.presentation.model.ViewerLoadState
import com.devscion.chapterstage.presentation.screens.AgentTraceScreen
import com.devscion.chapterstage.presentation.screens.CreateChapterScreen
import com.devscion.chapterstage.presentation.screens.ExperienceViewerScreen
import com.devscion.chapterstage.presentation.screens.GenerationProgressScreen
import com.devscion.chapterstage.presentation.screens.GenerationSettingsScreen
import com.devscion.chapterstage.presentation.screens.HomeScreen
import com.devscion.chapterstage.presentation.screens.SplashScreen
import kotlinx.coroutines.delay
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.core.logger.Level
import org.koin.dsl.koinConfiguration
import org.koin.dsl.module

@Composable
@Preview
fun App() {
    KoinApplication(
        configuration = koinConfiguration {
            modules(
                module {
                    single { ChapterStageDemoContent() }
                },
            )
        },
        logLevel = Level.ERROR,
    ) {
        ChapterStageTheme {
            ChapterStageApp()
        }
    }
}

@Composable
private fun ChapterStageApp() {
    val navController = rememberNavController()
    val content = koinInject<ChapterStageDemoContent>()

    var sourceMode by remember { mutableStateOf(SourceMode.PasteText) }
    var sourceDraft by remember { mutableStateOf(ChapterSourceDraft()) }
    var settings by remember { mutableStateOf(GenerationSettingsDraft()) }
    var generationRunKey by remember { mutableStateOf(0) }
    var snapshot by remember {
        mutableStateOf(
            GenerationSnapshot(
                statuses = content.agents.associate { it.id to com.devscion.chapterstage.presentation.model.AgentStatus.Waiting },
            ),
        )
    }
    var viewerState by remember { mutableStateOf(ViewerLoadState.Loaded) }
    var viewerRunKey by remember { mutableStateOf(0) }

    LaunchedEffect(generationRunKey) {
        if (generationRunKey == 0) return@LaunchedEffect

        snapshot = content.snapshotFor(
            revealedEventCount = 0,
            elapsedSeconds = 0,
            settings = settings,
        )
        for (eventCount in 1..content.traceEvents.size) {
            delay(720)
            snapshot = content.snapshotFor(
                revealedEventCount = eventCount,
                elapsedSeconds = (eventCount * 2).coerceAtMost(22),
                settings = settings,
            )
        }
    }

    LaunchedEffect(viewerRunKey) {
        if (viewerRunKey == 0) return@LaunchedEffect

        viewerState = ViewerLoadState.Loading
        delay(900)
        viewerState = ViewerLoadState.Loaded
    }

    NavHost(
        navController = navController,
        startDestination = SplashRoute,
        enterTransition = { stageEnterTransition(forward = true) },
        exitTransition = { stageExitTransition(forward = true) },
        popEnterTransition = { stageEnterTransition(forward = false) },
        popExitTransition = { stageExitTransition(forward = false) },
    ) {
        composable<SplashRoute> {
            SplashScreen(
                onCreateChapter = {
                    navController.navigate(HomeRoute)
                },
                onViewDemo = {
                    generationRunKey += 1
                    navController.navigate(GenerationProgressRoute)
                },
            )
        }
        composable<HomeRoute> {
            HomeScreen(
                recentJobs = content.recentJobs,
                onCreateChapter = { navController.navigate(CreateChapterRoute) },
                onOpenRecentJob = { job ->
                    if (job.status == "generating") {
                        snapshot = content.snapshotFor(
                            revealedEventCount = 7,
                            elapsedSeconds = 12,
                            settings = settings,
                        )
                        navController.navigate(GenerationProgressRoute)
                    } else {
                        snapshot = content.completedSnapshot(settings)
                        viewerState = ViewerLoadState.Loaded
                        navController.navigate(ExperienceViewerRoute)
                    }
                },
            )
        }
        composable<CreateChapterRoute> {
            CreateChapterScreen(
                sourceMode = sourceMode,
                draft = sourceDraft,
                sampleText = content.sampleText,
                onSourceModeChange = { sourceMode = it },
                onDraftChange = { sourceDraft = it },
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(GenerationSettingsRoute) },
            )
        }
        composable<GenerationSettingsRoute> {
            GenerationSettingsScreen(
                settings = settings,
                onSettingsChange = { settings = it },
                onBack = { navController.popBackStack() },
                onStartWorkflow = {
                    generationRunKey += 1
                    navController.navigate(GenerationProgressRoute)
                },
            )
        }
        composable<GenerationProgressRoute> {
            GenerationProgressScreen(
                agents = content.agents,
                snapshot = snapshot,
                settings = settings,
                chapterTitle = sourceDraft.chapterTitle.ifBlank { content.sampleChapterTitle },
                onBack = { navController.popBackStack() },
                onViewTrace = { navController.navigate(AgentTraceRoute) },
                onOpenViewer = {
                    viewerRunKey += 1
                    navController.navigate(ExperienceViewerRoute)
                },
            )
        }
        composable<AgentTraceRoute> {
            AgentTraceScreen(
                agents = content.agents,
                snapshot = snapshot,
                onBack = { navController.popBackStack() },
            )
        }
        composable<ExperienceViewerRoute> {
            ExperienceViewerScreen(
                state = viewerState,
                publicUrl = snapshot.publicUrl ?: content.sampleUrl,
                onBack = { navController.popBackStack() },
                onRetry = { viewerRunKey += 1 },
            )
        }
    }
}

private fun stageEnterTransition(forward: Boolean): EnterTransition =
    fadeIn(animationSpec = tween(durationMillis = 180)) +
        slideInHorizontally(animationSpec = tween(durationMillis = 260)) { fullWidth ->
            if (forward) fullWidth / 5 else -fullWidth / 5
        }

private fun stageExitTransition(forward: Boolean): ExitTransition =
    fadeOut(animationSpec = tween(durationMillis = 140)) +
        slideOutHorizontally(animationSpec = tween(durationMillis = 220)) { fullWidth ->
            if (forward) -fullWidth / 10 else fullWidth / 5
        }
