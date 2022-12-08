#!/bin/bash

cd /home/pi/home-telegram-bot
git pull
mvn clean install
ps -ef | grep 'java' | grep -v grep | awk '{print $2}' | xargs -r kill -9
rm /home/pi/home-telegram-bot/app.jar
mv /home/pi/home-telegram-bot/target/pi-telegram-bot.jar /home/pi/home-telegram-bot/app.jar
chmod 777 /home/pi/home-telegram-bot/app.jar
nohup java -jar /home/pi/home-telegram-bot/app.jar > /home/pi/logs.txt 2>&1&