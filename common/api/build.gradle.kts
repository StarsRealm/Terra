dependencies {
    api("ca.solo-studios", "strata", Versions.Libraries.strata)
    compileOnlyApi("org.slf4j", "slf4j-api", Versions.Libraries.slf4j)
    testImplementation("org.slf4j", "slf4j-api", Versions.Libraries.slf4j)
    api(files(rootProject.file("/templibs/cloud-core-2.0.0-rc.2-all.jar")))

    api("com.dfsek.tectonic", "common", Versions.Libraries.tectonic)

    api("com.github.ben-manes.caffeine", "caffeine", Versions.Libraries.caffeine)

}