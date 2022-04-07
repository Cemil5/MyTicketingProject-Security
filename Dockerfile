FROM openjdk:11-jdk
COPY ./target/MyTicketingProject-Security-0.0.1-SNAPSHOT.jar  /usr/app/
WORKDIR /usr/app
RUN sh -c 'touch MyTicketingProject-Security-0.0.1-SNAPSHOT.jar'
EXPOSE 8080
ENTRYPOINT ["java","-jar","MyTicketingProject-Security-0.0.1-SNAPSHOT.jar"]