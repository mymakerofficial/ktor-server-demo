package de.maiker.storage

import java.io.File

interface StorageSpec {
    fun readBytes(path : String) : ByteArray
    fun writeBytes(path : String, bytes : ByteArray)
    fun deleteFile(path : String)
}

class JStorage : StorageSpec {
    override fun readBytes(path: String): ByteArray {
        return File(path).readBytes()
    }

    override fun writeBytes(path: String, bytes: ByteArray) {
        File(path).writeBytes(bytes)
    }

    override fun deleteFile(path: String) {
        File(path).delete()
    }
}

class StorageFactory {
    fun createStorage() : StorageSpec {
        return JStorage()
    }
}