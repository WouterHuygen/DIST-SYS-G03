@REM build dependencies
cd Models
call deploy target/models-1.0.jar
cd ../Discovery
call deploy target/discovery-1.1.jar
@REM build Node project
cd ../Node
call mvnw clean package
@REM build Naming project
cd ../Naming-Server
call mvnw clean package
cd ..
