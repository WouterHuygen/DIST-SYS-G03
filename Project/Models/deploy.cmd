@echo off
@REM Packaging
mvn clean package
@REM Install in local maven repository
mvn install:install-file -Dfile=target/models-1.0.jar -DpomFile=pom.xml