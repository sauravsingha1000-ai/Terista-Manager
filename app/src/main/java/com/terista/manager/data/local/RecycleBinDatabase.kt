// data/local/RecycleBinDatabase.kt
package com.terista.manager.data.local

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "recycle_bin")
data class RecycleItem(
    @PrimaryKey val path: String,
    val name: String,
    val originalPath: String,
    val size: Long,
    val deletedTime: Long = System.currentTimeMillis()
)

@Database(entities = [RecycleItem::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RecycleBinDatabase : RoomDatabase() {
    abstract fun recycleDao(): RecycleDao
}

@androidx.room.Dao
interface RecycleDao {
    @Query("SELECT * FROM recycle_bin ORDER BY deletedTime DESC")
    fun getAll(): Flow<List<RecycleItem>>
    
    @Insert
    suspend fun moveToBin(item: RecycleItem)
    
    @Query("DELETE FROM recycle_bin WHERE path = :path")
    suspend fun restore(path: String)
    
    @Query("DELETE FROM recycle_bin")
    suspend fun emptyBin()
}