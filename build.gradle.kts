plugins {
    alias(libs.plugins.quilt.loom)
}

version = "1.5.0"

configurations {
    compileClasspath { extendsFrom(include.get()) }
}

dependencies {
    minecraft(libs.minecraft)
    mappings(variantOf(libs.fabric.yarn) { classifier("v2") })
    modImplementation("org.quiltmc:qsl:3.0.0-beta.16+1.19.2")
    modImplementation(libs.quilt.loader)
    include("com.github.ben-manes.caffeine:caffeine:3.1.1")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

loom {
    accessWidenerPath.set(file("src/main/resources/betterloading.accesswidener"))
}

tasks {
    withType<JavaCompile>().configureEach {
        options.run {
            encoding = "UTF-8"
            compilerArgs.add("--add-exports=java.base/jdk.internal.vm.annotation=ALL-UNNAMED")
        }
    }
    jar {
        from("LICENSE") {
            rename { "${it}_${archiveBaseName.get()}" }
        }
        manifest {
            attributes["Multi-Release"] = "true"
        }
    }
    processResources {
        filesMatching("quilt.mod.json") {
            expand(mapOf("version" to project.version))
        }
    }
    withType<AbstractArchiveTask>().configureEach {
        isReproducibleFileOrder = true
        isPreserveFileTimestamps = false
    }
}
