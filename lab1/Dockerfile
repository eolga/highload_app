
FROM openjdk:8-jdk

ENV PORT 8383

EXPOSE 8383

VOLUME /data

COPY ngnixlab.jar /app/ngnixlab.jar

CMD ["java", "-jar","/app/ngnixlab.jar"]
