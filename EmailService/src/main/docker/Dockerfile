FROM openjdk:8-jre-alpine

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    TIME_SLEEP=30 \
    JAVA_OPTS=""

# add directly the war
ADD *.jar /app.jar

EXPOSE 8080
CMD echo "The application will start in ${TIME_SLEEP}s..." && \
    sleep ${TIME_SLEEP} && \
    java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /app.jar
