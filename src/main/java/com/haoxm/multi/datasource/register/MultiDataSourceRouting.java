package com.haoxm.multi.datasource.register;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @Author: haoxm
 * @Description:
 * @Date: 2019/8/10 16:09
 * @Modified By:
 */
public class MultiDataSourceRouting extends AbstractRoutingDataSource {
    /**
     * Determine the current lookup key. This will typically be
     * implemented to check a thread-bound transaction context.
     * <p>Allows for arbitrary keys. The returned key needs
     * to match the stored lookup key type, as resolved by the
     * {@link #resolveSpecifiedLookupKey} method.
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return MultiDataSourceHolder.getCurrentDatasource();
    }
}
