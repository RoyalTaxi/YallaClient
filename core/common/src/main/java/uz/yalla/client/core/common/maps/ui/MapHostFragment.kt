package uz.yalla.client.core.common.maps.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import uz.yalla.client.core.common.R
import uz.yalla.client.core.common.maps.google.ui.GMapFragment
import uz.yalla.client.core.common.maps.libre.ui.LMapFragment
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.model.MapType

/**
 * Host fragment that switches between Google Maps and Libre Maps based on user preferences.
 */
class MapHostFragment : Fragment() {

    private val appPreferences: AppPreferences by inject()

    // Keep track of the current map type to avoid unnecessary fragment transactions
    private var currentMapType: MapType? = null

    // Keep references to the fragments to avoid recreating them unnecessarily
    private var googleMapFragment: GMapFragment? = null
    private var lMapFragment: LMapFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map_host, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe the map type preference and switch fragments accordingly
        viewLifecycleOwner.lifecycleScope.launch {
            appPreferences.mapType.collectLatest { mapType ->
                if (mapType != currentMapType) {
                    switchMapFragment(mapType)
                    currentMapType = mapType
                }
            }
        }
    }

    /**
     * Switch between map fragments based on the selected map type.
     */
    private fun switchMapFragment(mapType: MapType) {
        val fm = childFragmentManager
        val transaction = fm.beginTransaction().setReorderingAllowed(true)

        val newFragment = when (mapType) {
            MapType.Google -> {
                // Clear reference to the other provider to avoid retaining removed fragments
                lMapFragment = null
                (googleMapFragment ?: GMapFragment().also { googleMapFragment = it })
            }
            MapType.Libre -> {
                googleMapFragment = null
                (lMapFragment ?: LMapFragment().also { lMapFragment = it })
            }
        }

        transaction.replace(R.id.map_fragment_container, newFragment)
        // Apply immediately to keep lifecycle states in sync and release old fragment views
        transaction.commitNowAllowingStateLoss()
    }

    override fun onDestroyView() {
        // Proactively remove current child to drop its view hierarchy and listeners
        runCatching {
            val current = childFragmentManager.findFragmentById(R.id.map_fragment_container)
            if (current != null) {
                childFragmentManager.beginTransaction()
                    .remove(current)
                    .commitNowAllowingStateLoss()
            }
        }
        super.onDestroyView()
        googleMapFragment = null
        lMapFragment = null
    }
}
