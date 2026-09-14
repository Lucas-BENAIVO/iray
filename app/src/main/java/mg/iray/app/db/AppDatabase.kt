package mg.iray.app.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import mg.iray.app.dao.MediaDao
import mg.iray.app.dao.NotificationDao
import mg.iray.app.dao.SignalementDao
import mg.iray.app.dao.TaskDao
import mg.iray.app.dao.UserDao
import mg.iray.app.dao.UserProfileDao
import mg.iray.app.entity.MediaEntity
import mg.iray.app.entity.NotificationEntity
import mg.iray.app.entity.SignalementEntity
import mg.iray.app.entity.TaskEntity
import mg.iray.app.entity.UserEntity
import mg.iray.app.entity.UserProfileEntity

@Database(
    entities = [
        TaskEntity::class,
        UserEntity::class,
        UserProfileEntity::class,
        MediaEntity::class,
        SignalementEntity::class,
        NotificationEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun userDao(): UserDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun mediaDao(): MediaDao
    abstract fun signalementDao(): SignalementDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_db"
                )
                    // Dev : nouveau schéma sans migration (v1 -> v2 offline-first)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }

        fun inMemory(context: Context): AppDatabase =
            Room.inMemoryDatabaseBuilder(context.applicationContext, AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
    }
}