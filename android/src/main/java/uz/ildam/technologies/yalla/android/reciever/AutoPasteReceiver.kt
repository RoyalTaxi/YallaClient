package uz.ildam.technologies.yalla.android.reciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class AutoPasteReceiver(private val onRetrieved: (String) -> Unit) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val userData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(SmsRetriever.EXTRA_SMS_MESSAGE, Status::class.java)
            } else {
                intent.getParcelableExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
            }
            when (userData?.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val message = extras?.getString(SmsRetriever.EXTRA_SMS_MESSAGE)
                    message?.let { onRetrieved(extractCode(message)) }
                }
            }
        }
    }

    private fun extractCode(message: String): String {
        val regex = Regex("(\\d{5})")
        val match = regex.find(message)
        return match?.value ?: ""
    }
}