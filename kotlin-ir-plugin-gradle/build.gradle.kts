plugins {
    id("java-gradle-plugin")
    kotlin("jvm")
    id("com.github.gmazzo.buildconfig")
    id("org.jlleitschuh.gradle.ktlint")
}

dependencies {
    implementation(kotlin("gradle-plugin-api"))
}

buildConfig {
    val project = project(":kotlin-ir-plugin")
    packageName(project.group.toString())
    buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${rootProject.extra["kotlin_plugin_id"]}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_GROUP", "\"${project.group}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_NAME", "\"${project.name}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_VERSION", "\"${project.version}\"")
}

gradlePlugin {
    plugins {
        create("kotlinEvalPlugin") {
            id = rootProject.extra["kotlin_plugin_id"] as String
            displayName = "Kotlin Eval Plugin"
            description = "A Kotlin compiler plugin that evaluates functions beginning with \"eval\" at compile-time, handling basic operations on constants, control flow expressions, and variable assignments in the Kotlin IR."
            implementationClass = "com.rendox.evalplugin.EvalGradlePlugin"
        }
    }
}

ktlint {
    filter {
        exclude { entry ->
            entry.file.toString().contains("generated")
        }
    }
}
