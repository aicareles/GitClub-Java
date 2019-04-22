package com.jerry.geekdaily.config.share_table;

import com.alibaba.druid.pool.DruidDataSource;
import com.dangdang.ddframe.rdb.sharding.api.ShardingDataSourceFactory;
import com.dangdang.ddframe.rdb.sharding.api.rule.DataSourceRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.ShardingRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.TableRule;
import com.dangdang.ddframe.rdb.sharding.api.strategy.database.DatabaseShardingStrategy;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.TableShardingStrategy;
import com.dangdang.ddframe.rdb.sharding.keygen.DefaultKeyGenerator;
import com.dangdang.ddframe.rdb.sharding.keygen.KeyGenerator;
import com.mysql.jdbc.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    public static final String DEFALUT_DB_NAME = "geekdaily";

    @Autowired
    private DatabaseShardingAlgorithm databaseShardingAlgorithm;

    @Autowired
    private TableShardingAlgorithm tableShardingAlgorithm;

    @Bean
    public DataSource getDataSource() throws SQLException {
        return buildDataSource();
    }

    private DataSource buildDataSource() throws SQLException {
        //分库设置
        Map<String, DataSource> dataSourceMap = new HashMap<>(2);
//        //添加两个数据库database0和database1
        dataSourceMap.put(DEFALUT_DB_NAME, createDataSource(DEFALUT_DB_NAME));
//        dataSourceMap.put(database1Config.getDatabaseName(), database1Config.createDataSource());
        //设置默认数据库
        DataSourceRule dataSourceRule = new DataSourceRule(dataSourceMap, DEFALUT_DB_NAME);

        //分表设置，大致思想就是将查询虚拟表Goods根据一定规则映射到真实表中去
        TableRule orderTableRule = TableRule.builder("goods")
                .actualTables(Arrays.asList("goods_201901", "goods_201902", "goods_201903", "goods_201904", "goods_201905", "goods_201906",
                        "goods_201907", "goods_201908", "goods_201909", "goods_201910", "goods_201911", "goods_201912"))
                .tableShardingStrategy(new TableShardingStrategy("date", tableShardingAlgorithm))
                .dataSourceRule(dataSourceRule)
//                .dynamic(true)
                .build();

        //分库分表策略
        ShardingRule shardingRule = ShardingRule.builder()
                .dataSourceRule(dataSourceRule)
                .tableRules(Arrays.asList(orderTableRule))
//                .databaseShardingStrategy(new DatabaseShardingStrategy("goods_id", databaseShardingAlgorithm))
                .tableShardingStrategy(new TableShardingStrategy("date", tableShardingAlgorithm)).build();
        DataSource dataSource = ShardingDataSourceFactory.createDataSource(shardingRule);
        return dataSource;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new DefaultKeyGenerator();
    }

    private static DataSource createDataSource(String dataSourceName) {
        //使用druid连接数据库
        DruidDataSource result = new DruidDataSource();
//        result.setDriverClassName(Driver.class.getName());
        result.setDriverClassName("com.mysql.jdbc.Driver");
        result.setUrl(String.format("jdbc:mysql://localhost:3306/%s?characterEncoding=utf-8", dataSourceName));
        result.setUsername("root");
        result.setPassword("root");
        return result;
    }
}
