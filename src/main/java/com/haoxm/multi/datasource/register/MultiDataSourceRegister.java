package com.haoxm.multi.datasource.register;

import com.haoxm.multi.datasource.exception.MultiDataSourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author: haoxm
 * @Description: 注册数据源
 * @Date: 2019/8/10 11:13
 * @Modified By:
 */
public class MultiDataSourceRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware{

    private static final Logger logger = LoggerFactory.getLogger(MultiDataSourceHolder.class);
    /**
     * 默认数据源类型，与 springboot 保持一致
     */
    private static final String DEFAULT_DATASOURCE_TYPE = "com.zaxxer.hikari.HikariDataSource";
    private Binder binder;

    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        BindResult<HashMap> datasourceBind = binder.bind("spring.datasource", Bindable.of(HashMap.class));
        BindResult<String> defaultDatasourceBind = binder.bind("spring.datasource.default", Bindable.of(String.class));
        if (!defaultDatasourceBind.isBound()){
            throw new MultiDataSourceException("默认数据源 spring.datasource.default 必须配置 ");
        }
        //默认数据源和目标数据源
        DataSource defaultTargetDataSource = null;
        HashMap<Object, Object> targetDataSources = new HashMap<Object, Object>(8);

        //循环绑定所有数据源
        Set<Map.Entry> set = datasourceBind.get().entrySet();
        for (Map.Entry entry : set){
            if (entry.getValue() instanceof Map){
                Map propertiesMap = (Map) entry.getValue();
                String type = (String) propertiesMap.get("type");
                if (type == null || type.trim().length() == 0){
                    logger.info("使用默认数据源类型: {}", DEFAULT_DATASOURCE_TYPE);
                    type = DEFAULT_DATASOURCE_TYPE;
                }
                Class<? extends DataSource> clazz = (Class<? extends DataSource>) ClassUtils.resolveClassName(type, null);
                ConfigurationPropertySource configurationPropertySource = new MapConfigurationPropertySource(propertiesMap);
                Binder mapBinder = new Binder(configurationPropertySource);
                BindResult<? extends DataSource> dsBindResult =  mapBinder.bind(ConfigurationPropertyName.EMPTY, Bindable.of(clazz));
                if (dsBindResult.isBound()){
                    String dsKey = (String) entry.getKey();
                    targetDataSources.put(dsKey, dsBindResult.get());
                    MultiDataSourceHolder.addDataSource(dsKey);
                    if (defaultDatasourceBind.get().equals(dsKey)){
                        defaultTargetDataSource = dsBindResult.get();
                        MultiDataSourceHolder.defaultDataSource = dsKey;
                    }
                }
            }
        }
        if (defaultTargetDataSource == null){
            throw new MultiDataSourceException("没有找到默认数据源: " + defaultDatasourceBind.get());
        }

        //注册datasource
        GenericBeanDefinition definition = new GenericBeanDefinition();
        definition.setBeanClass(MultiDataSourceRouting.class);
        MutablePropertyValues propertyValues = definition.getPropertyValues();
        propertyValues.add("defaultTargetDataSource", defaultTargetDataSource);
        propertyValues.add("targetDataSources", targetDataSources);
        registry.registerBeanDefinition("datasource", definition);

        logger.info("已注册数据源 {},默认数据源为 {}", targetDataSources.keySet(), defaultDatasourceBind.get());
    }

    public void setEnvironment(Environment environment) {
        this.binder = Binder.get(environment);
    }
}
