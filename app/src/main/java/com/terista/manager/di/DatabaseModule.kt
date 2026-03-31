package com.terista.manager.di

import android.content.Context
import androidx.room.Room
import com.terista.manager.data.local.FavoriteDao
import com.terista.manager.data.local.FavoritesDatabase
import com.terista.manager.data.local.RecycleBinDatabase
import com.terista.manager.data.local.RecycleDao
import com.terista.manager.domain.repository.FavoritesRepository
import com.terista.manager.domain.repository.RecycleBinRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideFavoritesDatabase(@ApplicationContext context: Context): FavoritesDatabase {
        return Room.databaseBuilder(
            context,
            FavoritesDatabase::class.java,
            "favorites.db"
        )
        .fallbackToDestructiveMigration() // For dev
        .build()
    }
    
    @Provides
    fun provideFavoriteDao(db: FavoritesDatabase): FavoriteDao = db.favoriteDao()
    
    @Provides
    @Singleton
    fun provideFavoritesRepository(dao: FavoriteDao): FavoritesRepository {
        return com.terista.manager.data.repository.FavoritesRepositoryImpl(dao)
    }
    
    @Provides
    @Singleton
    fun provideRecycleBinDatabase(@ApplicationContext context: Context): RecycleBinDatabase {
        return Room.databaseBuilder(
            context,
            RecycleBinDatabase::class.java,
            "recycle.db"
        )
        .fallbackToDestructiveMigration() // For dev
        .build()
    }
    
    @Provides
    fun provideRecycleDao(db: RecycleBinDatabase): RecycleDao = db.recycleDao()
    
    @Provides
    @Singleton
    fun provideRecycleBinRepository(dao: RecycleDao): RecycleBinRepository {
        return com.terista.manager.data.repository.RecycleBinRepositoryImpl(dao)
    }
}