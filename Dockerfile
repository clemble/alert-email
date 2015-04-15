FROM java:8-jre
MAINTAINER antono@clemble.com

EXPOSE 10001

ADD target/alert-email-*-SNAPSHOT.jar /data/alert-email.jar

CMD java -jar -Dspring.profiles.active=cloud -Dlogging.config=classpath:logback.cloud.xml -Dserver.port=10001 /data/alert-email.jar
