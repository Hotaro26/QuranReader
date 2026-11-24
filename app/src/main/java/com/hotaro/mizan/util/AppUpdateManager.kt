package com.hotaro.mizan.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

sealed interface UpdateDownloadState {
    data object Idle : UpdateDownloadState
    data class Downloading(
        val progress: Float,
        val downloadedBytes: Long,
        val totalBytes: Long
    ) : UpdateDownloadState
    data class Downloaded(val file: File) : UpdateDownloadState
    data class Failed(val error: String) : UpdateDownloadState
}

object AppUpdateManager {

    private const val TAG = "AppUpdateManager"

    private fun extractVersionNumbers(raw: String): List<Int> {
        val clean = raw.trim().removePrefix("v").removePrefix("V")
        val match = Regex("""\d+(\.\d+)*""").find(clean)?.value ?: clean
        return match.split(".").mapNotNull { it.toIntOrNull() }
    }

    fun isNewerVersion(currentVersion: String, remoteTag: String): Boolean {
        try {
            val currentParts = extractVersionNumbers(currentVersion)
            val remoteParts = extractVersionNumbers(remoteTag)

            if (currentParts.isEmpty() || remoteParts.isEmpty()) return false

            val maxLen = maxOf(currentParts.size, remoteParts.size)
            for (i in 0 until maxLen) {
                val curr = currentParts.getOrElse(i) { 0 }
                val rem = remoteParts.getOrElse(i) { 0 }
                if (rem > curr) return true
                if (rem < curr) return false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error comparing versions $currentVersion vs $remoteTag: ${e.message}")
        }
        return false
    }

    fun isSameVersion(v1: String, v2: String): Boolean {
        val p1 = extractVersionNumbers(v1)
        val p2 = extractVersionNumbers(v2)
        if (p1.isEmpty() || p2.isEmpty()) return false
        val maxLen = maxOf(p1.size, p2.size)
        for (i in 0 until maxLen) {
            val n1 = p1.getOrElse(i) { 0 }
            val n2 = p2.getOrElse(i) { 0 }
            if (n1 != n2) return false
        }
        return true
    }

    suspend fun downloadApk(
        context: Context,
        downloadUrl: String,
        onProgress: (UpdateDownloadState) -> Unit
    ): File? = withContext(Dispatchers.IO) {
        try {
            onProgress(UpdateDownloadState.Downloading(0f, 0L, 0L))

            var currentUrl = downloadUrl
            var connection: HttpURLConnection
            var redirectCount = 0

            // Follow HTTP redirects safely (AWS S3 redirect handling for GitHub Releases)
            while (true) {
                connection = (URL(currentUrl).openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 15000
                    readTimeout = 30000
                    instanceFollowRedirects = false
                    setRequestProperty("User-Agent", "Mizan-App")
                }

                val status = connection.responseCode
                if ((status == HttpURLConnection.HTTP_MOVED_TEMP ||
                            status == HttpURLConnection.HTTP_MOVED_PERM ||
                            status == HttpURLConnection.HTTP_SEE_OTHER ||
                            status == 307 || status == 308) && redirectCount < 6
                ) {
                    val location = connection.getHeaderField("Location")
                    connection.disconnect()
                    currentUrl = location
                    redirectCount++
                } else {
                    break
                }
            }

            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                val err = "Download failed with HTTP $responseCode"
                Log.e(TAG, err)
                onProgress(UpdateDownloadState.Failed(err))
                connection.disconnect()
                return@withContext null
            }

            val totalLength = connection.contentLengthLong
            val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.cacheDir
            if (!downloadsDir.exists()) downloadsDir.mkdirs()

            val apkFile = File(downloadsDir, "Mizan-Latest.apk")
            if (apkFile.exists()) {
                apkFile.delete()
            }

            var downloaded = 0L
            var lastUpdateMillis = 0L

            connection.inputStream.use { input ->
                FileOutputStream(apkFile).use { output ->
                    val buffer = ByteArray(16 * 1024)
                    var read: Int
                    while (input.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                        downloaded += read

                        val now = System.currentTimeMillis()
                        if (now - lastUpdateMillis > 80 || (totalLength > 0 && downloaded >= totalLength)) {
                            lastUpdateMillis = now
                            val progress = if (totalLength > 0) downloaded.toFloat() / totalLength else 0f
                            onProgress(UpdateDownloadState.Downloading(progress, downloaded, totalLength))
                        }
                    }
                    output.flush()
                }
            }
            connection.disconnect()

            onProgress(UpdateDownloadState.Downloaded(apkFile))
            return@withContext apkFile
        } catch (e: Exception) {
            val errorMsg = e.localizedMessage ?: "Download failed"
            Log.e(TAG, "Exception during APK download: $errorMsg", e)
            onProgress(UpdateDownloadState.Failed(errorMsg))
            return@withContext null
        }
    }

    fun installApk(context: Context, apkFile: File) {
        if (!apkFile.exists()) {
            Log.e(TAG, "Install failed: APK file does not exist at ${apkFile.absolutePath}")
            return
        }

        try {
            // Check unknown sources installation permission on Android 8.0+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!context.packageManager.canRequestPackageInstalls()) {
                    val manageIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                        data = Uri.parse("package:${context.packageName}")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(manageIntent)
                    return
                }
            }

            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(contentUri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            context.startActivity(installIntent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start APK installation intent: ${e.message}", e)
        }
    }
}
