package de.maiker.storage

import java.io.File

interface StorageSpec {
    fun readBytes(path : String) : ByteArray
    fun readBytes(path : java.nio.file.Path) : ByteArray {
        return readBytes(path.toString())
    }
    fun writeBytes(path : String, bytes : ByteArray)
    fun writeBytes(path : java.nio.file.Path, bytes : ByteArray) {
        writeBytes(path.toString(), bytes)
    }
    fun deleteFile(path : String)
    fun deleteFile(path : java.nio.file.Path) {
        deleteFile(path.toString())
    }
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