plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.7.1"
}

group = "io.github.xiaozhuai"
version = "2.0.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        create("IU", "2025.2")
        plugin("PsiViewer:252.23892.248")
        plugin("com.cursiveclojure.cursive:2025.2-252")
        plugin("org.jetbrains.plugins.ruby:252.23892.360")
        plugin("Dart:252.24322.5")
        plugin("org.jetbrains.plugins.go:252.23892.360")
        plugin("com.jetbrains.php:252.23892.458")
        plugin("com.perl5:2025.2")
        plugin("PythonCore:252.23892.458")
        plugin("Pythonid:252.23892.458")

//        create("CL", "2025.2")
//        plugin("PsiViewer:252.23892.248")
//        plugin("com.jetbrains.rust:252.23892.452")
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "212"
        }
    }
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }

    buildSearchableOptions{
        enabled = false
    }

    signPlugin {
        certificateChain.set(System.getenv("INTELLIJ_CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("INTELLIJ_PRIVATE_KEY"))
        password.set(System.getenv("INTELLIJ_PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("INTELLIJ_PUBLISH_TOKEN"))
    }
}
