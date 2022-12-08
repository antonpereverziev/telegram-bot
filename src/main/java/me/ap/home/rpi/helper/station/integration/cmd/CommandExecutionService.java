package me.ap.home.rpi.helper.station.integration.cmd;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class CommandExecutionService {

    public String executeCommandAndGetResultResult(String args[]) {
        try {
            Process p = Runtime.getRuntime().exec(args);
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader er = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String output = IOUtils.toString(p.getInputStream(), StandardCharsets.UTF_8);
            String error = IOUtils.toString(p.getErrorStream(), StandardCharsets.UTF_8);
            if (error != null && !error.isEmpty()) {
                log.info("Error stream: {}", error);
            }
            return output;
        } catch (Exception e) {
            log.error("Some error occurred: " + e.getMessage());
            return e.getMessage();
        }
    }

}
