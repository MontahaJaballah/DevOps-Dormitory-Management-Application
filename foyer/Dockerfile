FROM maven:3.8.4-openjdk-17
WORKDIR /foyer
EXPOSE 8083
ADD target/4TWIN4-BugBusters-Foyer.jar foyer.jar
ENTRYPOINT ["java","-jar","/foyer.jar"]