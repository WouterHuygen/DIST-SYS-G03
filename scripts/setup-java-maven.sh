#!/bin/bash
sudo apt update

sudo apt upgrade

sudo apt install default-jdk
 
sudo apt install wget

sudo apt install zip

cd /tmp && wget http://mirrors.koehn.com/apache/maven/maven-3/3.5.4/binaries/apache-maven-3.5.4-bin.zip

unzip apache-maven-3.5.4-bin.zip

sudo mv apache-maven-3.5.4 /opt/apache-maven-3.5.4

sudo ln -s /opt/apache-maven-3.5.4 /opt/maven

cd /home/pi

mvnDir=/etc/profile.d/maven.sh

sudo touch $mvnDir

sudo chmod 777 $mvnDir

echo "export PATH=${M2_HOME}/bin:${PATH}" >> $mvnDir
echo "export MAVEN_HOME=/opt/maven" >> $mvnDir
echo "export M2_HOME=/opt/maven" >> $mvnDir
echo "export JAVA_HOME=/usr/lib/jvm/default-java" >> $mvnDir

source $mvnDir

mvn -version

