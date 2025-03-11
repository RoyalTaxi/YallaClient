package uz.yalla.client.core.common.utils

import android.content.Context
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object SignatureHelper {
    private const val TAG = "AppSignatureHelper"

    fun get(context: Context): String? {
        try {
            val packageName = context.packageName
            val packageManager = context.packageManager
            val signatures = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            ).signatures

            signatures?.iterator()?.forEach { signature ->
                val hash = getHashFromSignature(signature.toByteArray())
                if (hash != null) {
                    return hash
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "Package not found", e)
        }
        return null
    }

    private fun getHashFromSignature(signature: ByteArray): String? {
        try {
            val messageDigest = MessageDigest.getInstance("SHA-256")
            messageDigest.update(signature)
            val hashSignature = messageDigest.digest()

            val base64Hash = Base64.encodeToString(hashSignature, Base64.NO_WRAP)
            return base64Hash.substring(0, 11)
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "Hashing algorithm not found", e)
        } catch (e: Exception) {
            Log.e(TAG, "Error generating hash", e)
        }
        return null
    }
}