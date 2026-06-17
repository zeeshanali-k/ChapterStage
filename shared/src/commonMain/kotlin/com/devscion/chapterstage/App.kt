package com.devscion.chapterstage

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.devscion.chapterstage.design.ChapterStageTheme
import com.devscion.chapterstage.design.StageSharedMotionProvider
import com.devscion.chapterstage.di.AppModule
import com.devscion.chapterstage.di.module as appModule
import com.devscion.chapterstage.navigation.AgentTraceRoute
import com.devscion.chapterstage.navigation.CreateChapterRoute
import com.devscion.chapterstage.navigation.ExperienceViewerRoute
import com.devscion.chapterstage.navigation.GenerationProgressRoute
import com.devscion.chapterstage.navigation.GenerationSettingsRoute
import com.devscion.chapterstage.navigation.HomeRoute
import com.devscion.chapterstage.navigation.SplashRoute
import com.devscion.chapterstage.presentation.screens.AgentTraceScreen
import com.devscion.chapterstage.presentation.screens.CreateChapterScreen
import com.devscion.chapterstage.presentation.screens.ExperienceViewerScreen
import com.devscion.chapterstage.presentation.screens.GenerationProgressScreen
import com.devscion.chapterstage.presentation.screens.GenerationSettingsScreen
import com.devscion.chapterstage.presentation.screens.HomeScreen
import com.devscion.chapterstage.presentation.screens.SplashScreen
import com.devscion.chapterstage.presentation.state.ChapterStageAction
import com.devscion.chapterstage.presentation.state.ChapterStageEvent
import com.devscion.chapterstage.presentation.state.ChapterStageViewModel
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.logger.Level
import org.koin.dsl.koinConfiguration

