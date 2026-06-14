package com.devscion.chapterstage.di

//import com.devscion.chapterstage.AppFlavor
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.devscion.chapterstage")
class AppModule {
    @Single
    fun provideHttpClient(): HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
                this.explicitNulls = false
                this.prettyPrint = true
            })
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
    }

//    Following Pattern should be followed for prod and development environment's repos. In development mode we use mock data for quick prototyping and simulating the UI
//    Production repos have actual api calls and other related logic
//    @Single
//    fun provideAuthRepository(authApiService: AuthApiService): AuthRepository =
//        if (AppFlavor.USE_MOCK_DATA) MockAuthRepository()
//        else AuthRepositoryImpl(authApiService)

}
