package com.devscion.chapterstage.domain.model

data class ChapterFile(
    val fileName: String,
    val bytes: ByteArray,
    val contentType: String,
) {
    val extension: String = fileName.substringAfterLast('.', missingDelimiterValue = "").lowercase()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChapterFile) return false

        if (fileName != other.fileName) return false
        if (!bytes.contentEquals(other.bytes)) return false
        if (contentType != other.contentType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fileName.hashCode()
        result = 31 * result + bytes.contentHashCode()
        result = 31 * result + contentType.hashCode()
        return result
    }
}
