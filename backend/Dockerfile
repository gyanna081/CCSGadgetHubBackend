FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .

# Fix executable permissions on mvnw script
RUN chmod +x ./mvnw
# Run the build with the same parameters as your CI system is expecting
RUN ./mvnw -DoutputFile=target/mvn-dependency-list.log -B -DskipTests clean dependency:list install

# Stage 2: Run the application
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]