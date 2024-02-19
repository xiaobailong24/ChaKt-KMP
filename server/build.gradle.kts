import com.codingfeline.buildkonfig.compiler.FieldSpec
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.ktor)
    alias(libs.plugins.buildkonfig)
    application
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    sourceSets {
        jvmMain.dependencies {
//            implementation(libs.generativeai.google)
            implementation(projects.generativeai)
            implementation(libs.logback)
            implementation(libs.ktor.server.core)
            implementation(libs.ktor.server.netty)
            implementation(libs.ktor.server.status.pages)
        }
        jvmTest.dependencies {
            implementation(libs.ktor.server.tests)
            implementation(libs.kotlin.test.junit)
        }
    }
}

group = "server"
version = "1.0.0"
application {
    mainClass.set("ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["development"] ?: "false"}")
}

buildkonfig {
    packageName = "dev.shreyaspatil.chakt.server"

    val localProperties =
        Properties().apply {
            val propsFile = rootProject.file("local.properties")
            if (propsFile.exists()) {
                load(propsFile.inputStream())
            }
        }

    defaultConfigs {
        buildConfigField(
            FieldSpec.Type.STRING,
            "GEMINI_API_KEY",
            localProperties["gemini_api_key"]?.toString() ?: "",
        )
    }
}