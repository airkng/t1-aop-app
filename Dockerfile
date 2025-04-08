FROM amazoncorretto:20-alpine-jdk
COPY target/*.jar aop-app.jar
ENTRYPOINT ["java","-jar","/aop-app.jar"]