import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

// [MỚI] Đọc API key AI từ file local.properties (file này không được đưa lên Git)
val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

android {
    namespace = "com.example.aimentor"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.aimentor"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // [MỚI] Tạo hằng BuildConfig.ANTHROPIC_API_KEY để dùng trong code Kotlin
        buildConfigField(
            "String",
            "ANTHROPIC_API_KEY",
            "\"${localProps.getProperty("ANTHROPIC_API_KEY", "")}\""
        )
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    // [MỚI] Bật sinh class BuildConfig (AGP 8+ mặc định tắt)
    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // [MỚI] Gọi API: Retrofit + Gson converter
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    // [MỚI] Tải ảnh từ URL
    implementation(libs.glide)
    // [MỚI] RecyclerView + CardView (thêm tường minh)
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")
    // [MỚI] lifecycleScope để chạy Coroutines trong Fragment
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    // [MỚI] ViewModel + viewModelScope cho màn hình chat AI
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}