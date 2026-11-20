package com.example.whattowear

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform