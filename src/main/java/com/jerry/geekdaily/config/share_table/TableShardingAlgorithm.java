package com.jerry.geekdaily.config.share_table;

import com.dangdang.ddframe.rdb.sharding.api.ShardingValue;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.SingleKeyTableShardingAlgorithm;
import com.google.common.collect.Range;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;

/**
 * 这里使用的都是单键分片策略
 * 示例分表策略是：
 * GoodsType为奇数使用goods_1表
 * GoodsType为偶数使用goods_0表
 * @author yangyang
 * @date 2019/1/30
 */
@Component
public class TableShardingAlgorithm implements SingleKeyTableShardingAlgorithm<Date> {

    private String tablePrefix = "goods_";

    @Override
    public String doEqualSharding(final Collection<String> tableNames, final ShardingValue<Date> shardingValue) {
//        for (String each : tableNames) {
//            if (each.endsWith(shardingValue.getValue() % 2 + "")) {
//                return each;
//            }
//        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
        return tablePrefix +formatter.format(shardingValue.getValue());
//        throw new IllegalArgumentException();
    }

    @Override
    public Collection<String> doInSharding(final Collection<String> tableNames, final ShardingValue<Date> shardingValue) {
//        Collection<String> result = new LinkedHashSet<>(tableNames.size());
//        for (Long value : shardingValue.getValues()) {
//            for (String tableName : tableNames) {
//                if (tableName.endsWith(value % 2 + "")) {
//                    result.add(tableName);
//                }
//            }
//        }
//        return result;
        Collection<String> result = new LinkedHashSet<>(shardingValue.getValues().size());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
        for (Date value : shardingValue.getValues()) {
            result.add(tablePrefix + formatter.format(value));
        }
        return result;
    }

    @Override
    public Collection<String> doBetweenSharding(final Collection<String> tableNames,
                                                final ShardingValue<Date> shardingValue) {
//        Collection<String> result = new LinkedHashSet<>(tableNames.size());
//        Range<Long> range = shardingValue.getValueRange();
//        for (Long i = range.lowerEndpoint(); i <= range.upperEndpoint(); i++) {
//            for (String each : tableNames) {
//                if (each.endsWith(i % 2 + "")) {
//                    result.add(each);
//                }
//            }
//        }
//        return result;
        Collection<String> result = new LinkedHashSet<>();
        DateFormat sdf = new SimpleDateFormat("yyyyMM");
        Range<Date> ranges = shardingValue.getValueRange();
        Date startTime = ranges.lowerEndpoint();
        Date endTime = ranges.upperEndpoint();
        // range.lowerEndpoint() = 2018-08-01
        // range.upperEndpoint() = 2018-10-01
        // 此处应该返回  tablePrefix+201808 , tablePrefix+201809,tablePrefix+201810,
        Calendar cal = Calendar.getInstance();

        while (startTime.getTime()<=endTime.getTime()){
            result.add(tablePrefix + sdf.format(startTime));
            cal.setTime(startTime);//设置起时间
            cal.add(Calendar.MONTH,1);
            startTime = cal.getTime();
        }
        return result;
    }
}
