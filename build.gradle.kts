buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath (kotlin("gradle-plugin", Versions.kotlin))
        classpath (Classpaths.androidMaven)
        classpath (Classpaths.gradle)
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://jitpack.io")
    }
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
