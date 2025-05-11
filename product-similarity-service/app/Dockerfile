# Fase 1: compilar el JAR
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace
# Copiamos sólo lo necesario para cachear dependencias
COPY pom.xml mvnw ./
COPY .mvn .mvn/
RUN ./mvnw dependency:go-offline -B

# Ahora copiamos el código y empaquetamos
COPY src src
RUN ./mvnw package -DskipTests -B

# Fase 2: runtime ligero
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copiamos el JAR compilado
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 5000
ENTRYPOINT ["java","-jar","/app/app.jar"]
