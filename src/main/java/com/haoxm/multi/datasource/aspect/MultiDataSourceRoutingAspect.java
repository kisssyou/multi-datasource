package com.haoxm.multi.datasource.aspect;

import com.haoxm.multi.datasource.annotation.DataSource;
import com.haoxm.multi.datasource.register.MultiDataSourceHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Author: haoxm
 * @Description: 数据源选择切面
 * @Date: 2019/8/7 23:14
 * @Modified By:
 */
@Aspect
@Component
public class MultiDataSourceRoutingAspect {
    private static final Logger logger = LoggerFactory.getLogger(MultiDataSourceRoutingAspect.class);

    /**
     * 进入方法前设置数据源
     * @param joinPoint
     */
    @Before("@within(com.haoxm.multi.datasource.annotation.DataSource) || @annotation(com.haoxm.multi.datasource.annotation.DataSource)")
    public void setDynamicDatasource(JoinPoint joinPoint){
        DataSource ds = getDataSourceAnnotation(joinPoint);
        String routingKey = ds.routingKey();

        if (MultiDataSourceHolder.containDataSource(routingKey)){
            logger.debug("设置当前线程数据源: {}", routingKey);
        }else {
            logger.debug("数据源 {} 不存在，设置当前线程默认数据源: {}", routingKey, MultiDataSourceHolder.getDefaultDataSource());
            routingKey = MultiDataSourceHolder.getDefaultDataSource();
        }

        MultiDataSourceHolder.setCurrentDatasource(routingKey);
    }

    /**
     * 退出方法时清除数据源
     * @param joinPoint
     */
    @After("@within(com.haoxm.multi.datasource.annotation.DataSource)||@annotation(com.haoxm.multi.datasource.annotation.DataSource)")
    public void removeDynamicDatasource(JoinPoint joinPoint){
        DataSource ds = getDataSourceAnnotation(joinPoint);
        String routingKey = ds.routingKey();

        logger.debug("清除当前线程数据源: {}", MultiDataSourceHolder.getCurrentDatasource());
        MultiDataSourceHolder.removeCurrentDatasource();
    }

    /**
     * 获取数据源注解，先取方法，再取类，再取接口
     * @param joinPoint
     * @return
     */
    private DataSource getDataSourceAnnotation(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        DataSource ds = signature.getMethod().getAnnotation(DataSource.class);
        if (ds == null){
            ds = joinPoint.getTarget().getClass().getAnnotation(DataSource.class);
        }
        if (ds == null){
            Class<?>[] interfaces = joinPoint.getTarget().getClass().getInterfaces();
            for(Class<?> intf : interfaces){
                ds = intf.getAnnotation(DataSource.class);
                if(ds != null){
                    break;
                }
            }
        }
        return ds;
    }

}





