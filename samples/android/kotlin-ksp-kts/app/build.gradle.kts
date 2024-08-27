plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.devtools.ksp)
}

//JniGen settings
class JniGenProps{
    val generatedPath: String = file("src/main/cpp/generated").absolutePath
    val outputListName = "outputs.txt"
    val additionalClasses = arrayOf("java.lang.Byte")
}
val jniGenProps = JniGenProps()

dependencies {

    //JNI annotations
    compileOnly(libs.smjni.jnigen.annotations)
    //JNI code generator
    ksp(libs.smjni.jnigen.kprocessor)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.android.material)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
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

tasks.withType<com.google.devtools.ksp.gradle.KspTaskJvm> {
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
    val match = Regex("""^buildCMake([^\[]*).*$""").matchEntire(name)
    if (match != null) {
        val config = when(match.groupValues[1]) {
            "RelWithDebInfo" -> "Release"
            else -> match.groupValues[1]
        }
        dependsOn("ksp${config}Kotlin")
    }
}



