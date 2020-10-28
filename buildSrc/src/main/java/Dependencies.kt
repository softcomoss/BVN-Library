@file:Suppress("KDocMissingDocumentation")

object Versions {
    const val targetSdkVersion = 28
    const val compileSdkVersion = 28
    const val minSdkVersion = 21

    const val versionCode = 1
    const val versionName = "1.0.0"

    const val gradle = "3.5.0"

    const val kotlin = "1.3.70"

    const val coreKtx = "1.0.2"

    const val mockito = "2.25.0"
    const val jUnit = "4.12"
    const val androidJUnit = "1.1.1"
    const val androidTest = "1.1.0"
    const val androidTestRunner = "1.1.1"

    const val lifecycle = "2.0.0"

    const val okhttp = "3.12.0"
    const val retrofit = "2.4.0"
}

object RootDependencies {
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
}

object NetworkDependencies {
    const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val gsonConverter = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    const val rxJavaAdapter = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit}"
}

object AndroidXDependencies {
    const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
    const val lifecycle = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycle}"
    const val lifecycleCompiler = "androidx.lifecycle:lifecycle-compiler:${Versions.lifecycle}"
}

object TestingDependencies {
    const val jUnit = "junit:junit:${Versions.jUnit}"
    const val androidJUnit = "androidx.test.ext:junit:${Versions.androidJUnit}"
    const val androidTestRunner = "androidx.test:runner:${Versions.androidTestRunner}"
    const val androidTest = "androidx.test:core:${Versions.androidTest}"
    const val mockitoCore = "org.mockito:mockito-core:${Versions.mockito}"
}

object Classpaths {
    const val gradle = "com.android.tools.build:gradle:${Versions.gradle}"
}