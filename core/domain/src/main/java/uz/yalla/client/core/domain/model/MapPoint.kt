package uz.yalla.client.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MapPoint(
    val lat: Double,
    val lng: Double
) : Parcelable {
    companion object {
        val Zero = MapPoint(0.0, 0.0)
    }
}