
FROM openjdk:8-jdk

ENV PORT 8383

EXPOSE 8383

VOLUME /data

COPY redislab.jar /data/redislab.jar

CMD ["java","-jar","/data/redislab.jar"]
