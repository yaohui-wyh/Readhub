import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.7.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.10"
    id("org.jetbrains.intellij") version "1.8.0"
    id("org.jetbrains.changelog") version "1.3.1"
}

sourceSets {
    main {
        java { srcDir("src") }
        resources { srcDir("resources") }
    }
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.3.1")
    implementation("org.jsoup:jsoup:1.11.3")
}

group = properties("pluginGroup")
version = properties("pluginVersion")

repositories {
    mavenCentral()
}

intellij {
    pluginName.set(properties("pluginName"))
    version.set(properties("platformVersion"))
    type.set(properties("platformType"))
    plugins.set(properties("platformPlugins").split(',').map(String::trim).filter(String::isNotEmpty))
}

changelog {
    version.set(properties("pluginVersion"))
    groups.set(emptyList())
}

tasks {
    wrapper {
        gradleVersion = properties("gradleVersion")
    }

    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    patchPluginXml {
        version.set(properties("pluginVersion"))
        sinceBuild.set(properties("pluginSinceBuild"))
        untilBuild.set(properties("pluginUntilBuild"))
    }

    runIde {
        jvmArgs = listOf("-Xmx2g")
        systemProperty("readhub.internal", true)
    }
}