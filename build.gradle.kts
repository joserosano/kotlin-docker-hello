plugins {
    kotlin("jvm") version "2.1.20" // Tu versión de Kotlin
    application                   // Plugin para aplicaciones ejecutables
}

group = "com.joserosano"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8")) // Biblioteca estándar de Kotlin
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21) // Correcto: Usar JDK 21 para compilar
}

// Configuración para el plugin 'application'
application {
    // Define la clase que contiene tu función main()
    // Para Main.kt en el paquete com.joserosano, la clase generada es com.joserosano.MainKt
    mainClass.set("com.joserosano.MainKt")
}

// Configuración para la tarea 'jar' (la que crea el archivo .jar)
tasks.jar {
    manifest {
        // Esto añade el atributo 'Main-Class' al MANIFEST.MF del JAR
        attributes["Main-Class"] = application.mainClass.get()
    }

    // Las siguientes líneas son para crear un "fat JAR" (incluir dependencias dentro del JAR).
    // Para tu "Hola Mundo" actual, que solo usa la stdlib de Kotlin, no es estrictamente
    // necesario si la imagen Docker base del JRE ya la tuviera o la pudiera resolver.
    // Pero es una práctica común para aplicaciones más complejas. No hace daño tenerlo.
    configurations["runtimeClasspath"].forEach { file ->
        from(zipTree(file.absoluteFile)) {
            // Evitar duplicados de archivos de manifiesto de dependencias si es necesario
            // exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
        }
    }
    // duplicatesStrategy = DuplicatesStrategy.EXCLUDE // O INCLUDE, o FAIL
    // Para fat JARs, a veces necesitas una estrategia para manejar archivos duplicados de las dependencias.
    // 'EXCLUDE' es a menudo una opción segura para empezar.
    duplicatesStrategy = DuplicatesStrategy.INCLUDE // Lo dejamos como lo tenías, puede ser necesario ajustarlo si hay conflictos.
}