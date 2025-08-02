package uz.yalla.client.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import uz.yalla.client.core.domain.local.AppPreferences
import uz.yalla.client.core.domain.local.StaticPreferences
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.auth.AUTH_ROUTE
import uz.yalla.client.feature.auth.authModule
import uz.yalla.client.feature.auth.navigateToAuthModule
import uz.yalla.client.feature.intro.INTRO_ROUTE
import uz.yalla.client.feature.intro.introModule
import uz.yalla.client.feature.registration.presentation.navigation.navigateToRegistrationScreen
import uz.yalla.client.feature.registration.presentation.navigation.registrationScreen

class LoginActivity : AppCompatActivity() {
    private val appPreferences: AppPreferences by inject()
    private val staticPreferences: StaticPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val accessToken = appPreferences.accessToken.firstOrNull() ?: ""
            if (staticPreferences.isDeviceRegistered && accessToken.isNotEmpty()) {
                navigateToMainActivity()
                return@launch
            }
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.Transparent.toArgb(),
                Color.Black.toArgb()
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.Transparent.toArgb(),
                Color.Black.toArgb()
            )
        )

        setContent {
            YallaTheme {
                val navController = rememberNavController()

                NavHost(
                    modifier = Modifier.fillMaxSize(),
                    navController = navController,
                    startDestination = when {
                        staticPreferences.skipOnboarding -> AUTH_ROUTE
                        else -> INTRO_ROUTE
                    },
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Start,
                            tween(700)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.End,
                            tween(700)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Start,
                            tween(700)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.End,
                            tween(700)
                        )
                    }
                ) {
                    introModule(
                        navController = navController,
                        onPermissionGranted = navController::navigateToAuthModule
                    )
                    authModule(
                        navController = navController,
                        onClientFound = { navigateToMainActivity() },
                        onClientNotFound = navController::navigateToRegistrationScreen,
                    )
                    registrationScreen(
                        onBack = navController::popBackStack,
                        onNext = { navigateToMainActivity() }
                    )
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
