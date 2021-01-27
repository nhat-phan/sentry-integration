package net.ntworld.sentryIntegration

import net.ntworld.sentryIntegration.entity.LinkedProject
import net.ntworld.sentryIntegration.storage.MemoryStorageImpl
import java.util.*

object StorageManager {
    private val myStorages = Collections.synchronizedMap(mutableMapOf<String, Storage>())

    @Synchronized
    fun make(linkedProject: LinkedProject): Storage {
        val storage = myStorages[linkedProject.id]
        if (null === storage) {
            val created = MemoryStorageImpl(linkedProject)
            myStorages[linkedProject.id] = created
            return created
        }
        return storage
    }

}