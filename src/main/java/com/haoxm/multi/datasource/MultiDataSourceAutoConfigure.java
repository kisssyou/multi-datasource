package com.haoxm.multi.datasource;

import com.haoxm.multi.datasource.register.MultiDataSourceRegister;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @Author: haoxm
 * @Description: 多数据源自动装配
 * @Date: 2019/8/10 15:58
 * @Modified By:
 */
@Configuration
@Import(MultiDataSourceRegister.class)
@ConditionalOnClass(AbstractRoutingDataSource.class)
public class MultiDataSourceAutoConfigure {

}
