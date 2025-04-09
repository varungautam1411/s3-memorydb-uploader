#!/bin/bash

# Build the application
mvn clean package

# Copy to EC2
scp -i "ec2.pem" /Users/bsvag/Downloads/s3-memorydb-loader-1.0.jar \
    ec2-user@ec2-3-148-255-133.us-east-2.compute.amazonaws.com:/home/ec2-user/app/

# Copy configuration
scp -i "ec2.pem" src/main/resources/application.properties \
    ec2-user@ec2-3-148-255-133.us-east-2.compute.amazonaws.com:/home/ec2-user/app/config/

# Copy run script
scp -i "ec2.pem" scripts/run.sh \
    ec2-user@ec2-3-148-255-133.us-east-2.compute.amazonaws.com:/home/ec2-user/app/

