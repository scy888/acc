package com.weshare.loan.controller;

import com.weshare.loan.dao.LoanDao;
import com.weshare.loan.entity.SysLog;
import com.weshare.loan.repo.SysLogRepayRepo;
import common.JsonUtil;
import common.SnowFlake;
import common.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author: scyang
 * @program: acc
 * @package: com.weshare.repay.controller
 * @date: 2021-07-04 19:11:26
 * @describe:
 */
@Component
@Aspect
@Slf4j
@EnableAspectJAutoProxy
public class SysLogLoanAspect {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SysLogRepayRepo sysLogRepayRepo;
    @Autowired
    private LoanDao loanDao;
    private static final String pointCut = "execution(* com.weshare.loan..*.*(..))";

    @Pointcut(pointCut)
    public void pointCut() {

    }

    //@Around("pointCut()")
    public Object saveSysLog(ProceedingJoinPoint jp) throws Throwable {
        SysLog sysLog = new SysLog();
        //获取目标对象字节码
        Class<?> clazz = jp.getTarget().getClass();
        //获取方法的签名
        MethodSignature signature = (MethodSignature) jp.getSignature();
        Method method = signature.getMethod();
        //获取方法名
        String methodName = method.getName();
        //获取参数类型字节码数组
        Class<?>[] parameterTypes = method.getParameterTypes();
        //参数列表
        Parameter[] parameters = method.getParameters();
        //参数值列表
        Object[] args = jp.getArgs();
        //返回值类型字节码
        Class<?> returnType = method.getReturnType();
        LocalDateTime star = LocalDateTime.now();
        LocalDateTime star2 = LocalDateTime.now().with(ChronoField.MILLI_OF_SECOND, 0);
        //执行目标方法
        Object object = jp.proceed();
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime end2 = LocalDateTime.now().withNano(0);
        //保存日志记录
        sysLog.setId(SnowFlake.getInstance().nextId() + "");
        sysLog.setIp(InetAddress.getLocalHost().getHostAddress());
        //sysLog.setUri(request.getRequestURI());
        sysLog.setStartTime(star2);
        sysLog.setEndTime(end2);
        sysLog.setLostTime(
                new BigDecimal(Duration.between(star, end).toMillis()).setScale(2, BigDecimal.ROUND_HALF_UP)
        );
        sysLog.setClassName(clazz.getName());
        sysLog.setMethodName(methodName);
        sysLog.setParamsType(Arrays.stream(parameters).map(e -> e.getType().getSimpleName()).collect(Collectors.joining(",")));
        sysLog.setParamsName(Arrays.stream(parameters).map(Parameter::getName).collect(Collectors.joining(",")));
        String paramsValue = Arrays.stream(args).map(Object::toString).collect(Collectors.joining(","));
        sysLog.setParamsValue(paramsValue.length() > 100 ? paramsValue.substring(0, 100) : paramsValue);
        if (object != null) {
            sysLog.setReturnClassName(returnType.getSimpleName());
            sysLog.setReturnValue(object.toString().length() > 100 ? object.toString().substring(0, 100) : object.toString());
        }
        String[] objects = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            String type = parameters[i].getType().getSimpleName();
            String name = parameters[i].getName();
            objects[i] = type + " " + name;
        }
        sysLog.setMethodDesc(Modifier.toString(method.getModifiers()) + " " + returnType.getSimpleName() + " " + methodName + "("
                + Arrays.stream(objects).collect(Collectors.joining(",")) + ")");
        System.out.println(JsonUtil.toJson(sysLog, true));
        try {
            jdbcTemplate.update(StringUtils.getInsertSql(sysLog));
        } catch (DataAccessException e) {
            e.printStackTrace();
            log.error("acc-loan服务端保存日志出险异常:",e);
        }
        //sysLogRepayRepo.save(sysLog);
        //loanDao.addSysLog(sysLog);
        return object;
    }
}
