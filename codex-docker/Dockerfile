FROM adoptopenjdk/openjdk11:ubi

COPY build/libs/codex.jar /opt/app/codex.jar
ENTRYPOINT ["java"]
CMD ["-jar", "/opt/app/codex.jar"]
EXPOSE 7000