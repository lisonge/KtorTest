plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.ksp)
}

android {
    namespace = "ktor.test"
    compileSdk = 35
    buildToolsVersion = "35.0.0"
    defaultConfig {
        applicationId = "ktor.test"
        minSdk = 26
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        all {
            // use android sdk keystore
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.majorVersion
    }
    buildFeatures {
        compose = true
    }

    dependenciesInfo.includeInApk = false
    packagingOptions.resources.excludes += setOf(
        // https://github.com/Kotlin/kotlinx.coroutines/issues/2023
        "META-INF/**", "**/attach_hotspot_windows.dll",

        "**.properties", "**.bin", "**/*.proto",
        "**/kotlin-tooling-metadata.json",

        // ktor
        "**/custom.config.conf",
        "**/custom.config.yaml",
    )
}

configurations.configureEach {
    //    https://github.com/Kotlin/kotlinx.coroutines/issues/2023
    exclude("org.jetbrains.kotlinx", "kotlinx-coroutines-debug")
}

dependencies {

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.compose.ui)
    implementation(libs.compose.preview)
    debugImplementation(libs.compose.tooling)
    androidTestImplementation(libs.compose.junit4)

    implementation(libs.compose.activity)
    implementation(libs.compose.material3)
    implementation(libs.compose.navigation)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso)

    implementation(libs.rikka.shizuku.api)

    implementation(libs.androidx.room.runtime)

    implementation(libs.androidx.paging.runtime)

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cio)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.utilcodex)
    implementation(libs.floatingBubbleView)

    implementation(libs.destinations.core)
    ksp(libs.destinations.ksp)

    implementation(libs.coil.compose)

    implementation(libs.toaster)


}