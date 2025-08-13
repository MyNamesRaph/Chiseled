import org.gradle.internal.extensions.stdlib.capitalized

plugins {
    id("multiloader-loader")
    alias(libs.plugins.moddev)
}

val modId: String by project

neoForge {
    version = libs.versions.neoforge.get()
    // Automatically enable neoforge AccessTransformers if the file exists
    val at = project(":common").file("src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists()) {
        accessTransformers.from(at.absolutePath)
    }
    parchment {
        minecraftVersion = libs.versions.parchmentMC
        mappingsVersion = libs.versions.parchment
    }
    runs {
        configureEach {
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
            ideName = "NeoForge ${name.capitalized()} (${project.path})" // Unify the run config names with fabric
        }
        register("client") {
            client()
        }
        /*register("clientData") {
            data()
        }*/
        register("server") {
            server()
        }
    }
    mods {
        register(modId) {
            sourceSet(sourceSets.main.get())
        }
    }
}

repositories {
    flatDir {
        dir("libs")
    }
}

sourceSets.main.get().resources { srcDir("src/generated/resources") }

dependencies {
    implementation(libs.kff)

    compileOnly("org.kodein.di:kodein-di-conf:7.26.1")

    // Manually patched jars to make them register as mods and add an Automatic-Module-Name to the manifest
    val kodein = implementation("org.kodein.di:kodein-di-jvm:7.26.1-patch") {}
    val kodeinConf = implementation("org.kodein.di:kodein-di-conf-jvm:7.26.1-patch") {}
    val kaverit = implementation("org.kodein.type:kaverit-jvm:2.10.0-patch") {}

    jarJar(kodein)
    jarJar(kodeinConf)
    jarJar(kaverit)
}