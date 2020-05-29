#!/bin/bash

cd /home/pi

sudo apt update
sudo apt upgrade

# Curl
sudo apt install curl
printf "\n"
curl --version
printf "\n"

# Nodejs, node, NPM
curl -sL https://deb.nodesource.com/setup_12.x | sudo -E bash -
sudo apt install nodejs
printf "\n"
node --version
npm --version
printf "\n"

# ZIP and UNZIP
sudo apt-get install zip unzip
printf "\n"
zip --version
unzip --version
printf "\n"

# Nano
sudo apt install nano
printf "\n"
nano --version
printf "\n"

# Wget
sudo apt install wget
printf "\n"
nano --version
printf "\n"

# Java
sudo apt install default-jdk
sudo echo 'export JAVA_HOME=/usr/lib/jvm/default-java' >> /home/pi/.bashrc
sudo source /home/pi/.bashrc