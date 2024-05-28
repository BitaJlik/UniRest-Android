plugins {
    id("com.android.application")
}

android {
    namespace = "com.unirest"
    compileSdk = 34
    viewBinding.isEnabled = true

    defaultConfig {
        applicationId = "com.unirest"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Core
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    // Layouts
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    // Navigation
    implementation("androidx.navigation:navigation-fragment:2.7.6")
    implementation("androidx.navigation:navigation-ui:2.7.6")
    // MVVM
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1") // 2.5.1+ NOT WORKS!!!!
    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")  // 2.5.1+ NOT WORKS!!!!
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:adapter-rxjava3:2.9.0")
    // Picasso
    implementation("com.squareup.picasso:picasso:2.71828")
    // Shimmer
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    // GSON
    implementation("com.google.code.gson:gson:2.10.1")
    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-gson:0.12.5")
    // Barcode & QRCode
    implementation("com.github.yuriy-budiyev:code-scanner:2.3.0")

    implementation("com.github.aabhasr1:OtpView:v1.1.2")

    // Zoom imageview
    implementation("com.jsibbold:zoomage:1.3.1")

    // WorkManager for reminders
    implementation ("androidx.work:work-runtime:2.7.0")

    implementation(fileTree("libs"))
}