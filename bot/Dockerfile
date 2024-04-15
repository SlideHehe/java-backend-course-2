FROM eclipse-temurin:21-jdk-alpine

COPY target/bot.jar bot.jar

EXPOSE 8090
EXPOSE 18090

ENTRYPOINT ["java","-jar","/bot.jar"]
