package uz.ildam.technologies.yalla.android.app

import android.app.Application
import com.google.android.gms.maps.MapsInitializer
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import sp.bvantur.inspektify.ktor.InspektifyKtor
import uz.ildam.technologies.yalla.android.di.Navigation
import uz.ildam.technologies.yalla.core.data.di.Common
import uz.ildam.technologies.yalla.core.data.local.AppPreferences

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        AppPreferences.init(this)
//        AppPreferences.accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiI1IiwianRpIjoiNDJiM2Q1ZmJjNzkyNWY5ZGUzMTI3MTdlMGVjMDJkZjFmNTE1OWUwZDc1MDhjMjA4ZmI1MzIzNTMxYTA3ZjM5NGMwNTQzMmQ1NWY4NjBiZWIiLCJpYXQiOjE3MzE2NjEwMDkuNzgxODY4LCJuYmYiOjE3MzE2NjEwMDkuNzgxODY5LCJleHAiOjE3NjMxOTcwMDkuNzgwMjQsInN1YiI6IjciLCJzY29wZXMiOlsiY2xpZW50Il19.Je6-eg_nP9xW1grsxJdjW0Mak8qvhSMR3HH83TGRDUcevT7NFB04S6Ziu96S_yl-9OBEhyjeOTS2c6EKf31f1xuL_mGtnybQx4mvv-JhA_dIFHaWPunh8c9bU-fvO0A3chnkLekneVrip49KVW8syt4SMiWcxzffjwSA-spkOp3y541NHBNqxyTJ6UAXVKy1wKkoeeje1b5N84BY9J21bGmBsQQmKJ9gNYgLY-0bFTf8eisxQ6U1l8m7XqLMENo2SbPl_63yG8TONiKvhbqPy7iymCzOMnzeo2Fw1BcGN9ikYIzfjTfm1rhnHwtrmLFZdT5tvkZxNyOfE1CYklg-nzVXdcgrx10FXW7kAOeWOiOQUsYfzNANlc-ccaiHxC2jAzDFS7KW8J8p164JN-yyhV46VoD2SZ8EqvDDAqgUcZ1RWZ_Hv3HbqTXDL8rfugOD-T0DXwJsZ9ywCoESCPXLnn_ptMPbVgEuVzuT5zUHlMoVW8GYOb04HTy6dmC4UNSW--ZlU7d2oAOcOiuNJUCs1UTx5vJrWB-jIU3jjjQU_GxJdIzBlK2rObG6mplS4IjrduYk3w47NgfUhytcYuRVRvX9JVab1vSvvQSqBukmt6UkHvzmMg2PFSvR4lPMHYreXwfgvvAg3LkYb_43lnTjX8Q1RtycV9N2gOAZzIL6C5k"
        MapsInitializer.initialize(this)
        startKoin {
            androidContext(this@App)
            printLogger(level = org.koin.core.logger.Level.DEBUG)
            modules(
                *Common.modules.toTypedArray(),
                *Navigation.modules.toTypedArray(),
            )
        }
    }
}