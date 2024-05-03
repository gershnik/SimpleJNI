val gradleExt = gradle as ExtensionAware
class Global {
    val jniGenVersion: String by gradleExt.extra
    val cmakeVersion: String by gradleExt.extra
    val compileSdk: Int by gradleExt.extra
    val minSdk: Int by gradleExt.extra
    val targetSdk: Int by gradleExt.extra
}
val global = Global()

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.devtools.ksp") version "1.9.23-1.0.20"
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
    compileOnly("io.github.gershnik:smjni-jnigen-annotations:${global.jniGenVersion}")
    //JNI code generator
    ksp("io.github.gershnik:smjni-jnigen-kprocessor:${global.jniGenVersion}")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

android {
    compileSdk = global.compileSdk
    namespace = "com.example.myapplication"

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = global.minSdk
        targetSdk = global.targetSdk
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
            version = global.cmakeVersion
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



