package com.devscion.chapterstage.data.repository

import com.devscion.chapterstage.data.mapper.toDomain
import com.devscion.chapterstage.data.mapper.toRequest
import com.devscion.chapterstage.data.remote.ChapterRemoteDataSource
import com.devscion.chapterstage.domain.model.Chapter
import com.devscion.chapterstage.domain.model.ChapterFile
import com.devscion.chapterstage.domain.model.ChapterInput
import com.devscion.chapterstage.domain.repository.ChapterRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
class ProductionChapterRepository(
    private val remoteDataSource: ChapterRemoteDataSource,
    @Named("io") private val ioDispatcher: CoroutineDispatcher,
) : ChapterRepository {
    override suspend fun createTextChapter(input: ChapterInput): Result<Chapter> =
        withContext(ioDispatcher) {
            remoteDataSource.createTextChapter(input.toRequest()).map { it.toDomain() }
        }

    override suspend fun uploadChapter(file: ChapterFile): Result<Chapter> =
        withContext(ioDispatcher) {
            remoteDataSource.uploadChapter(file).map { it.toDomain() }
        }
}
