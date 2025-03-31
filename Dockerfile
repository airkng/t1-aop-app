FROM amazoncorretto:latest
COPY target/*.jar aop-app.jar
ENTRYPOINT ["java","-jar","/aop-app.jar"]