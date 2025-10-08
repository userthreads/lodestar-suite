plugins {
    id("fabric-loom") version "1.10-SNAPSHOT"
    id("maven-publish")
    id("com.gradleup.shadow") version "9.0.0-beta11"
}

// Function to generate random 5-character suffix (a-z, 0-9)
fun generateRandomSuffix(): String {
    val chars = "abcdefghijklmnopqrstuvwxyz0123456789"
    return (1..5).map { chars.random() }.joinToString("")
}

// Function to generate deterministic suffix for CI builds
fun generateDeterministicSuffix(): String {
    val buildNumber = project.findProperty("build_number")?.toString() ?: "local"
    val hash = buildNumber.hashCode().toString().takeLast(5).padStart(5, '0')
    return hash
}

base {
    group = properties["maven_group"] as String
    version = "1.0.0"
}

// Custom JAR naming with random suffix
val versionSuffix = generateRandomSuffix()
val baseName = properties["archives_base_name"] as String
val minecraftVersion = properties["minecraft_version"] as String
val customArchivesName = "${baseName}-${minecraftVersion}-${versionSuffix}"

repositories {
    maven {
        name = "Meteor Development"
        url = uri("https://maven.meteordev.org/releases")
    }
    maven {
        name = "Terraformers"
        url = uri("https://maven.terraformersmc.com")
    }
    maven {
        name = "ViaVersion"
        url = uri("https://repo.viaversion.com")
    }
    mavenCentral()

    exclusiveContent {
        forRepository {
            maven {
                name = "modrinth"
                url = uri("https://api.modrinth.com/maven")
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
}

val modInclude: Configuration by configurations.creating
val library: Configuration by configurations.creating

configurations {
    // include mods
    modImplementation.configure {
        extendsFrom(modInclude)
    }
    include.configure {
        extendsFrom(modInclude)
    }

    // include libraries
    implementation.configure {
        extendsFrom(library)
    }
    shadow.configure {
        extendsFrom(library)
    }
}

dependencies {
    // Fabric
    minecraft("com.mojang:minecraft:${properties["minecraft_version"] as String}")
    mappings("net.fabricmc:yarn:${properties["yarn_mappings"] as String}:v2")
    modImplementation("net.fabricmc:fabric-loader:${properties["loader_version"] as String}")

    modInclude(fabricApi.module("fabric-api-base", properties["fapi_version"] as String))
    modInclude(fabricApi.module("fabric-resource-loader-v0", properties["fapi_version"] as String))

    // Compat fixes
    modCompileOnly(fabricApi.module("fabric-renderer-indigo", properties["fapi_version"] as String))
    modCompileOnly("maven.modrinth:sodium:${properties["sodium_version"] as String}") { isTransitive = false }
    modCompileOnly("maven.modrinth:lithium:${properties["lithium_version"] as String}") { isTransitive = false }
    modCompileOnly("maven.modrinth:iris:${properties["iris_version"] as String}") { isTransitive = false }
    modCompileOnly("com.viaversion:viafabricplus:${properties["viafabricplus_version"] as String}") { isTransitive = false }
    modCompileOnly("com.viaversion:viafabricplus-api:${properties["viafabricplus_version"] as String}") { isTransitive = false }

    // Baritone removed - no pathfinding functionality
    // ModMenu (https://github.com/TerraformersMC/ModMenu)
    modCompileOnly("com.terraformersmc:modmenu:${properties["modmenu_version"] as String}")

    // Libraries
    library("meteordevelopment:orbit:${properties["orbit_version"] as String}")
    library("org.meteordev:starscript:${properties["starscript_version"] as String}")
    library("meteordevelopment:discord-ipc:${properties["discordipc_version"] as String}")
    library("org.reflections:reflections:${properties["reflections_version"] as String}")
    library("io.netty:netty-handler-proxy:${properties["netty_version"] as String}") { isTransitive = false }
    library("io.netty:netty-codec-socks:${properties["netty_version"] as String}") { isTransitive = false }
    library("de.florianmichael:WaybackAuthLib:${properties["waybackauthlib_version"] as String}")

    // Launch sub project
    shadow(project(":launch"))
}

loom {
    accessWidenerPath = file("src/main/resources/lodestar-client.accesswidener")
}

afterEvaluate {
    tasks.migrateMappings.configure {
        outputDir.set(project.file("src/main/java"))
    }
}

tasks {
    processResources {
        val buildNumber = project.findProperty("build_number")?.toString() ?: ""
        val commit = project.findProperty("commit")?.toString() ?: ""

        val propertyMap = mapOf(
            "version" to project.version,
            "build_number" to buildNumber,
            "commit" to commit,
            "minecraft_version" to project.property("minecraft_version"),
            "loader_version" to project.property("loader_version")
        )

        inputs.properties(propertyMap)
        filesMatching("fabric.mod.json") {
            expand(propertyMap)
        }
    }

    jar {
        archiveFileName.set("${customArchivesName}.jar")
        
        from("LICENSE") {
            rename { "${it}_${customArchivesName}" }
        }

        manifest {
            attributes["Main-Class"] = "meteordevelopment.meteorclient.Main"
        }
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }

        if (providers.environmentVariable("CI").map { it.toBoolean() }.getOrElse(false)) {
            withSourcesJar()
            withJavadocJar()
        }
    }

    withType<JavaCompile> {
        options.release = 21
        options.compilerArgs.add("-Xlint:deprecation")
        options.compilerArgs.add("-Xlint:unchecked")
    }

    shadowJar {
        configurations = listOf(project.configurations.shadow.get())
        archiveFileName.set("${customArchivesName}-all.jar")

        from("LICENSE") {
            rename { "${it}_${customArchivesName}" }
        }

        dependencies {
            exclude {
                it.moduleGroup == "org.slf4j"
            }
        }
    }

    remapJar {
        dependsOn(shadowJar)
        inputFile.set(shadowJar.get().archiveFile)
        archiveFileName.set("${customArchivesName}.jar")
    }

    javadoc {
        with(options as StandardJavadocDocletOptions) {
            addStringOption("Xdoclint:none", "-quiet")
            addStringOption("encoding", "UTF-8")
            addStringOption("charSet", "UTF-8")
        }
    }

    build {
        if (providers.environmentVariable("CI").map { it.toBoolean() }.getOrElse(false)) {
            dependsOn("javadocJar")
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "lodestar-client"

            version = project.version.toString()
        }
    }
}
