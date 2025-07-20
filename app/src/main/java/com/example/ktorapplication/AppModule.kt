package com.example.ktorapplication

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun ktor():HttpClient = ktorClient

    @Provides
    @Singleton
    fun repository():RepositoryImpl = RepositoryImpl(ktorClient)
}