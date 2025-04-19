plugins {
    id(libs.plugins.buildlogic.get().pluginId)
}

android {
    namespace = "uz.yalla.client.feature.order.data"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.service.order)
    implementation(projects.feature.order.domain)

    testImplementation(projects.core.test)
}