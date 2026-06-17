package com.devscion.chapterstage.data.remote

import com.devscion.chapterstage.data.config.ChapterStageConfig
import com.devscion.chapterstage.data.dto.AgentTraceResponse
import com.devscion.chapterstage.data.dto.ChapterResponse
import com.devscion.chapterstage.data.dto.CreateTextChapterRequest
import com.devscion.chapterstage.data.dto.ExperienceMetadataResponse
import com.devscion.chapterstage.data.dto.GenerationJobResponse
import com.devscion.chapterstage.data.dto.GenerationJobStartResponse
import com.devscion.chapterstage.data.dto.RecentGenerationJobsResponse
import com.devscion.chapterstage.data.dto.StartGenerationJobRequest
import com.devscion.chapterstage.domain.model.ChapterFile
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentDisposition
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import org.koin.core.annotation.Single

@Single
class ChapterStageApi(
    private val client: HttpClient,
    private val config: ChapterStageConfig,
) {
    suspend fun health(): String = client.get(endpoint("health")).body()

    suspend fun createTextChapter(request: CreateTextChapterRequest): ChapterResponse =
        client.post(endpoint("chapters/text")) {
            setBody(request)
        }.body()

    suspend fun uploadChapter(file: ChapterFile): ChapterResponse =
        client.submitFormWithBinaryData(
            url = endpoint("chapters/upload"),
            formData = formData {
                append(
                    key = "file",
                    value = file.bytes,
                    headers = Headers.build {
                        append(
                            name = HttpHeaders.ContentDisposition,
                            value = ContentDisposition.File.withParameter("filename", file.fileName).toString(),
                        )
                        append(name = HttpHeaders.ContentType, value = file.contentType)
                    },
                )
            },
        ).body()

    suspend fun startGenerationJob(request: StartGenerationJobRequest): GenerationJobStartResponse =
        client.post(endpoint("generation-jobs")) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun getGenerationJob(jobId: String): GenerationJobResponse =
        client.get(endpoint("generation-jobs/$jobId")).body()

    suspend fun getTrace(jobId: String): AgentTraceResponse =
        client.get(endpoint("generation-jobs/$jobId/trace")).body()

    suspend fun getRecentJobs(): RecentGenerationJobsResponse =
        client.get(endpoint("generation-jobs?limit=20&offset=0")).body()

    suspend fun getExperienceMetadata(experienceId: String): ExperienceMetadataResponse =
        client.get(endpoint("experiences/$experienceId")).body()

    private fun endpoint(path: String): String =
        config.apiBaseUrl.trimEnd('/') + "/" + path.trimStart('/')
}
