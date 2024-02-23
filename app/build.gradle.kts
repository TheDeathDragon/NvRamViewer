import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import java.util.Properties
import java.io.FileInputStream

val localProps = Properties().apply {
    load(FileInputStream("local.properties"))
}

val signKeyPassword: String = localProps.getProperty("signKeyPassword")
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "la.shiro.nvramviewer"
    compileSdk = 34

    defaultConfig {
        applicationId = "la.shiro.nvramviewer"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    signingConfigs {
        getByName("debug") {
            keyAlias = "RinShiro"
            keyPassword = signKeyPassword
            storeFile = file("RinShiro.jks")
            storePassword = signKeyPassword
        }
        create("release") {
            keyAlias = "RinShiro"
            keyPassword = signKeyPassword
            storeFile = file("RinShiro.jks")
            storePassword = signKeyPassword
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            isDebuggable = false
        }
        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    applicationVariants.all {
        outputs.all {
            if (this is ApkVariantOutputImpl) {
                outputFileName = "NvRamViewer.apk"
            }
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation(files("/libs/nvram.jar"))
    compileOnly(files("/libs/framework.jar"))
}