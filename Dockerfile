FROM openjdk1.8-jdk-alpine
VOLUME /tmp
COPY ./target/guli_parent.jar guli_parent.jar
ENTRYPOINT ["java","-jar","/guli_parent.jar","&"]
