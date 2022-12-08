package me.ap.home.rpi.helper.station.pi.sensor.weather;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
@Slf4j
public class TemperatureSensor {


    public Float getTemperature() {
        String args[] = {"cat", "/sys/bus/w1/devices/28-3c01e076cee2/w1_slave"};
        StringBuilder stringBuilder = new StringBuilder("Temperature: \n");
        try {
            Process p = Runtime.getRuntime().exec(args);
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = in.readLine();
            while (line != null) {
                stringBuilder.append(line);
                line = in.readLine();
            }
        } catch (Exception e) {
            log.error("Some error occurred: {}", e.getMessage());
        }
        return parseTextFile(stringBuilder.toString());
    }

    /*
    90 00 55 05 7f a5 a5 66 89 : crc=89 YES
    90 00 55 05 7f a5 a5 66 89 t=9000
    */
    public float parseTextFile(String rawText) {
        String[] arr = rawText.split("=");
        if (arr.length > 2) {
            return ((float) Integer.parseInt(arr[2])) / 1000;
        }
        return 1000f;
    }

}
