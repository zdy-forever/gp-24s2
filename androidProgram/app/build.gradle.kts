import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

if (file("google-services.json").exists()) {
    apply(plugin = "com.google.gms.google-services")
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use(::load)
    }
}

android {
    namespace = "com.example.smartcity"
    compileSdk = 34
    buildFeatures {
        viewBinding=true
    }

    defaultConfig {
        applicationId = "com.example.smartcity"
        minSdk = 34
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        resValue(
            "string",
            "map_key",
            localProperties.getProperty("GOOGLE_MAPS_API_KEY", "")
        )

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

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")
    implementation ("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-maps:18.0.2")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.google.firebase:firebase-firestore")
    implementation ("androidx.lifecycle:lifecycle-common:2.8.6")
    implementation ("androidx.lifecycle:lifecycle-runtime:2.8.6")
    implementation ("androidx.lifecycle:lifecycle-viewmodel:2.8.6")
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.6.1")
    androidTestImplementation("androidx.test:runner:1.6.1")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test:rules:1.6.1")

    implementation("org.osmdroid:osmdroid-android:6.1.10")
    implementation("org.osmdroid:osmdroid-wms:6.1.10")
    implementation ("com.google.android.gms:play-services-auth:20.0.0")
    implementation("org.mapsforge:mapsforge-map:0.14.0")
    implementation("org.mapsforge:mapsforge-map-android:0.14.0")
    implementation("org.mapsforge:mapsforge-themes:0.14.0")
    implementation("org.osmdroid:osmdroid-mapsforge:6.1.10")
    implementation( "com.google.firebase:firebase-messaging:24.0.1")
    implementation (libs.glide)
    annotationProcessor (libs.compiler)
    // Mockito core library
    testImplementation("org.mockito:mockito-core:4.0.0")
    // Mockito for Android Instrumentation tests
    androidTestImplementation("org.mockito:mockito-android:5.5.0")
    androidTestImplementation ("androidx.test:rules:1.5.1")
    testImplementation ("org.robolectric:robolectric:4.9")
}
