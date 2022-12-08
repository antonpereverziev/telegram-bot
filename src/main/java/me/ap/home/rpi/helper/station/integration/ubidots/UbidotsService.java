package me.ap.home.rpi.helper.station.integration.ubidots;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;

import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.nio.charset.StandardCharsets;


@Slf4j
@Service
public class UbidotsService {

    private ObjectMapper mapper = null;

    public UbidotsService() {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void sendData(double temperature) throws Exception {
        String fooResourceUrl
                = "http://industrial.api.ubidots.com/api/v1.6/devices/temperature-sensor";

        HttpPost httpPost = new HttpPost(new URL(fooResourceUrl).toURI());
        httpPost.addHeader("Content-Type", "application/json");
        //httpPost.addHeader("Accept", ContentType.APPLICATION_JSON.getMimeType());
        httpPost.addHeader("X-Auth-Token", "BBFF-GGcEOpemMDaPKulSosFqvkSmXQvZq2");

        String request = mapper.writeValueAsString(new TemperatureHolder(temperature));

        httpPost.setEntity(new StringEntity(request));
        try (CloseableHttpResponse httpResponse = HttpClientBuilder.create()
                .build().execute(httpPost)) {
            String json = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
            if (httpResponse.getStatusLine().getStatusCode() > 201) {
                log.error("Ubidots server returned status code {} body: {}",
                        httpResponse.getStatusLine().getStatusCode(), json);
            }
            log.debug("Ubidots response :{}", json);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}

@Data
@AllArgsConstructor
class TemperatureHolder {
    private double temperature;
}
