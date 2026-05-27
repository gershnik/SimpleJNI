plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.devtools.ksp)
}

//JniGen settings
private val jniGenProps = object {
    val generatedPath: String = file("src/main/cpp/generated").absolutePath
    val outputListName = "outputs.txt"
    val additionalClasses = listOf("java.lang.Byte", "java.lang.Double")
}

dependencies {

    //JNI annotations
    compileOnly(libs.smjni.jnigen.annotations)
    //JNI code generator
    ksp(libs.smjni.jnigen.kprocessor)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    namespace = "com.example.myapplication"

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        externalNativeBuild {
            cmake {
                arguments("-DJNIGEN_GENERATED_DIR=${file(jniGenProps.generatedPath).parentFile.absolutePath}")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                          "proguard-rules.pro",
                          "../../../common-proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlin {
        jvmToolchain(8)
    }
    buildFeatures {
        compose = true
    }
    externalNativeBuild {
        cmake {
            path = file("../../../cpp/CMakeLists.txt")
            version = libs.versions.cmake.get()
        }
    }
}

ksp {
    arg("smjni.jnigen.dest.path", jniGenProps.generatedPath)
    arg("smjni.jnigen.own.dest.path", "true")
    arg("smjni.jnigen.output.list.name", jniGenProps.outputListName)
    arg("smjni.jnigen.expose.extra", jniGenProps.additionalClasses.joinToString(";"))
}

//Use com.google.devtools.ksp.gradle.KspTaskJvm for older KSP
tasks.withType<com.google.devtools.ksp.gradle.KspAATask> {
    outputs.upToDateWhen utd@{

        val jniGenOutputList = file("${jniGenProps.generatedPath}/${jniGenProps.outputListName}")

        if (!jniGenOutputList.exists()) {
            return@utd false
        }

        for(line in jniGenOutputList.readLines()) {
            if (!file("${jniGenProps.generatedPath}/$line").exists()) {
                return@utd false
            }
        }

        return@utd true
    }
}



//Clean generated headers on project clean
tasks.register<Delete>("cleanJNIHeaders") {
    group = "build"
    delete(file(jniGenProps.generatedPath))
}
tasks.named("clean") {
    dependsOn("cleanJNIHeaders")
}

//Make KSP (and so JniGen code generation) run before CMake build
tasks.whenTaskAdded {
    val match = Regex("""^(?:build|configure)CMake([^\[]*).*$""").matchEntire(name)
    if (match != null) {
        val config = when(match.groupValues[1]) {
            "RelWithDebInfo" -> "Release"
            else -> match.groupValues[1]
        }
        dependsOn("ksp${config}Kotlin")
    }
}



