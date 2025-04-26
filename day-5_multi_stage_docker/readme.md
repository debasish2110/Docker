
# Maven and Tomcat Docker Setup

This repository provides a Docker setup that uses Maven and Tomcat to build and deploy a web application. It contains multiple approaches for building the application and deploying it with Tomcat, using either Maven and Tomcat images directly or installing both on Ubuntu.

## Table of Contents

- [Overview](#overview)
- [Setup](#setup)
  - [Maven and Tomcat Images](#maven-and-tomcat-images)
  - [Maven on Ubuntu](#maven-on-ubuntu)
  - [Maven and Tomcat on Ubuntu](#maven-and-tomcat-on-ubuntu)
- [Usage](#usage)
- [Customizing the Build](#customizing-the-build)
- [License](#license)

## Overview

This repository demonstrates how to build and deploy a Java web application using Maven for building the project and Tomcat as the application server. We provide several different Dockerfile configurations:

1. **Maven and Tomcat images**: Uses official Maven and Tomcat images to build and deploy the application.
2. **Maven on Ubuntu**: Installs Maven on an Ubuntu-based image.
3. **Maven and Tomcat on Ubuntu**: Both Maven and Tomcat are installed on an Ubuntu-based image, providing more control over the environment.

## Setup

### Maven and Tomcat Images

In this approach, we use official Maven and Tomcat Docker images to build and deploy the web application:

```dockerfile
# Build stage
FROM maven:3.8.4-eclipse-temurin-17 AS build
RUN mkdir /app
WORKDIR /app
COPY . .
RUN mvn package

# Tomcat stage
FROM tomcat:latest
COPY --from=build /app/webapp/target/webapp.war /usr/local/tomcat/webapps/webapp.war
RUN cp -R  /usr/local/tomcat/webapps.dist/*  /usr/local/tomcat/webapps
```

This setup consists of two stages:
1. **Build Stage**: It uses the Maven image to compile the project and package it as a `.war` file.
2. **Tomcat Stage**: It uses the Tomcat image to copy the `.war` file into the Tomcat webapps directory.

### Maven on Ubuntu

In this configuration, we install Maven on an Ubuntu-based image:

```dockerfile
FROM ubuntu:latest as builder
RUN apt-get update &&     apt-get install -y openjdk-8-jdk wget unzip

ARG MAVEN_VERSION=3.9.6
RUN wget https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz &&     tar -zxvf apache-maven-${MAVEN_VERSION}-bin.tar.gz &&     rm apache-maven-${MAVEN_VERSION}-bin.tar.gz &&     mv apache-maven-${MAVEN_VERSION} /usr/lib/maven

ENV MAVEN_HOME /usr/lib/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"
ENV PATH=$MAVEN_HOME/bin:$PATH

RUN mkdir -p /app
COPY . /app
WORKDIR /app
RUN mvn install
```

This Dockerfile:
1. Installs OpenJDK 8 and Maven on an Ubuntu-based image.
2. Downloads and installs the specified version of Maven.
3. Builds the project using Maven.

### Maven and Tomcat on Ubuntu

This approach installs both Maven and Tomcat in a custom Ubuntu image, giving full control over the environment:

```dockerfile
# Build stage: Installing Maven and building the project
FROM ubuntu:20.04 AS build

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y     openjdk-11-jdk     maven     wget     curl     git     && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY . .
RUN mvn clean package

# Tomcat runtime stage: Installing Tomcat
FROM ubuntu:20.04

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y     openjdk-11-jdk     wget     curl     && rm -rf /var/lib/apt/lists/*

RUN wget https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.95/bin/apache-tomcat-9.0.95.tar.gz -O /tmp/tomcat.tar.gz &&     mkdir /opt/tomcat &&     tar xzvf /tmp/tomcat.tar.gz -C /opt/tomcat --strip-components=1 &&     rm /tmp/tomcat.tar.gz

ENV CATALINA_HOME=/opt/tomcat
ENV PATH=$CATALINA_HOME/bin:$PATH

EXPOSE 8080

COPY --from=build /app/webapp/target/webapp.war /opt/tomcat/webapps/webapp.war

CMD ["/opt/tomcat/bin/catalina.sh", "run"]
```

This setup:
1. Installs Maven and OpenJDK on Ubuntu.
2. Builds the `.war` file using Maven.
3. Installs Tomcat on Ubuntu and deploys the generated `.war` file to the Tomcat server.

## Usage

1. Clone the repository:
   ```bash
   git clone https://github.com/your-repository-name.git
   cd your-repository-name
   ```

2. Build the Docker image:
   ```bash
   docker build -t my-java-webapp .
   ```

3. Run the Docker container:
   ```bash
   docker run -p 8080:8080 my-java-webapp
   ```

4. Access the web application by navigating to `http://localhost:8080/webapp`.