@Composable
@Preview
fun App() {
    KoinApplication(
        configuration = koinConfiguration {
            modules(AppModule().appModule())
        },
        logLevel = Level.ERROR,
    ) {
        ChapterStageTheme {
            ChapterStageApp()
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ChapterStageApp() {
    val navController = rememberNavController()
    val viewModel = koinViewModel<ChapterStageViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(viewModel, navController) {
        viewModel.events.collect { event ->
            when (event) {
                ChapterStageEvent.NavigateHome -> navController.navigate(HomeRoute)
                ChapterStageEvent.NavigateCreateChapter -> navController.navigate(CreateChapterRoute)
                ChapterStageEvent.NavigateGenerationSettings -> navController.navigate(GenerationSettingsRoute)
                ChapterStageEvent.NavigateGenerationProgress -> navController.navigate(GenerationProgressRoute)
                ChapterStageEvent.NavigateAgentTrace -> navController.navigate(AgentTraceRoute)
                ChapterStageEvent.NavigateExperienceViewer -> navController.navigate(ExperienceViewerRoute)
                ChapterStageEvent.NavigateBack -> navController.popBackStack()
            }
        }
    }

    SharedTransitionLayout {
        val sharedTransitionScope = this

        NavHost(
            navController = navController,
            startDestination = SplashRoute,
            enterTransition = { stageEnterTransition(forward = true) },
            exitTransition = { stageExitTransition(forward = true) },
            popEnterTransition = { stageEnterTransition(forward = false) },
            popExitTransition = { stageExitTransition(forward = false) },
        ) {
            composable<SplashRoute> {
                StageSharedMotionProvider(
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = this,
                ) {
                    SplashScreen(
                        onCreateChapter = {
                            viewModel.onAction(ChapterStageAction.NavigateCreateChapter)
                        },
                        onViewDemo = {
                            viewModel.onAction(ChapterStageAction.StartDemoFlow)
                        },
                    )
                }
            }
            composable<HomeRoute> {
                StageSharedMotionProvider(
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = this,
                ) {
                    HomeScreen(
                        recentJobs = state.recentJobs,
                        isLoading = state.isHomeLoading,
                        errorMessage = state.homeErrorMessage,
                        onCreateChapter = { viewModel.onAction(ChapterStageAction.NavigateCreateChapter) },
                        onOpenRecentJob = { job ->
                            viewModel.onAction(ChapterStageAction.OpenRecentJob(job))
                        },
                        onRetryRecentJobs = { viewModel.onAction(ChapterStageAction.LoadHome) },
                        onDismissError = { viewModel.onAction(ChapterStageAction.DismissError) },
                    )
                }
            }
            composable<CreateChapterRoute> {
                StageSharedMotionProvider(
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = this,
                ) {
                    CreateChapterScreen(
                        sourceMode = state.sourceMode,
                        draft = state.sourceDraft,
                        sampleText = state.sampleText,
                        isPickingFile = state.isPickingFile,
                        errorMessage = state.createErrorMessage,
                        onSourceModeChange = { viewModel.onAction(ChapterStageAction.ChangeSourceMode(it)) },
                        onDraftChange = { viewModel.onAction(ChapterStageAction.UpdateSourceDraft(it)) },
                        onPickFile = { viewModel.onAction(ChapterStageAction.PickChapterFile) },
                        onFileSelected = { file -> viewModel.onAction(ChapterStageAction.SelectChapterFile(file)) },
                        onFilePickCancelled = { viewModel.onAction(ChapterStageAction.CancelChapterFilePicker) },
                        onFilePickFailed = { message ->
                            viewModel.onAction(ChapterStageAction.FilePickerFailed(message))
                        },
                        onRemoveFile = { viewModel.onAction(ChapterStageAction.RemoveChapterFile) },
                        onBack = { viewModel.onAction(ChapterStageAction.NavigateBack) },
                        onContinue = { viewModel.onAction(ChapterStageAction.ContinueToSettings) },
                        onDismissError = { viewModel.onAction(ChapterStageAction.DismissError) },
                    )
                }
            }
            composable<GenerationSettingsRoute> {
                StageSharedMotionProvider(
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = this,
                ) {
                    GenerationSettingsScreen(
                        settings = state.settings,
                        isStartingWorkflow = state.isStartingWorkflow,
                        errorMessage = state.settingsErrorMessage,
                        onSettingsChange = { viewModel.onAction(ChapterStageAction.UpdateSettings(it)) },
                        onBack = { viewModel.onAction(ChapterStageAction.NavigateBack) },
                        onStartWorkflow = {
                            viewModel.onAction(ChapterStageAction.StartWorkflow)
                        },
                        onDismissError = { viewModel.onAction(ChapterStageAction.DismissError) },
                    )
                }
            }
            composable<GenerationProgressRoute> {
                StageSharedMotionProvider(
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = this,
                ) {
                    GenerationProgressScreen(
                        agents = state.agents,
                        snapshot = state.snapshot,
                        settings = state.settings,
                        chapterTitle = state.sourceDraft.chapterTitle.ifBlank { state.sampleChapterTitle },
                        errorMessage = state.progressErrorMessage,
                        onBack = { viewModel.onAction(ChapterStageAction.NavigateBack) },
                        onViewTrace = { viewModel.onAction(ChapterStageAction.ViewTrace) },
                        onOpenViewer = {
                            viewModel.onAction(ChapterStageAction.OpenViewer)
                        },
                        onRetry = { viewModel.onAction(ChapterStageAction.RetryProgress) },
                        onDismissError = { viewModel.onAction(ChapterStageAction.DismissError) },
                    )
                }
            }
            composable<AgentTraceRoute> {
                StageSharedMotionProvider(
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = this,
                ) {
                    AgentTraceScreen(
                        agents = state.agents,
                        snapshot = state.snapshot,
                        errorMessage = state.traceErrorMessage,
                        onBack = { viewModel.onAction(ChapterStageAction.NavigateBack) },
                        onRetry = { viewModel.onAction(ChapterStageAction.RetryTrace) },
                        onDismissError = { viewModel.onAction(ChapterStageAction.DismissError) },
                    )
                }
            }
            composable<ExperienceViewerRoute> {
                StageSharedMotionProvider(
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = this,
                ) {
                    val publicUrl = state.snapshot.publicUrl ?: "chapterstage.app/loading"
                    ExperienceViewerScreen(
                        state = state.viewerState,
                        publicUrl = publicUrl,
                        title = state.snapshot.title
                            ?: state.sourceDraft.chapterTitle.ifBlank { state.sampleChapterTitle },
                        subtitle = state.snapshot.subtitle
                            ?: state.sourceDraft.bookTitle.ifBlank { "Generated experience" },
                        errorMessage = state.viewerErrorMessage,
                        onBack = { viewModel.onAction(ChapterStageAction.NavigateBack) },
                        onRetry = { viewModel.onAction(ChapterStageAction.RetryViewer) },
                        onOpenExternal = {
                            runCatching {
                                uriHandler.openUri(publicUrl.withHttpsScheme())
                            }.onFailure {
                                viewModel.onAction(ChapterStageAction.ExternalViewerOpenFailed)
                            }
                        },
                    )
                }
            }
        }
    }
}

private fun String.withHttpsScheme(): String =
    if (startsWith("http://") || startsWith("https://")) this else "https://$this"

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
