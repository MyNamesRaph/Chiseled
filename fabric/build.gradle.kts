plugins {
    id("multiloader-loader")
    alias(libs.plugins.loom)
}

val modId: String by project

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${libs.versions.parchmentMC.get()}:${libs.versions.parchment.get()}@zip")
    })
    modImplementation(libs.fabricLoader)
    modImplementation(libs.fabricApi)

    modImplementation(libs.flk)

    modImplementation("maven.modrinth:area_lib:0.3.6+1.21.8")

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