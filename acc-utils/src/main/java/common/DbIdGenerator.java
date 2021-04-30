package common;//package common;
//
//import org.springframework.beans.factory.annotation.Configurable;
//
//import javax.imageio.spi.ServiceRegistry;
//import java.io.Serializable;
//import java.lang.reflect.Type;
//import java.util.Properties;
//
///**
// * db_id生产器
// *
// * @author: Chao
// * @date： 2020/7/24 14:47
// * @since: JDK1.8
// * @description:
// */
//public class DbIdGenerator implements IdentifierGenerator, Configurable {
//
//    private SnowFlake snowFlake;
//
//    @Override
//    public void configure(Type type, Properties properties, ServiceRegistry serviceRegistry) throws MappingException {
//        // when entity factory load entity bean do nothing
//    }
//
//    @Override
//    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
//        initGenerator();
//        return snowFlake.nextId();
//    }
//
//    /**
//     * 每个entity将会创建一个id生成器的实例，
//     * 针对同一个entity，{@link DbIdGenerator#snowFlake}只会赋值一次,
//     * 需保证线程安全
//     */
//    public void initGenerator() {
//        if (this.snowFlake == null) {
//            synchronized (this) {
//                if (this.snowFlake == null) {
//                    snowFlake = SpringContextUtil.getBean(SnowFlake.class);
//                }
//            }
//        }
//    }
//
//}
//
