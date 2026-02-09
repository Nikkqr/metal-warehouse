FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY /warehouse-presentation/target/warehouse-presentation-1.0-SNAPSHOT.jar metalwh.jar

ENTRYPOINT ["java", "-jar", "metalwh.jar"]