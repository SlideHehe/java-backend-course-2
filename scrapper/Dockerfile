FROM eclipse-temurin:21-jdk-alpine

COPY target/scrapper.jar scrapper.jar

EXPOSE 8080
EXPOSE 18080

ENTRYPOINT ["java","-jar","/scrapper.jar"]
