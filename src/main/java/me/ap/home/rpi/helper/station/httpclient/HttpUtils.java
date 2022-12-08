package me.ap.home.rpi.helper.station.httpclient;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.auth.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.*;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

@Slf4j
public class HttpUtils {

    public static byte[] downloadFileWithDigitAuth(String url, String username, String password) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);
        HttpContext httpContext = new BasicHttpContext();
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpGet, httpContext);

            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
                Header authHeader = httpResponse.getFirstHeader(AUTH.WWW_AUTH);
                DigestScheme digestScheme = new DigestScheme();

                digestScheme.overrideParamter("realm", "User Login Required !!");
                digestScheme.processChallenge(authHeader);

                UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);
                httpGet.addHeader(digestScheme.authenticate(creds, httpGet, httpContext));

                httpResponse.close();
                httpResponse = httpClient.execute(httpGet);
            }
            return IOUtils.toByteArray(httpResponse.getEntity().getContent());
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return null;
    }
}
