# Home Telegram bot 

Telegram bot for easy management of smart home. Able to get current temperature, make screenshots from ip camera, show alarms sent by camera. 
Bot is integrated with eWeLink environment via cloud, can manage Shelly devices via local network.


Supported sensors:
- Temperature: `DS18B20`

Supported devices:
- Sonoff 4CH relay
- Shelly1 relay
- Dahua IP camera

## Prepare RPi
- Install fresh Raspbian according to your model.
- Configure static IP for RaspberryPI to be able to receive camera logs.  
- Install Java 15 distro.
- Install latest maven.
- Install git. Create and upload necessary ssh keys to github.
- Clone repo to RPi.
- Find misc/upgrade.sh file and execute it.
- Configure bot application to run as a service. Useful commands and config can be found in misc/telegram-bot.service.

## Prepare Ip camera
- Enable syslog in IP camera settings. Set RPi's IP address as the log server. Enable smart motion detection.

## Prepare bot
- Create [Telegram bot](https://core.telegram.org/bots#creating-a-new-bot) and retrieve the token
- Set Telegram bot token at application.yaml

## Build and run
- [Install Pi4J](http://pi4j.com/install.html#Installation) on your Raspberry Pi
- Connect `DS18B20` sensor to your Raspberry Pi

## Notes
