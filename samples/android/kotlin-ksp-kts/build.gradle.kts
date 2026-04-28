buildscript {
    dependencies {
        classpath(libs.builtin.kotlin.android)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
}

tasks.register<Delete>("clean") {
    group = "build"
    delete(rootProject.layout.buildDirectory)
}
