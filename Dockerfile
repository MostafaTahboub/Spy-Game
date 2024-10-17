FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .

RUN mvn dependency:go-offline -B
COPY . .
RUN mvn clean package -DskipTests

#// docker build build-arg PROFLE=prod
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
#backend-${PROFILE}
COPY --from=build /app/target/*.jar exec.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/exec.jar"]
#//docker run -e PROFLE=prod DATABASE_URL=postgres
#// export DATABASE_URL=postgres

