package uz.yalla.client.update

import android.app.Activity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.appcompat.app.AlertDialog
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import uz.yalla.client.R

class AppUpdateHandler(private val activity: Activity) {

    private val appUpdateManager: AppUpdateManager by lazy { 
        AppUpdateManagerFactory.create(activity) 
    }

    fun checkForImmediateUpdate(updateFlowLauncher: ActivityResultLauncher<IntentSenderRequest>) {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            val updateAvailable =
                appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                        appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)

            if (updateAvailable) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    updateFlowLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE)
                        .setAllowAssetPackDeletion(true)
                        .build()
                )
            }
        }

        appUpdateInfoTask.addOnFailureListener { exception ->
            showErrorDialog(exception.message ?: activity.getString(R.string.unknown_error))
        }
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(activity)
            .setTitle(activity.getString(R.string.update_check_failed_title))
            .setMessage(activity.getString(R.string.update_error_message, message))
            .setCancelable(false)
            .setNegativeButton(activity.getString(R.string.close)) { _, _ -> 
                activity.finish() 
            }
            .show()
    }
}