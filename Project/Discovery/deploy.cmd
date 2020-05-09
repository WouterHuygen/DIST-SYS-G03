@echo off
@REM Packaging
call mvn clean package
@REM Install in local maven repository
echo installing %1
call mvn install:install-file -Dfile=%%1 -DpomFile=pom.xml
