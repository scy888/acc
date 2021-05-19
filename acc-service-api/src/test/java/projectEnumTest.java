import com.weshare.service.api.enums.ProjectEnum;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: scyang
 * @program: acc
 * @package: PACKAGE_NAME
 * @date: 2021-05-19 14:46:37
 * @describe:
 */
public class projectEnumTest {

    @Test
    public void test(){
        System.out.println(ProjectEnum.getProductName("WS121212", "0101"));
        System.out.println(ProjectEnum.getProductName("WS121212", "0102"));
        System.out.println(ProjectEnum.getProductName("WS121212", "0101_"));
        System.out.println(ProjectEnum.getProductName("WS121212_", "0101"));
        System.out.println(ProjectEnum.getProductName(null, "0102"));
        System.out.println(ProjectEnum.getProductName("WS121212", null));
    }

    @Test
    public void test01(){
        System.out.println(ProjectEnum.getProductName_("WS121212", "0101"));
        System.out.println(ProjectEnum.getProductName_("WS121212", "0102"));
        System.out.println(ProjectEnum.getProductName_("WS121212", "0101_"));
        System.out.println(ProjectEnum.getProductName_("WS121212_", "0101"));
        System.out.println(ProjectEnum.getProductName_(null, "0102"));
        System.out.println(ProjectEnum.getProductName_("WS121212", null));
    }
    @Test
    public void test02(){
        List<List<String>> lists = List.of(List.of("1"),List.of("1","2"),List.of("1","2","3"));
        Integer integer = lists.stream().map(List::size).max(Integer::compareTo).orElse(0);
        System.out.println(integer);
        List<Integer> list = lists.stream().map(e -> e.stream().map(Integer::parseInt).mapToInt(a ->a).sum()).collect(Collectors.toList());
        System.out.println(list);
        long sum = list.stream().collect(Collectors.summarizingInt(e -> e)).getSum();
        System.out.println(sum);

        List<Integer> integers = List.of(1, 2, 3);
        String s = integers.stream().map(String::valueOf).collect(Collectors.joining(","));
        System.out.println(s);
        System.out.println(Arrays.stream(s.split(",")).count());
    }
}
