#!/bin/bash

APP_HOME="/home/ec2-user/app"
JAR_FILE="$APP_HOME/s3-memorydb-loader-1.0.jar"
CONFIG_FILE="$APP_HOME/config/application.properties"
LOG_DIR="$APP_HOME/logs"

# Create log directory if it doesn't exist
mkdir -p $LOG_DIR

# Run the application
java -jar $JAR_FILE \
    --spring.config.location=file:$CONFIG_FILE \
    >> $LOG_DIR/application.log 2>&1
