@echo off
set JAVA_PATH="java/bin/java.exe"  
set JAR_FILE="kube-link.jar"
set JAR_CONFIG="configs/config/application.yml"
%JAVA_PATH% -jar %JAR_FILE% --spring.config.location=file:%JAR_CONFIG%