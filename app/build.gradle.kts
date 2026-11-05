plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.mylibrary"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.mylibrary"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
