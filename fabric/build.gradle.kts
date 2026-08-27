plugins {
    id("multiloader-loader")
    alias(libs.plugins.loom)
}

val modId: String = project.property("modId") as String

dependencies {
    minecraft(libs.minecraft)
    implementation(libs.fabricLoader)
    implementation(libs.fabricApi)

    implementation(libs.flk)

    compileOnly("maven.modrinth:area_lib:0.8.3+26.1")

    implementation("org.kodein.di:kodein-di-conf:7.26.1")
    include("org.kodein.di:kodein-di-conf:7.26.1")
    include("org.kodein.di:kodein-di:7.26.1")
    include("org.kodein.type:kaverit:2.10.0")
}

repositories {
    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = uri("https://api.modrinth.com/maven")
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
}

fabricApi {
    configureDataGeneration {
        client = true
        outputDirectory = project(":common").file("src/generated")
        addToResources = false
    }
}

loom {
    val aw = project(":common").file("src/main/resources/${modId}.accesswidener")
    if (aw.exists()) {
        accessWidenerPath.set(aw)
    }
    mixin {
        defaultRefmapName.set("${modId}.refmap.json")
    }
    runs {
        named("client") {
            client()
            setConfigName("Fabric Client")
            ideConfigGenerated(true)
            runDir("runs/client")
        }
        named("server") {
            server()
            setConfigName("Fabric Server")
            ideConfigGenerated(true)
            runDir("runs/server")
        }
    }
}