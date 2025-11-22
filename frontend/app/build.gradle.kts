plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.0.21-1.0.25"
    
    // CI/CD Plugins
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.6"
    id("jacoco")
}

android {
    namespace = "com.uade.ticket_mobile"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.uade.ticket_mobile"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    
    // Configuración de Android Lint
    lint {
        abortOnError = false
        ignoreWarnings = false
        warningsAsErrors = false
        checkReleaseBuilds = true
        htmlReport = true
        xmlReport = true
        htmlOutput = file("$buildDir/reports/lint-results.html")
        xmlOutput = file("$buildDir/reports/lint-results.xml")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    
    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    
    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    
    // Room Database
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    
    // DataStore for preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    
    // Accompanist Pager for Onboarding
    implementation("com.google.accompanist:accompanist-pager:0.34.0")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.34.0")
    
    // CameraX - Solución oficial de Google para cámara
    implementation("androidx.camera:camera-core:1.3.1")
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")
    
    // Accompanist Permissions - Manejo de permisos en Compose
    implementation("com.google.accompanist:accompanist-permissions:0.35.0-alpha")
    
    // Coil - Carga de imágenes optimizada
    implementation("io.coil-kt:coil-compose:2.6.0")
    
    // ExifInterface - Para rotar imágenes según orientación
    implementation("androidx.exifinterface:exifinterface:1.3.7")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

// ============================================
// CI/CD Configurations
// ============================================

// Ktlint Configuration
configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    version.set("1.1.1")
    android.set(true)
    ignoreFailures.set(false)
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
    }
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}

// Detekt Configuration
detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom(files("$rootDir/../detekt-config.yml"))
    baseline = file("detekt-baseline.xml")
    
    reports {
        html {
            required.set(true)
            outputLocation.set(file("$buildDir/reports/detekt/detekt.html"))
        }
        xml {
            required.set(true)
            outputLocation.set(file("$buildDir/reports/detekt/detekt.xml"))
        }
        txt {
            required.set(true)
            outputLocation.set(file("$buildDir/reports/detekt/detekt.txt"))
        }
    }
}

// JaCoCo Configuration
tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")
    
    reports {
        xml.required.set(true)
        html.required.set(true)
        xml.outputLocation.set(file("$buildDir/reports/jacoco/jacocoTestReport.xml"))
        html.outputLocation.set(file("$buildDir/reports/jacoco/jacocoTestReport"))
    }
    
    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "**/data/models/**",
        "**/di/**",
        "**/ui/theme/**"
    )
    
    val debugTree = fileTree("${buildDir}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }
    
    val mainSrc = "${project.projectDir}/src/main/java"
    
    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(buildDir) {
        include("jacoco/testDebugUnitTest.exec")
    })
}

// Task para ejecutar todos los checks de calidad
tasks.register("qualityCheck") {
    dependsOn(
        "ktlintCheck",
        "detekt",
        "lintDebug",
        "testDebugUnitTest",
        "jacocoTestReport"
    )
    
    group = "verification"
    description = "Ejecuta todos los checks de calidad de código (ktlint, detekt, lint, tests, coverage)"
}