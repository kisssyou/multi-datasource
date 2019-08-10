package com.haoxm.multi.datasource.register;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: haoxm
 * @Description: 保存当前线程的数据源ID
 * @Date: 2019/8/10 11:12
 * @Modified By:
 */
public final class MultiDataSourceHolder {
    private static final Logger logger = LoggerFactory.getLogger(MultiDataSourceHolder.class);

    /**
     * 当前线程数据源
     */
    private static ThreadLocal<String> currentDatasourceHolder = new ThreadLocal<String>();
    /**
     * 所有数据源
     */
    static Set<String> dataSources = new HashSet<String>(8);
    /**
     * 默认数据源
     */
    static String defaultDataSource;

    /**
     * 判断数据源是否存在
     * @param dataSource
     * @return
     */
    public static boolean containDataSource(String dataSource){
        return dataSources.contains(dataSource);
    }

    /**
     * 获取默认数据源
     * @return
     */
    public static String getDefaultDataSource(){
        return defaultDataSource;
    }

    /**
     * 保存数据源
     * @param dataSource
     */
    public static void addDataSource(String dataSource){
        dataSources.add(dataSource);
    }

    /**
     * 设置当前线程数据源
     * @param currentDatasource
     */
    public static void setCurrentDatasource(String currentDatasource){
        currentDatasourceHolder.set(currentDatasource);
    }

    public static Set<String> getDataSources() {
        return dataSources;
    }

    /**
     * 获取当前线程数据源
     * @return
     */
    public static String getCurrentDatasource(){
        return currentDatasourceHolder.get();
    }

    /**
     * 清除当前线程数据源
     */
    public static void removeCurrentDatasource(){
        currentDatasourceHolder.remove();
    }
}


