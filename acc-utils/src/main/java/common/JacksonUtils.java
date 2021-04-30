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
import java.util.Date;
import java.util.List;

/**
 * @author: scyang
 * @program: tensquare_parent
 * @package: common
 * @date: 2020-03-19 21:43:10
 * @describe:
 */
public class JacksonUtils {
    private static final JacksonUtils _instance = new JacksonUtils();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public static JacksonUtils getInstance() {
        return _instance;
    }

    public String writeValueAsString(Object value) throws Exception {
        return this.objectMapper.writeValueAsString(value);
    }

    public <T> T readValue(String content, Class<T> valueType) {
        return this.readValue(content, valueType);
    }

    public <T> T convertValue(Object fromValue, Class<T> toValueType) {
        return this.objectMapper.convertValue(fromValue, toValueType);
    }

    /********************************************************************************************/

    private static final ObjectMapper object_mapper = new ObjectMapper();

    static {
        //忽略為null的字段
        object_mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        // 忽略匹配不上的字段
        object_mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 允许出现特殊字符和转义符
        object_mapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        // 允许出现单引号
        object_mapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
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
        object_mapper.registerModule(javaTimeModule);
    }


    public static String toString(Object object, Boolean... booleans) {
        /**
         * @Description: 将对象转成json字符串
         * @methodName: toString
         * @Param: [object]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2020/7/6 23:03
         */
        String value = null;
        try {
            if (booleans.length > 0 && booleans[0]) {
                value = object_mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            } else {
                value = object_mapper.writeValueAsString(object);
            }
        } catch (JsonProcessingException e) {
            value = e.getMessage();
            throw new RuntimeException("对象转换json字符串异常....");
        }
        return value;
    }

    public static <T> T toObject(Object object, Class<T> clazz) {
        /**
         * @Description: 将对象转换成对象
         * @methodName: toObject
         * @Param: [object, clazz]
         * @return: T
         * @Author: scyang
         * @Date: 2020/7/6 23:08
         */
        T t = null;
        try {
            t = object_mapper.convertValue(object, clazz);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return t;
    }

    public static <T> T toObject(String jsonStr, Class<T> clazz) {
        /**
         * @Description: 将json字符串装换成对象
         * @methodName: toObject
         * @Param: [jsonStr, clazz]
         * @return: T
         * @Author: scyang
         * @Date: 2020/7/6 23:12
         */
        T t = null;
        try {
            t = object_mapper.readValue(jsonStr, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return t;
    }

    public static <T, V> List<V> toList(List<T> paramList, TypeReference<List<V>> typeReference) {
        /**
         * @Description: 将集合转换成集合
         * @methodName: toList
         * @Param: [paramList, typeReference]
         * @return: java.util.List<V>
         * @Author: scyang
         * @Date: 2020/7/6 23:16
         */
        List<V> vList = null;
        try {
            vList = object_mapper.convertValue(paramList, typeReference);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return vList;
    }

    public static <T, V> List<V> toList(String jsonStr, TypeReference<List<V>> typeReference) {
        /**
         * @Description: 将json字符串装换成集合
         * @methodName: toList
         * @Param: [jsonStr, reference]
         * @return: java.util.List<V>
         * @Author: scyang
         * @Date: 2020/7/6 23:22
         */
        List<V> vList = null;
        try {
            vList = object_mapper.readValue(jsonStr, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vList;
    }

    public static String toJsonNode(String str, String nodeName) {
        try {
            JsonNode jsonNode = object_mapper.readTree(str).get(nodeName);
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
}
