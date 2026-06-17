package com.devscion.chapterstage.data.remote

import com.devscion.chapterstage.data.dto.ChapterResponse
import com.devscion.chapterstage.data.dto.CreateTextChapterRequest
import com.devscion.chapterstage.domain.model.ChapterFile
import org.koin.core.annotation.Single

@Single
class ChapterRemoteDataSource(
    private val api: ChapterStageApi,
) {
    suspend fun createTextChapter(request: CreateTextChapterRequest): Result<ChapterResponse> =
        apiResult {
            api.createTextChapter(request)
        }

    suspend fun uploadChapter(file: ChapterFile): Result<ChapterResponse> =
        apiResult {
            api.uploadChapter(file)
        }
}
