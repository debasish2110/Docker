FROM maven:3.8.4-eclipse-temurin-17 AS build
RUN mkdir /app
WORKDIR /app
COPY . .
RUN mvn package

FROM tomcat:latest
COPY --from=build /app/webapp/target/webapp.war /usr/local/tomcat/webapps/webapp.war
RUN cp -R  /usr/local/tomcat/webapps.dist/*  /usr/local/tomcat/webapps
