# Scripts
## install-essentials.sh
This script will install some essential libraries for the node.

## setup-jade.sh
This script will set-up JADE and ANT.

## setup-maven.sh
This script will set-up maven on the node.

## start-failure-agent.sh
This script that will start the failure agent when the node is failing.

## failure.service
This service will initiate the 'start-failure-agent.sh' script. The service has to be put in the '/lib/systemd/system-shutdown' directory, which is handeled by the systemd-halt service.