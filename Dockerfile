FROM openjdk:17-alpine
WORKDIR /app
ADD ./target/client-1.0.0-SNAPSHOT.jar /app/application.jar
# Ensure the uploads directory exists
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:MaxMetaspaceSize=128m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:MaxRAMPercentage=75"
RUN mkdir -p /app/uploads
EXPOSE 443
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar application.jar"]