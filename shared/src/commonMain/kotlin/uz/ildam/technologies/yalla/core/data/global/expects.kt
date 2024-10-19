package uz.ildam.technologies.yalla.core.data.global

import io.ktor.client.HttpClient

expect fun provideHttpClientForApi1(): HttpClient
expect fun provideHttpClientForApi2(): HttpClient
