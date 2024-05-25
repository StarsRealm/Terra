plugins {
    id("xyz.jpenilla.run-paper") version "2.2.4"
    id("io.papermc.paperweight.userdev") version "1.7.1"
    java
    `maven-publish`
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    paperweight.devBundle("com.starsrealm.nylon", "1.20.6-R0.4-STARSREALM-SNAPSHOT")
    shadedApi("io.papermc", "paperlib", Versions.Bukkit.paperLib)
    shadedApi(project(":common:implementation:base"))
    shadedApi("com.google.guava", "guava", Versions.Libraries.Internal.guava)
    shadedApi("cloud.commandframework", "cloud-paper", Versions.Libraries.cloud)
    shadedApi("xyz.jpenilla", "reflection-remapper", Versions.Bukkit.reflectionRemapper)
}

tasks {
    shadowJar {
        relocate("io.papermc.lib", "com.dfsek.terra.lib.paperlib")
        relocate("com.google.common", "com.dfsek.terra.lib.google.common")
        relocate("org.apache.logging.slf4j", "com.dfsek.terra.lib.slf4j-over-log4j")
        exclude("org/slf4j/**")
        exclude("org/checkerframework/**")
        exclude("org/jetbrains/annotations/**")
        exclude("org/intellij/**")
        exclude("com/google/errorprone/**")
        exclude("com/google/j2objc/**")
        exclude("javax/**")
    }

    runServer {
        minecraftVersion(Versions.Bukkit.minecraft)
        dependsOn(shadowJar)
        pluginJars(shadowJar.get().archiveFile)
    }
}


addonDir(project.file("./run/plugins/Terra/addons"), tasks.named("runServer").get())
