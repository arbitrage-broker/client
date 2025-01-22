FROM openjdk:17-alpine
WORKDIR /app
ADD ./target/client-1.0.0-SNAPSHOT.jar /app/application.jar
# Ensure the uploads directory exists
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:MaxMetaspaceSize=128m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
RUN mkdir -p /app/uploads
EXPOSE 443
CMD java $JAVA_OPTS -jar application.jar
