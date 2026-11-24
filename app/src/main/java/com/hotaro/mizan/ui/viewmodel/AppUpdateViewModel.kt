package com.hotaro.mizan.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotaro.mizan.BuildConfig
import com.hotaro.mizan.data.model.ReleaseNote
import com.hotaro.mizan.data.repository.QuranRepository
import com.hotaro.mizan.util.AppUpdateManager
import com.hotaro.mizan.util.UpdateDownloadState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AppUpdateViewModel @Inject constructor(
    private val repository: QuranRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _availableUpdate = MutableStateFlow<ReleaseNote?>(null)
    val availableUpdate = _availableUpdate.asStateFlow()

    private val _showUpdateDrawer = MutableStateFlow(false)
    val showUpdateDrawer = _showUpdateDrawer.asStateFlow()

    private val _downloadState = MutableStateFlow<UpdateDownloadState>(UpdateDownloadState.Idle)
    val downloadState = _downloadState.asStateFlow()

    private var hasCheckedOnLaunch = false

    fun checkUpdateOnLaunch() {
        if (hasCheckedOnLaunch) return
        hasCheckedOnLaunch = true

        viewModelScope.launch {
            try {
                val releases = repository.fetchGithubReleases()
                val latest = releases.firstOrNull { it.downloadUrl != null } ?: return@launch
                val currentVersion = BuildConfig.VERSION_NAME

                val isNewer = AppUpdateManager.isNewerVersion(currentVersion, latest.version)
                val dismissedTag = repository.dismissedUpdateTag.first()

                Log.d("AppUpdateViewModel", "Current: $currentVersion, Latest: ${latest.version}, isNewer: $isNewer, dismissed: $dismissedTag")

                if (isNewer && dismissedTag != latest.version) {
                    _availableUpdate.value = latest
                    _showUpdateDrawer.value = true
                }
            } catch (e: Exception) {
                Log.e("AppUpdateViewModel", "Failed to check update on launch: ${e.message}")
            }
        }
    }

    fun openUpdateDrawerManually(release: ReleaseNote? = null) {
        viewModelScope.launch {
            if (release != null) {
                _availableUpdate.value = release
                _showUpdateDrawer.value = true
            } else {
                val releases = repository.fetchGithubReleases()
                val target = releases.firstOrNull { it.downloadUrl != null } ?: releases.firstOrNull()
                if (target != null) {
                    _availableUpdate.value = target
                    _showUpdateDrawer.value = true
                }
            }
        }
    }

    fun dismissDrawer(rememberDismissed: Boolean = true) {
        _showUpdateDrawer.value = false
        if (rememberDismissed) {
            _availableUpdate.value?.version?.let { tag ->
                viewModelScope.launch {
                    repository.saveDismissedUpdateTag(tag)
                }
            }
        }
    }

    fun startDownload() {
        val release = _availableUpdate.value ?: return
        val url = release.downloadUrl ?: return
        viewModelScope.launch {
            AppUpdateManager.downloadApk(context, url) { state ->
                _downloadState.value = state
                if (state is UpdateDownloadState.Downloaded) {
                    AppUpdateManager.installApk(context, state.file)
                }
            }
        }
    }

    fun installDownloadedApk(file: File) {
        AppUpdateManager.installApk(context, file)
    }
}
