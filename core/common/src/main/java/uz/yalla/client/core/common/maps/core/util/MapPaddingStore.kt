package uz.yalla.client.core.common.maps.core.util

/**
 * Holds the latest map padding requested by the app so that
 * provider-specific components (e.g., MapLibre style reload) can reapply it.
 */
object MapPaddingStore {
    @Volatile var left: Int = 0
        private set
    @Volatile var top: Int = 0
        private set
    @Volatile var right: Int = 0
        private set
    @Volatile var bottom: Int = 0
        private set

    fun set(left: Int, top: Int, right: Int, bottom: Int) {
        this.left = left
        this.top = top
        this.right = right
        this.bottom = bottom
    }
}

