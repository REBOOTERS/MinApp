/*
 * Copyright (C) 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.engineer.android.mini.coroutines.old

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Entity
data class Title constructor(val title: String, val create_time: String?, @PrimaryKey val id: Int)


data class Address(val code: String, val city: String)

@Entity
data class Image constructor(
    val path: String, val format: String, val size: Int, @PrimaryKey val id: Int
)

@Dao
interface TitleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTitle(title: Title)

    @get:Query("select * from Title where id > 0")
    val titleLiveData: LiveData<Title?>
}

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveImage(image: Image)
}

private const val DATA_BASE_NAME = "titles.db"
private const val DATA_BASE_VERSION = 4


@Database(
    entities = [Title::class, Image::class],
    version = DATA_BASE_VERSION,
    autoMigrations = [AutoMigration(2, 3, TitleDatabase.ReNameColumnMigration::class)],
    exportSchema = true
)
abstract class TitleDatabase : RoomDatabase() {
    abstract val titleDao: TitleDao

    abstract val imageDao: ImageDao

    @RenameColumn(
        tableName = "Title", fromColumnName = "createTime", toColumnName = "create_time"
    )
    class ReNameColumnMigration : AutoMigrationSpec {
        override fun onPostMigrate(db: SupportSQLiteDatabase) {
            super.onPostMigrate(db)
            Log.d("TAG", "onPostMigrate() called with: db = $db")
        }
    }
}

private lateinit var INSTANCE: TitleDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Title ADD COLUMN createTime TEXT")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `Image` (`id` INTEGER, `path` TEXT,`format` TEXT, `size` INTEGER,PRIMARY KEY(`id`))"
        )
    }
}


fun getDatabase(context: Context): TitleDatabase {

    synchronized(TitleDatabase::class) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext, TitleDatabase::class.java, DATA_BASE_NAME
            ).addMigrations(MIGRATION_1_2, MIGRATION_3_4).fallbackToDestructiveMigration().build()
        }
    }
    return INSTANCE
}
