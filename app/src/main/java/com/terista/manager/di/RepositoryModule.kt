package com.terista.manager.di

import android.content.Context
import androidx.room.Room
import com.terista.manager.data.local.FavoriteDao
import com.terista.manager.data.local.FavoritesDatabase
import com.terista.manager.data.repository.FileRepositoryImpl
import com.terista.manager.data.repository.FavoritesRepositoryImpl
import com.terista.manager.domain.repository.FileRepository
import com.terista.manager.domain.repository.FavoritesRepository
import com.terista.manager.domain.usecase.GetFilesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    // Existing File Repository
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

    // ✅ NEW: Favorites Database
    @Provides
    @Singleton
    fun provideFavoritesDatabase(
        @ApplicationContext context: Context
    ): FavoritesDatabase {
        return Room.databaseBuilder(
            context,
            FavoritesDatabase::class.java,
            "favorites_db"
        ).build()
    }

    // ✅ NEW: DAO
    @Provides
    @Singleton
    fun provideFavoriteDao(
        db: FavoritesDatabase
    ): FavoriteDao {
        return db.favoriteDao()
    }

    // ✅ NEW: Favorites Repository
    @Provides
    @Singleton
    fun provideFavoritesRepository(
        dao: FavoriteDao
    ): FavoritesRepository {
        return FavoritesRepositoryImpl(dao)
    }
}
