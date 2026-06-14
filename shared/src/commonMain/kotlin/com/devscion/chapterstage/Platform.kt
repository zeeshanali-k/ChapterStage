package com.devscion.chapterstage

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform