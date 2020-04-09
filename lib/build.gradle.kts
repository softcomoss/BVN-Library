import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
}

android {
    compileSdkVersion(Versions.compileSdkVersion)

    defaultConfig {
        minSdkVersion(Versions.minSdkVersion)
        targetSdkVersion(Versions.targetSdkVersion)

        versionCode = Versions.versionCode
        versionName = Versions.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType < KotlinCompile > {
        kotlinOptions.jvmTarget = "1.8"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(RootDependencies.kotlin)

    implementation(AndroidXDependencies.coreKtx)
    implementation(AndroidXDependencies.lifecycle)

    api(NetworkDependencies.retrofit)
    api(NetworkDependencies.okhttp)
    api(NetworkDependencies.gsonConverter)
    api(NetworkDependencies.loggingInterceptor)
    api(NetworkDependencies.rxJavaAdapter)

    annotationProcessor(AndroidXDependencies.lifecycleCompiler)

    testImplementation(TestingDependencies.jUnit)
    androidTestImplementation(TestingDependencies.androidJUnit)

}