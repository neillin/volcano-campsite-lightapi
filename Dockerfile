
FROM openjdk:11.0.3-slim


COPY /target/volcano-campsite-1.00.jar server.jar


CMD ["/bin/sh","-c","exec java -Dlight-4j-config-dir=/config -Dlogback.configurationFile=/config/logback.xml -jar /server.jar"]
