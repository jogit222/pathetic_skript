plugins {
    id("java-library")
    id("com.gradleup.shadow") version "9.3.0"
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://repo.skriptlang.org/releases")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    implementation("com.github.bsommerfeld.pathetic-bukkit:core:5.5.0")
    compileOnly("com.github.SkriptLang:Skript:2.15.0")
    implementation("com.github.ShaneBeee:SkriptRegistration:1.5.0")

}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.21.11")
        jvmArgs("-Xms2G", "-Xmx2G")
    }

    processResources {
        val props = mapOf("version" to version)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    shadowJar {
        relocate("com.github.shanebeee.skr", "io.github.pathetic_skript.skr")
        relocate("com.github.bsommerfeld.pathetic-bukkit", "io.github.pathetic_skript.pathetic_bukkit")
    }
}
