package mg.iray.app.repository

interface SyncableRepository {
    suspend fun pushPendingChanges()
    suspend fun pullRemoteChanges()
}