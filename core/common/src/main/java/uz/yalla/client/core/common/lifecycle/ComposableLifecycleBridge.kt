package uz.yalla.client.core.common.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import uz.yalla.client.core.common.viewmodel.LifeCycleAware

@Composable
fun LifecycleOwner.MakeBridge(viewModel: LifeCycleAware) {
    DisposableEffect(this) {
        val observer = object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                viewModel.onCreate()
            }

            override fun onStart(owner: LifecycleOwner) {
                super.onCreate(owner)
                viewModel.onStart()
            }

            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                viewModel.onResume()
            }

            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                viewModel.onPause()
            }

            override fun onStop(owner: LifecycleOwner) {
                super.onDestroy(owner)
                viewModel.onStop()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                viewModel.onDestroy()
            }
        }

        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}