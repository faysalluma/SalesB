package com.groupec.salesb.core

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream

class UploadUtility {

    companion object {

        fun getRealPathFromURI(context: Context, uri: Uri): String? {
            return try {
                if (uri.scheme == "content") { // Photo taken
                    val cursor = context.contentResolver.query(uri, null, null, null, null)
                    cursor?.use {
                        if (cursor.moveToFirst()) {
                            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                            val fileName = cursor.getString(nameIndex)
                            val file = File(context.cacheDir, fileName)
                            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                                FileOutputStream(file).use { outputStream ->
                                    inputStream.copyTo(outputStream)
                                }
                            }
                            return file.absolutePath
                        }
                    }
                }
                uri.path // Si l'Uri est un chemin de fichier, le retourner directement
            } catch (e: Exception) {
                println("Error when upload deleted file")
                e.printStackTrace() // Affiche l'erreur dans les logs pour le débogage
                null // Retourne null en cas d'erreur
            }
        }

        fun deleteImageFromCache(context: Context, fileName: String) {
            val imagesDir = File(context.cacheDir, "images")
            val fileInImagesDir = File(imagesDir, fileName)
            val fileInCacheRoot = File(context.cacheDir, fileName)

            val fileToDelete = when {
                fileInImagesDir.exists() -> fileInImagesDir
                fileInCacheRoot.exists() -> fileInCacheRoot
                else -> null
            }
            fileToDelete?.delete()
        }

        fun deleteDirectoryFromCache(context: Context) {
            val directory = File(context.cacheDir, "images")
            directory.deleteRecursively()
        }
    }
}