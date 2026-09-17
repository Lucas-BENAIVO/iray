package mg.iray.app.di

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import mg.iray.app.auth.AuthSource
import mg.iray.app.auth.FirebaseAuthSource
import mg.iray.app.auth.SharedPrefsUserSession
import mg.iray.app.auth.UserSessionStore
import mg.iray.app.db.AppDatabase
import mg.iray.app.repository.AnnouncementRepository
import mg.iray.app.repository.AuthRepository
import mg.iray.app.repository.FirebaseStorageMediaUploader
import mg.iray.app.repository.MediaRepository
import mg.iray.app.repository.MediaUploader
import mg.iray.app.repository.NotificationRepository
import mg.iray.app.repository.ProcedureRepository
import mg.iray.app.repository.RequestRepository
import mg.iray.app.repository.SignalementRepository
import mg.iray.app.repository.SyncableRepository
import mg.iray.app.repository.TaskRepository
import mg.iray.app.repository.TerritoryRepository
import mg.iray.app.repository.UserProfileRepository
import mg.iray.app.sync.FirestoreRemoteSync
import mg.iray.app.sync.RemoteSync

object ServiceLocator {

    @Volatile private lateinit var appContext: Context
    @Volatile lateinit var db: AppDatabase
    @Volatile lateinit var session: UserSessionStore
    @Volatile lateinit var authSource: AuthSource
    @Volatile lateinit var remoteSync: RemoteSync
    @Volatile lateinit var mediaUploader: MediaUploader

    fun init(context: Context) {
        appContext = context.applicationContext
        db = AppDatabase.getInstance(context)
        session = SharedPrefsUserSession(context)
        authSource = FirebaseAuthSource()
        remoteSync = FirestoreRemoteSync(FirebaseFirestore.getInstance())
        mediaUploader = FirebaseStorageMediaUploader(FirebaseStorage.getInstance()) { session.getUid().orEmpty() }
    }

    fun uidProvider(): () -> String = { session.getUid().orEmpty() }

    fun authRepository() = AuthRepository(
        authSource, session,
        db.userDao(), db.userProfileDao(), db.taskDao(),
        db.signalementDao(), db.mediaDao(), db.notificationDao(),
        db.requestDao()
    )

    fun taskRepository() = TaskRepository(db.taskDao(), remoteSync, uidProvider())
    fun userProfileRepository() = UserProfileRepository(db.userProfileDao(), remoteSync, uidProvider())
    fun signalementRepository() = SignalementRepository(db.signalementDao(), remoteSync, session)
    fun notificationRepository() = NotificationRepository(db.notificationDao(), remoteSync, uidProvider())
    fun mediaRepository() = MediaRepository(db.mediaDao(), mediaUploader, uidProvider())
    fun territoryRepository() = TerritoryRepository(db.territoryDao(), remoteSync, appContext)
    fun procedureRepository() = ProcedureRepository(db.procedureDao(), remoteSync)
    fun requestRepository() = RequestRepository(db.requestDao(), remoteSync, session)
    fun announcementRepository() = AnnouncementRepository(db.announcementDao(), remoteSync)

    fun syncableRepositories(): List<SyncableRepository> = listOf(
        taskRepository(),
        userProfileRepository(),
        signalementRepository(),
        notificationRepository(),
        territoryRepository(),
        procedureRepository(),
        requestRepository(),
        announcementRepository()
    )
}
