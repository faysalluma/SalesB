package com.groupec.salesb.core.ui

import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import android.Manifest
import android.content.Context
import android.provider.OpenableColumns
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import com.groupec.salesb.core.Utility
import com.groupec.salesb.core.designsystem.component.AddImageModalBottomSheet
import com.groupec.salesb.core.designsystem.component.CardImage
import java.io.FileOutputStream
import java.io.IOException

@Composable
fun AddImage(
    uri: Uri? = null, //target url to preview
    directory: File? = null, // stored directory
    onSetUri : (Uri?) -> Unit = {}, // selected / taken uri
    upload: (Uri) -> Unit = {},
    deleteFile: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val tempUri = remember { mutableStateOf<Uri?>(null) }
    val authority = stringResource(id = R.string.fileprovider)

    // for takePhotoLauncher used
    fun getTempUri(): Uri? {
        directory?.let {
            it.mkdirs()
            val file = File.createTempFile(
                Utility.generateUniqueStringValue(),
                ".jpg",
                it
            )

            return FileProvider.getUriForFile(
                context,
                authority,
                file
            )
        }
        return null
    }

    // for image picked
    fun renameAndCopyFile(
        context: Context,
        originalUri: Uri,
        onSetUri: (Uri?) -> Unit
    ) {
        val cursor = context.contentResolver.query(originalUri, null, null, null, null)
        val fileName = cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            it.getString(nameIndex)
        } ?: "image_${System.currentTimeMillis()}.jpg" // Fallback if no name is found

        val extension = fileName.substringAfterLast(".", "")
        val newFileName = "${Utility.generateUniqueStringValue()}.${extension}" // Your logic for the new name

        // Create a new file with the new name in the specified directory (here cache dir)
        val newFile = File(context.cacheDir, newFileName)
        val newUri = Uri.fromFile(newFile)

        // Optionally copy the content from the original URI to the new file
        try {
            context.contentResolver.openInputStream(originalUri)?.use { inputStream ->
                FileOutputStream(newFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace() // Handle error
        }

        // Invoke the onSetUri with the new file URI
        onSetUri.invoke(newUri)
        tempUri.value = newUri
    }


    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { it ->
            it?.let {
                renameAndCopyFile(context, it, onSetUri)
                // onSetUri.invoke(it)
            }
        }
    )

    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { isSaved ->
            tempUri.value?.let {
                // if photo taked
                if (isSaved) {
                    onSetUri.invoke(it)
                } else {
                    deleteFile.invoke(it.lastPathSegment?:"")
                }
            }
        }
    )

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted, launch takePhotoLauncher
            tempUri.value = getTempUri()
            tempUri.value?.let { takePhotoLauncher.launch(it) }
        } else {
            // Permission is denied, handle it accordingly
        }
    }

    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet){
        AddImageModalBottomSheet(
            onDismiss = {
                showBottomSheet = false
            },
            onTakePhotoClick = {
                showBottomSheet = false

                val permission = Manifest.permission.CAMERA
                if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
                ) {
                    // Permission is already granted, proceed to step 2
                    tempUri.value = getTempUri()
                    tempUri.value?.let { takePhotoLauncher.launch(it) }
                } else {
                    // Permission is not granted, request it
                    cameraPermissionLauncher.launch(permission)
                }
            },
            onPhotoGalleryClick = {
                showBottomSheet = false
                imagePicker.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            },
        )
    }

    Column (
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        CardImage(
            imageUri = uri,
            onClick = {
                showBottomSheet = true
            }
        )
        Spacer(modifier = Modifier.height(4.dp))
        TextButton(
            onClick = {
                // Delete cache file if image is exists
                tempUri.value?.let { temp ->
                    deleteFile.invoke(temp.lastPathSegment?:"")
                }

                // Set uri
                onSetUri.invoke(null)
            }
        ) {
            Text(text = stringResource(R.string.delete_item))
        }

        /*Button(
            onClick = {
                upload.invoke(uri!!)
            }
        ) {
            Text(text = "upload to server")
        }*/

    }
}