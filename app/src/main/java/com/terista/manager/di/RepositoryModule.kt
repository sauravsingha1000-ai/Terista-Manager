package com.terista.manager.di

import com.terista.manager.data.repository.FileRepositoryImpl
import com.terista.manager.domain.repository.FileRepository
import com.terista.manager.domain.usecase.GetFilesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideFileRepository(): FileRepository {
        return FileRepositoryImpl()
    }
    
    @Provides
    @Singleton
    fun provideGetFilesUseCase(
        fileRepository: FileRepository
    ): GetFilesUseCase {
        return GetFilesUseCase(fileRepository)
    }
}