import java.security.MessageDigest

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.systemadminbd.syshackbar"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.systemadminbd.syshackbar"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

// ─────────────────────── Branding integrity guard ───────────────────────
// SysHackBar is open source, but the SystemAdminBD identity (logo, credit and
// application id) is protected. Any attempt to remove or alter the logo, the
// "coded by systemadminbd" credit, or the application id will fail the build.
val guardAppId: String? = android.defaultConfig.applicationId
val guardLogo = file("src/main/res/drawable-nodpi/brand_logo.png")
val guardDisclaimer = file("src/main/java/com/systemadminbd/syshackbar/ui/screens/DisclaimerScreen.kt")
val guardManifest = file("src/main/AndroidManifest.xml")
val guardBrandGuard = file("src/main/java/com/systemadminbd/syshackbar/data/BrandGuard.kt")
val guardMainActivity = file("src/main/java/com/systemadminbd/syshackbar/MainActivity.kt")

val verifyBranding = tasks.register("verifyBranding") {
    notCompatibleWithConfigurationCache("Performs SystemAdminBD branding integrity checks at build time")
    doLast {
        val expectedLogoSha = "9c008e6f192621113e78be8d8f3fa289916d8b7d744fa3ec91cb83aac4cdc203"
        val expectedAppId = "com.systemadminbd.syshackbar"
        val expectedFingerprint = "6d98b49bb165c2db0ead230eb8ca0f56c580fa61bf88d3ee25649977d0ebe396"

        if (!guardLogo.exists()) {
            throw GradleException("Branding check failed: the SystemAdminBD logo (brand_logo.png) is missing and cannot be removed.")
        }
        val sha = MessageDigest.getInstance("SHA-256")
            .digest(guardLogo.readBytes())
            .joinToString("") { "%02x".format(it) }
        if (sha != expectedLogoSha) {
            throw GradleException("Branding check failed: the SystemAdminBD logo was modified. Restore the original logo to build.")
        }

        if (!guardDisclaimer.exists()) {
            throw GradleException("Branding check failed: the Terms of Use screen is missing.")
        }
        val disclaimerText = guardDisclaimer.readText()
        if (!disclaimerText.contains("coded by systemadminbd")) {
            throw GradleException("Branding check failed: the 'coded by systemadminbd' credit was removed.")
        }
        if (!disclaimerText.contains("R.drawable.brand_logo")) {
            throw GradleException("Branding check failed: the SystemAdminBD logo was removed from the Terms of Use screen.")
        }

        if (guardAppId != expectedAppId) {
            throw GradleException("Branding check failed: the application id was changed from '$expectedAppId'.")
        }

        // App label must stay SysHackBar.
        if (!guardManifest.exists() || !guardManifest.readText().contains("android:label=\"SysHackBar\"")) {
            throw GradleException("Branding check failed: the app label must remain 'SysHackBar'.")
        }

        // Runtime guard must be present, intact and wired into MainActivity.
        if (!guardBrandGuard.exists()) {
            throw GradleException("Branding check failed: the runtime BrandGuard was removed.")
        }
        val brandGuardText = guardBrandGuard.readText()
        if (!brandGuardText.contains(expectedFingerprint)) {
            throw GradleException("Branding check failed: the runtime BrandGuard fingerprint was altered.")
        }
        if (!brandGuardText.contains("\"systemadminbd\"") || !brandGuardText.contains("coded by ")) {
            throw GradleException("Branding check failed: the SystemAdminBD credit was removed from BrandGuard.")
        }
        if (!guardMainActivity.exists() || !guardMainActivity.readText().contains("BrandGuard.verify")) {
            throw GradleException("Branding check failed: the runtime BrandGuard.verify call was removed from MainActivity.")
        }
    }
}

tasks.named("preBuild") {
    dependsOn(verifyBranding)
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.json)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.koin.androidx.compose)
    debugImplementation(libs.androidx.ui.tooling)
}
