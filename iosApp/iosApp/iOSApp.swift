import SwiftUI
import shared

@main
struct iOSApp: App {
	var body: some Scene {
        shared.VerifyAuthCodeUseCase(repository: <#T##any AuthRepository#>)
		WindowGroup {
            OnboardingScreen()
		}
	}
}
