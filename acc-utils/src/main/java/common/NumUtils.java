package common;

import org.junit.Test;

import java.util.Random;
import java.util.UUID;

/**
 * @author: scyang
 * @program: tensquare_parent
 * @package: common
 * @date: 2020-02-13 20:59:57
 * @describe:
 */
public class NumUtils {
    public  int getNum(int size){
        int num=0;
        if (size==4){
            num=1000+new Random().nextInt(9000);
        }
        else if (size==6){
            num=100000+new Random().nextInt(900000);
        }
        else {
            throw new RuntimeException("请输入4或6!!!");
        }
        return num;
    }
    @Test
    public void test(){
        System.out.println(getNum(4));
        System.out.println(getNum(6));
        System.out.println(6+""+"6");
        System.out.println(UUID.nameUUIDFromBytes("aa".getBytes()));
        System.out.println(UUID.randomUUID());
        System.out.println(getNum(2));
    }
}
