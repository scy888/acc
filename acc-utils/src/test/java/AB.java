import common.ReflectUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.service.api.result
 * @date: 2021-05-26 10:09:17
 * @describe:
 */
@Slf4j
public class AB {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class A {
        private String name;
        private Integer age;
        private String address;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class B {
        private String name;
        private String age;
        private String address;
    }

    @Test
    public void test() {
        System.out.println(ReflectUtil.getBeanUtils(new ArrayList<A>(), B.class));
    }
}
