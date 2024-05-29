import org.jetbrains.kotlin.utils.addToStdlib.safeAs

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
}

buildscript {
    repositories {
        google()  // Agregar repositorio Maven de Google
        mavenCentral()
    }
    dependencies {
        val nav_version = "2.7.7"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version");
        classpath("com.android.tools.build:gradle:7.1.0")
    }

}
