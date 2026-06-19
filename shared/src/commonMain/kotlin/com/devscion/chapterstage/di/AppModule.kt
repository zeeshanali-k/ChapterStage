package com.devscion.chapterstage.di

import com.devscion.chapterstage.AppFlavor
import com.devscion.chapterstage.data.config.ChapterStageConfig
import com.devscion.chapterstage.data.repository.MockChapterRepository
import com.devscion.chapterstage.data.repository.MockGenerationRepository
import com.devscion.chapterstage.data.repository.ProductionChapterRepository
import com.devscion.chapterstage.data.repository.ProductionGenerationRepository
import com.devscion.chapterstage.domain.repository.ChapterRepository
import com.devscion.chapterstage.domain.repository.GenerationRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.sse.SSE
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.devscion.chapterstage")
class AppModule {

    @Single
    fun provideChapterStageConfig(): ChapterStageConfig =
        ChapterStageConfig(
            apiBaseUrl = AppFlavor.API_BASE_URL,
            useMockData = AppFlavor.USE_MOCK_DATA,
        )

    @Single
    @Named("io")
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Single
    fun provideHttpClient(config: ChapterStageConfig): HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
                explicitNulls = false
                prettyPrint = AppFlavor.USE_MOCK_DATA
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 30_000
        }
        install(SSE)
        install(Logging) {
            logger = Logger.DEFAULT
            level = if (config.useMockData) LogLevel.BODY else LogLevel.NONE
        }
        defaultRequest {
            contentType(ContentType.Application.Json)
        }
    }

    @Single
    fun provideChapterRepository(
        mockRepository: MockChapterRepository,
        productionRepository: ProductionChapterRepository,
        config: ChapterStageConfig,
    ): ChapterRepository =
        if (config.useMockData) mockRepository else productionRepository

    @Single
    fun provideGenerationRepository(
        mockRepository: MockGenerationRepository,
        productionRepository: ProductionGenerationRepository,
        config: ChapterStageConfig,
    ): GenerationRepository =
        if (config.useMockData) mockRepository else productionRepository
}
