package uz.yalla.client.core.common.maps.libre.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.koin.androidx.scope.ScopeActivity
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import uz.yalla.client.core.common.R
import uz.yalla.client.core.common.maps.core.model.MapsIntent
import uz.yalla.client.core.common.maps.core.viewmodel.MapsViewModel
import uz.yalla.client.core.common.utils.dpToPx
import uz.yalla.client.core.common.utils.getCurrentLocation
import uz.yalla.client.core.domain.model.MapPoint

/**
 * A fragment that displays an OpenStreetMap using OSMDroid library.
 */
class LMapFragment : Fragment() {

    companion object {
        const val SIDE_MARK_PADDING = 8
        const val MAP_PADDING = 100
        const val DEFAULT_ZOOM = 15.0
    }

    private val viewModel: MapsViewModel by lazy {
        (requireActivity() as ScopeActivity).scope.get<MapsViewModel>()
    }

    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_libre_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the MapLibre MapView
        mapView = view.findViewById(R.id.maplibre_map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { map: MapLibreMap ->
            // Notify the ViewModel that the map is ready
            viewModel.onIntent(MapsIntent.OnMapReady(map))

            // Set padding values
            viewModel.onIntent(
                MapsIntent.SetSideMarkPadding(
                    dpToPx(
                        requireContext(),
                        SIDE_MARK_PADDING
                    )
                )
            )
            viewModel.onIntent(MapsIntent.SetMapPadding(dpToPx(requireContext(), MAP_PADDING)))

            // Handle initial camera position
            handleInitialCameraPosition()
        }
    }

    private fun handleInitialCameraPosition() {
        lifecycleScope.launch {
            if (viewModel.hasSavedCameraPosition()) {
                viewModel.getSavedCameraPosition()?.let { savedPosition ->
                    viewModel.onIntent(MapsIntent.MoveTo(savedPosition))
                }
            } else {
                getCurrentLocation(
                    context = requireContext(),
                    onLocationFetched = { location ->
                        val point = MapPoint(location.latitude, location.longitude)
                        viewModel.onIntent(MapsIntent.MoveTo(point))
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }
}
