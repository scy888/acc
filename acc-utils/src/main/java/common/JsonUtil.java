package common;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * JSON工具类(使用Jackson做为实现)
 *
 * @author qujiayuan
 */
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 匹配不上的字段忽略，宽松模式
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 允许出现特殊字符和转义符
        objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        // 允许出现单引号
        objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addSerializer(Date.class, new DateSerializer(false, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));

        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addDeserializer(Date.class, new com.fasterxml.jackson.databind.JsonDeserializer<Date>() {
            @Override
            public Date deserialize(com.fasterxml.jackson.core.JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                try {
                    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(p.getText());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        objectMapper.registerModule(javaTimeModule);
    }

    /**
     * 把对象转成json格式的字符串
     *
     * @param object      要序列化为json的对象
     * @param prettyPrint 是否格式化JSON串
     * @return 对象被序列化为json的字符串
     */
    public static String toJson(Object object, boolean prettyPrint) {
        String json = null;
        try {
            if (prettyPrint) {
                json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            } else {
                json = objectMapper.writeValueAsString(object);
            }
        } catch (JsonProcessingException e) {
            // do nothing
        }
        return json;
    }

    public static String toJson(Object object) {
        return toJson(object, false);
    }

    /**
     * 将json字符串转成对象， eg：{@code fromJson(jsonStr, User.class)}
     *
     * @param jsonString
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        T t = null;
        try {
            t = objectMapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            throw new RuntimeException("json转换成对象异常", e);
        }
        return t;
    }

    /**
     * 将json字符串转成带有泛型的List对象， eg: {@code fromJson(yourJsonStr, new TypeReference<List<String>>(){})}
     *
     * @param jsonString    json串
     * @param typeReference 传入一个带有泛型的TypeReference, new一个空实现就可以了
     * @param <T>           json转成的对象类型
     * @return 返回json字符串反列化的对象
     */
    public static <T> T fromJson(String jsonString, TypeReference<T> typeReference) {
        T t = null;
        try {
            t = objectMapper.readValue(jsonString, typeReference);
        } catch (IOException e) {
            throw new RuntimeException("将json字符串转成带有泛型的List对象", e);
        }
        return t;
    }

    /**
     * 获取json串对象节点的信息
     *
     * @param str
     * @param nodeName
     * @return
     */
    public static String toJsonNode(String str, String nodeName) {
        try {
            JsonNode jsonNode = objectMapper.readTree(str).get(nodeName);
            if (jsonNode == null) {
                return null;
            }
            if (jsonNode.isTextual()) {
                return jsonNode.asText();
            } else {
                return jsonNode.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 把Map对象里，含有子json串的字符串(String)，还原成Map对象
     *
     * @param map
     * @return
     */
    public static Map<String, Object> format(Map<String, Object> map) {
        map.forEach((k, v) -> {
            if (v != null && v.toString().startsWith("{\"") && v.toString().endsWith("}")) {
                map.put(k, format(fromJson(v.toString(), Map.class)));
            } else if (v != null && v.toString().startsWith("[{\"") && v.toString().contains("]")) {
                List<Map<String, Object>> list = fromJson(v.toString(), List.class);
                List<Map<String, Object>> newlist = new ArrayList<>();
                for (Map<String, Object> innerMap : list) {
                    newlist.add(format(innerMap));
                }
                map.put(k, newlist);
            } else if (v != null && v.toString().startsWith("{")) {
                map.put(k, format((Map<String, Object>) v));
            } else if (v != null && v.toString().startsWith("[")) {
                List<Map<String, Object>> list = (List<Map<String, Object>>) v;
                List<Map<String, Object>> newlist = new ArrayList<>();
                for (Map<String, Object> innerMap : list) {
                    newlist.add(format(innerMap));
                }
                map.put(k, newlist);
            }
        });
        return map;
    }
    /**
     * 把非标的JSON串（含有大量反斜杠等转义），转为标准JSON字符串，用法，参考：JsonUtilTest#revertToStandardJsonString等3个例子。
     *
     * @param nonStandardJsonString 含有转义符的非标JSON
     * @param prettyPrint           是否格式化
     * @return 标准JSON字符串
     */
    public static String revertToStandardJsonString(String nonStandardJsonString, boolean prettyPrint) {
        return toJson(format(fromJson(nonStandardJsonString, Map.class)), prettyPrint);
    }
}
