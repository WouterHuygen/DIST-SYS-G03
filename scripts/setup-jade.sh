#!/bin/bash

# First of all we need Ant to compile our written .java files

cd /home/pi
mkdir downloads
cd downloads
wget https://apache.belnet.be//ant/binaries/apache-ant-1.10.8-bin.tar.gz
tar -zxvf apache-ant-1.10.8-bin.tar.gz
mv apache-ant-1.10.8 ant
cd ant
ANT_HOME=/home/pi/downloads/ant
PATH=$PATH:${ANT_HOME}/bin
# Install almost all the dependencies the optional Ant tasks need
ant -f fetch.xml -Ddest=system


# Now for JADE

cd /home/pi
mkdir agent
cd agent

# We need both bin and src folders to the pi
wget "https://jade.tilab.com/dl.php?file=JADE-src-4.5.0.zip"
wget "https://jade.tilab.com/dl.php?file=JADE-bin-4.5.0.zip"

# We unzip in them a location called 'agent' at /home/pi
sudo unzip /home/pi/agent/JADE-src-4.5.0.zip
sudo unzip /home/pi/agent/JADE-bin-4.5.0.zip

sudo echo 'export JADE_LIB=/home/pi/agent/jade/lib
export JADE_HOME=/home/pi/agent/jade
export CLASSPATH=$JADE_LIB/jade.jar:$JADE_LIB/commons-codec/commons-codec-1.3.jar:$JADE_HOME/classes' >> /home/pi/.bashrc

sudo source /home/pi/.bashrc

sudo chown -R pi:pi /home/pi/agent/jade