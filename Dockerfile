# --- Etapa 1: Construcción (Builder) ---
# Usa una imagen de Gradle con JDK 21
FROM gradle:jdk21-ubi AS builder

# Establecemos el directorio de trabajo dentro del contenedor para esta etapa
WORKDIR /app

# Copiamos primero los archivos de definición de Gradle y el wrapper.
# Esto ayuda a Docker a cachear esta capa si estos archivos no cambian,
# haciendo las reconstrucciones más rápidas si solo cambias el código fuente.
COPY build.gradle.kts settings.gradle.kts gradlew gradle.properties ./
COPY gradle ./gradle

# Copiamos el resto del código fuente de la aplicación
COPY src ./src

# Ejecutamos el comando de Gradle para construir la aplicación y generar el JAR.
# El JAR ejecutable se creará (según nuestra configuración de build.gradle.kts)
# en build/libs/kotlin-docker-hello-1.0-SNAPSHOT.jar
# Usamos -x test para saltar los tests durante la construcción de la imagen (opcional, pero común).
RUN gradle build -x test

# --- Etapa 2: Ejecución (Runtime) ---
# Usamos una imagen base ligera con solo Java Runtime Environment (JRE) de OpenJDK 21.
FROM amazoncorretto:8u452-alpine3.21-jre AS runtime

# Establecemos el directorio de trabajo para esta etapa final
WORKDIR /app

# Copiamos ÚNICAMENTE el JAR compilado desde la etapa 'builder' a esta etapa final.
COPY --from=builder /app/build/libs/kotlin-docker-hello-1.0-SNAPSHOT.jar ./app.jar

# Comando que se ejecutará cuando el contenedor inicie, usando el JAR que hemos copiado.
CMD ["java", "-jar", "app.jar"]