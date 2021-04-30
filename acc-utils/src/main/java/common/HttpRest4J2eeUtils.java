package common;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * @author: scyang
 * @program: tensquare_parent
 * @package: common
 * @date: 2020-03-20 20:39:00
 * @describe:
 */
public class HttpRest4J2eeUtils {
    private static final HttpRest4J2eeUtils _instance = new HttpRest4J2eeUtils();
    private static final Logger logger= LoggerFactory.getLogger(HttpRest4J2eeUtils.class);
    public static HttpRest4J2eeUtils getInstance() {
        return _instance;
    }

    public <T> T getForObject(String url, HttpMethod method, Class<T> responseType, Object... requestParams) throws ClaimpptException {
        try {
            logger.info("url{}:"+url);
            HttpHeaders httpHeaders = new HttpHeaders();
            //httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            httpHeaders.setContentType(new MediaType("application", "json", Charset.forName("utf-8")));
            httpHeaders.setAccept(Arrays.asList(new MediaType[]{new MediaType("application", "json", Charset.forName("utf-8"))}));
            HttpEntity httpEntity = new HttpEntity(null, httpHeaders);
            logger.info("httpEntity{}:"+ JSON.toJSONString(httpEntity));
            RestTemplate restTemplate = new RestTemplate();
            T t = restTemplate.exchange(url, method, httpEntity, responseType, requestParams).getBody();
            // T t = exchange(url, method, null, responseType, requestParams);
            return t;
        } catch (RestClientException e) {
            e.printStackTrace();
            throw new ClaimpptException(e.getMessage()+" j2ee调用失败");
        }
    }

    public <T> T postForObject(String url, HttpMethod method, Object obj, Class<T> responseType, Object... requestParams) throws ClaimpptException {
        try {
            logger.info("url{}:"+url);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(new MediaType("application", "json", Charset.forName("utf-8")));
            httpHeaders.setAccept(Arrays.asList(new MediaType[]{new MediaType("application", "json", Charset.forName("utf-8"))}));
            HttpEntity httpEntity = new HttpEntity(obj, httpHeaders);

            RestTemplate restTemplate = new RestTemplate();
            T t = restTemplate.exchange(url, method, httpEntity, responseType, requestParams).getBody();
            //T t = exchange(url, method, obj, responseType, requestParams);
            return t;
        } catch (RestClientException e) {
            e.printStackTrace();
            throw new ClaimpptException(e.getMessage()+" j2ee调用失败");
        }
    }

    private <T> T exchange(String url, HttpMethod method, Object obj, Class<T> responseType, Object... requestParams) {
        logger.info("url{}:"+url);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", Charset.forName("utf-8")));
        httpHeaders.setAccept(Arrays.asList(new MediaType[]{new MediaType("application", "json", Charset.forName("utf-8"))}));
        HttpEntity httpEntity = new HttpEntity(obj, httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        T t = restTemplate.exchange(url, method, httpEntity, responseType, requestParams).getBody();
        return t;
    }
}
