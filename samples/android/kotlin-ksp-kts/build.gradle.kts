plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}

tasks.register<Delete>("clean") {
    group = "build"
    delete(rootProject.layout.buildDirectory)
}
