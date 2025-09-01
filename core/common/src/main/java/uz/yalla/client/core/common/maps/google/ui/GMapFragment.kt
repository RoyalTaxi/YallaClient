package uz.yalla.client.core.common.maps.google.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.coroutines.launch
import org.koin.androidx.scope.ScopeActivity
import uz.yalla.client.core.common.R
import uz.yalla.client.core.common.maps.core.model.MapsIntent
import uz.yalla.client.core.common.maps.core.viewmodel.MapsViewModel
import uz.yalla.client.core.common.utils.dpToPx
import uz.yalla.client.core.common.utils.getCurrentLocation
import uz.yalla.client.core.domain.model.MapPoint

class GMapFragment : Fragment() {

    companion object Companion {
        const val GOOGLE_MARK_PADDING = 8
        const val MAP_PADDING = 100
    }

    private val viewModel: MapsViewModel by lazy {
        (requireActivity() as ScopeActivity).scope.get<MapsViewModel>()
    }

    private val callback = OnMapReadyCallback { googleMap ->
        viewModel.onIntent(MapsIntent.OnMapReady(googleMap))

        viewModel.onIntent(
            MapsIntent.SetSideMarkPadding(
                dpToPx(
                    requireContext(),
                    GOOGLE_MARK_PADDING
                )
            )
        )
        viewModel.onIntent(MapsIntent.SetMapPadding(dpToPx(requireContext(), MAP_PADDING)))

        handleInitialCameraPosition()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onDestroyView() {
        // Ensure child SupportMapFragment view hierarchy is fully released
        runCatching {
            (childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment)?.let { smf ->
                childFragmentManager.beginTransaction()
                    .remove(smf)
                    .commitNowAllowingStateLoss()
            }
        }
        super.onDestroyView()
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
                        viewModel.onIntent(
                            MapsIntent.MoveTo(
                                MapPoint(
                                    location.latitude,
                                    location.longitude
                                )
                            )
                        )
                    }
                )
            }
        }
    }
}
