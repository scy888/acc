package common;
import org.junit.Test;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author: scyang
 * @program: ssm_super
 * @package: com.itheima.commom
 * @date: 2019-10-25 21:34:11
 * @describe: 集合工具类
 */
public class CollectionsUtils {
    /**
     * @Description: 判断单列集合是否为空
     * @methodName:
     * @Param:
     * @return:
     * @Author: scyang
     * @Date: 2019/10/26 14:24
     */
    public static boolean isListEmpty(Collection collection){
        return collection==null||collection.isEmpty();
    }
    @Test
    public void test01(){
        Collection collection=null;
        System.out.println(CollectionsUtils.isListEmpty(new ArrayList()));
        System.out.println(CollectionsUtils.isListEmpty(collection));
    }
    public static boolean isEmptyMap(Map map){
        /**
         * @Description: 判断双列集合是否为空
         * @methodName: isEmptyMap
         * @Param: [map]
         * @return: boolean
         * @Author: scyang
         * @Date: 2019/10/26 14:27
         */
        return map==null||map.size()==0;
    }
    @Test
    public void test02(){
        Map map=null;
        System.out.println(CollectionsUtils.isEmptyMap(new HashMap()));
        System.out.println(CollectionsUtils.isEmptyMap(null));
    }
    public static <T> List<T> createList(T ... tArry){
        /**
         * @Description: 创建一个集合
         * @methodName: createList
         * @Param: [tArry]
         * @return: java.util.List<T>
         * @Author: scyang
         * @Date: 2019/10/26 14:46
         */
        List<T> list=new ArrayList<>();
        for (T t :tArry) {
            list.add(t);
        }
        return list;
    }
    @Test
    public void test03(){
        System.out.println(CollectionsUtils.createList("aa", "bb", "cc","",null,12));
    }
    public static <T> List<T> differenceList(List<T> oneList,List<T> twoList){
        /**
         * @Description: 返回两个集合的差集
         * @methodName: differenceList
         * @Param: [oneList, twoList]
         * @return: java.util.List<T>
         * @Author: scyang
         * @Date: 2019/10/26 15:15
         */
        List<T> list=new ArrayList<>();
        if (oneList.size()<twoList.size()){
            List<T> tempList=oneList;
            oneList=twoList;
            twoList=tempList;
        }
        for (T t : oneList) {
            if (!twoList.contains(t)){
               list.add(t);
            }
        }
        return list;
    }
    @Test
    public void test04(){
        List<Integer> oneList=new ArrayList<>();
        Collections.addAll(oneList,1,9,8);
        List<Integer> twoList=new ArrayList<>();
        Collections.addAll(twoList,1,4,6);
        System.out.println(this.differenceList(oneList, twoList));
    }
    public static <T> boolean isDifferenceList(List<T> oneList,List<T> twoList){
        /**
         * @Description: 判断两个集合是否有差集
         * @methodName: isDifferenceList
         * @Param: [oneList, twoList]
         * @return: boolean
         * @Author: scyang
         * @Date: 2019/10/26 15:40
         */
        if (oneList.size()<twoList.size()){
            List<T> tempList=oneList;
            oneList=twoList;
            twoList=tempList;
        }
        boolean flag=false;
        for (T t : oneList) {
            if (!twoList.contains(t)){
                flag=true;
                break;
            }
        }
        return flag;
    }
    @Test
    public void test05(){
        List<Integer> oneList=new ArrayList<>();
        Collections.addAll(oneList,4,6);
        List<Integer> twoList=new ArrayList<>();
        Collections.addAll(twoList,1,2,6,8);
        System.out.println(isDifferenceList(oneList, twoList));
    }
    public static <T> boolean isSameList(List<T> oneList,List<T> twoList){
        /**
         * @Description: 判断两个集合是否有交集
         * @methodName: isSameList
         * @Param: [oneList, twoList]
         * @return: boolean
         * @Author: scyang
         * @Date: 2019/10/26 16:09
         */
        if (oneList.size()<twoList.size()){
            List<T> tempList=oneList;
            oneList=twoList;
            twoList=tempList;
        }
        for (int i = 0; i < oneList.size(); i++) {
            if (twoList.contains(oneList.get(i))){
                return true;
            }
        }
        return false;
    }
    @Test
    public void test06(){
        List<Integer> oneList=new ArrayList<>();
        Collections.addAll(oneList,4,2);
        List<Integer> twoList=new ArrayList<>();
        Collections.addAll(twoList,1,2,6,8);
        System.out.println(isSameList(oneList, twoList));
    }
    public static boolean isArryContainsStr(String[] arr,String str){
        /**
         * @Description: 字符串是否在数组内
         * @methodName: isArryContainsStr
         * @Param: [arr, str]
         * @return: boolean
         * @Author: scyang
         * @Date: 2019/10/26 16:21
         */
        for (String s : arr) {
            if (str.equals(s)){
                return true;
            }
        }
        return false;
    }
    @Test
    public void test(){
        System.out.println(isArryContainsStr(new String[]{"1", "2"}, "2"));
        System.out.println(isArryContainsStr(new String[]{"1", "2"}, "4"));
    }
    public static <T> boolean isListContainsObj(List<T> list,T t){
        /**
         * @Description: 判断集合是否包含某个元素
         * @methodName: isListContainsObj
         * @Param: [list, t]
         * @return: boolean
         * @Author: scyang
         * @Date: 2019/10/26 16:58
         */
        boolean flag=false;
      if (list.contains(t)){
          flag=true;
      }
      return flag;
    }
    @Test
    public void test07(){
        List<String> list=new ArrayList<>();
        list.add("s");
        list.add("c");
        System.out.println(isListContainsObj(list, "5"));
    }
    public static <T> List<T> addList(List<T>...listArr){
        /**
         * @Description: 多个集合汇总
         * @methodName: addList
         * @Param: [listArr]
         * @return: java.util.List<T>
         * @Author: scyang
         * @Date: 2019/10/26 17:08
         */
        List<T> returnList=new ArrayList<>();
        for (List<T> list : listArr) {
            if (!isListEmpty(list)) {
                for (T t : list) {
                    returnList.add(t);
                }
            }
        }
        return returnList;
    }
    @Test
    public void test08(){
        List oneList=new ArrayList();
        Collections.addAll(oneList,1,"s",2);
        List twoList=new ArrayList();
  Collections.addAll(twoList,"盛重阳");
        System.out.println(addList(oneList,twoList));
    }
    public static <T> Collection<T> noSameList(List<T> list){
        /**
         * @Description: 集合去重
         * @methodName: noSameList
         * @Param: [list]
         * @return: java.util.Collection<T>
         * @Author: scyang
         * @Date: 2019/10/26 17:22
         */
        if (isListEmpty(list)){
            throw new RuntimeException("集合不能为空....");
        }
       Set<T> hashSet=new HashSet();
        for (T t : list) {
            hashSet.add(t);
        }
        List<T> returnList=new ArrayList<>();
        returnList.addAll(hashSet);
       // return returnList;
        return hashSet;
    }
    @Test
    public void test09(){
        List list=new ArrayList();
        Collections.addAll(list,1,2,3,2,4,3);
        System.out.println(noSameList(list));
    }
    public static <T> String listToStr(List<T> list,String sign){
        /**
         * @Description: 将list转换成以sign隔开的字符串
         * @methodName: listToStr
         * @Param: [list, sign]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2019/10/26 17:23
         */
        StringBuffer sb=new StringBuffer();
         if (!isListEmpty(list)){
             Iterator<T> iterator = list.iterator();
             while (iterator.hasNext()){
               sb.append(iterator.next()).append(sign);
             }
             sb.deleteCharAt(sb.length()-1);
         }
         return sb.toString();
    }
    @Test
    public void test10(){
        List list=new ArrayList();
        Collections.addAll(list,"盛","重","阳");
        System.out.println(listToStr(list, "&"));
    }
    public static  List<String> strToList(String str,String sign){
        /**
         * @Description: 将字符串以sign切割返回list
         * @methodName: strToList
         * @Param: [str, sign]
         * @return: java.util.List<java.lang.String>
         * @Author: scyang
         * @Date: 2019/10/26 17:38
         */
        List list=new ArrayList<>();
        String[] strings = str.split(sign);
        List<String> asList = Arrays.asList(strings);
        return asList;
    }
    @Test
    public void test11(){
        System.out.println(strToList("盛&重阳", "&"));
    }
    public static Map<String,Object> listToMap(List<Object> list){
        /**
         * @Description: 将list转换成map
         * @methodName: listToMap
         * @Param: [list]
         * @return: java.util.Map<java.lang.String,java.lang.Object>
         * @Author: scyang
         * @Date: 2019/10/26 17:46
         */
        Map<String,Object> returnMap=new HashMap<>();
        for (Object o : list) {
            returnMap.put(String.valueOf(o ), o);
        }
        return returnMap;
    }
    @Test
    public void test12(){
        List list=new ArrayList();
        Collections.addAll(list,1,"盛",2);
        System.out.println(listToMap(list));
    }

    public static<T,V> Map<T,V> listToMap(List<V> paramList, String mapKey, Class<V> clazz) throws Exception {
        /**
         * @Description: 把对象的list转换成map,key为对象的属性值
         * @methodName: listToMap
         * @Param: [paramList, mapKey, clazz]
         * @return: java.util.Map<T,V>
         * @Author: scyang
         * @Date: 2019/11/4 20:24
         */
        Map<T,V> returnMap=new HashMap<>();
        PropertyDescriptor pd=new PropertyDescriptor(mapKey,clazz );
        Method method = pd.getReadMethod();

        for (V v : paramList) {
            Object obj = method.invoke(v);
            returnMap.put((T)obj,v );
        }
        return returnMap;
    }

}
