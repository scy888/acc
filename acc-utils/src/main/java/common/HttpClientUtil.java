package common;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

/**
 * HttpClient工具类(JDK11)
 *
 * @author chong.xie
 * @since 2020/12/10
 */
@Slf4j
public class HttpClientUtil {
    /**
     * POST同步请求
     * @param url
     * @param requestParameters
     * @return
     */
    public static String post(String url, String requestParameters) {
        String responseBody = "";
        log.info("HttpRequest:{}", JsonUtil.toJson(requestParameters));
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofString(requestParameters))
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            responseBody = httpResponse.body();
            log.info("HttpResponse.body:{}", JsonUtil.toJson(responseBody));
        } catch (IOException e) {
            log.error("error", e);
        } catch (InterruptedException e) {
            log.error("error", e);
        } catch (Exception e) {
            log.error("error", e);
        }
        return responseBody;
    }

    public static String get(String url) {
        log.info("requestUrl:{}", url);
        String body = "";
        try {
            String encoded = new String(Base64.getEncoder().encode("login:password".getBytes()));
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
                    .GET()
                    .setHeader("Proxy-Authorization", "Basic " + encoded)
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            body = response.body();
            log.info("responseBody:{}", body);
        } catch (IOException e) {
            log.error("error", e);
        } catch (InterruptedException e) {
            log.error("error", e);
        }

        return body;
    }





}
