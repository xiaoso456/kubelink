#!/bin/bash
export JAVA_PATH=java/bin
export JAR_FILE=kube-link.jar
export JAR_CONFIG="configs/config/application.yml"
$JAVA_PATH/java -jar $JAR_FILE --spring.config.location=file:$JAR_CONFIG