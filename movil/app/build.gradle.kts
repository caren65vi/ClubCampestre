// Módulo móvil del Club Campestre Villa Katy: gestión de empleados desde Android.
plugins {
    id("com.android.application")
}

android {
    namespace = "co.edu.sena.villakaty"
    compileSdk = 36

    defaultConfig {
        applicationId = "co.edu.sena.villakaty"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        // Dirección de la API: 10.0.2.2 es el "localhost" del computador visto desde el emulador
        buildConfigField("String", "API_URL", "\"http://10.0.2.2:8080/api\"")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.recyclerview:recyclerview:1.4.0")

    testImplementation("junit:junit:4.13.2")
    // Implementación real de org.json para las pruebas en la JVM (en Android ya viene incluida)
    testImplementation("org.json:json:20240303")
}
