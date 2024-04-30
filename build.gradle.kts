plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.3"
}

group = "io.github.xiaozhuai"
version = "1.1.3"

repositories {
    mavenCentral()
}

intellij {
    version.set("2021.2")
    type.set("IU")
    // version.set("2024.1")
    // type.set("CL")
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    buildSearchableOptions{
        enabled = false
    }

    patchPluginXml {
        sinceBuild.set("212")
        untilBuild.set("241.*")
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

dependencies {

}
