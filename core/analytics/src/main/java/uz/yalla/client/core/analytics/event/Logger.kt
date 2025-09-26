package uz.yalla.client.core.analytics.event

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent

object Logger {
    private lateinit var analytics: FirebaseAnalytics

    fun init(ctx: Context) {
        analytics = FirebaseAnalytics.getInstance(ctx)
    }

    fun log(event: Event) {
        when (event) {
            is Event.ActivateBonusClick -> {
                analytics.logEvent(event.name) {
                    param(Event.SOURCE, event.source.name)
                }
            }

            is Event.ServiceOptionClick -> {
                analytics.logEvent(event.name) {
                    param(Event.OPTION, event.option)
                }
            }

            is Event.TariffOptionClick -> {
                analytics.logEvent(event.name) {
                    param(Event.OPTION, event.tariff)
                }
            }

            else -> {
                analytics.logEvent(event.name.asCustom(), null)
            }
        }
    }

    fun String.asCustom() = "custom_$this"
}