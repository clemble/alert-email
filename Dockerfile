FROM java:8-jre
MAINTAINER antono@clemble.com

EXPOSE 10001

ADD target/alert-email-0.17.0-SNAPSHOT.jar /data/alert-email.jar

CMD java -jar -Dspring.profiles.active=cloud -Dserver.port=10001 /data/alert-email.jar
