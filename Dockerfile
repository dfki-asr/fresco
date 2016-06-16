FROM maven:3.3.9-jdk-8

RUN mkdir -p /usr/src/fresco
WORKDIR /usr/src/fresco

ADD . /usr/src/fresco

RUN mvn clean package
CMD mvn tomcat7:run
