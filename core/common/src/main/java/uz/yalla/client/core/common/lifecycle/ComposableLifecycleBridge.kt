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
                viewModel.onAppear()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                viewModel.onDisappear()
            }
        }

        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}