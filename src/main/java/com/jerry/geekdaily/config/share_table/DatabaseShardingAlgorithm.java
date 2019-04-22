package com.jerry.geekdaily.config.share_table;

import com.dangdang.ddframe.rdb.sharding.api.ShardingValue;
import com.dangdang.ddframe.rdb.sharding.api.strategy.database.SingleKeyDatabaseShardingAlgorithm;
import com.google.common.collect.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashSet;

@Component
public class DatabaseShardingAlgorithm implements SingleKeyDatabaseShardingAlgorithm<Long> {

    @Override
    public String doEqualSharding(Collection<String> availableTargetNames, ShardingValue<Long> shardingValue) {
//        Long value = shardingValue.getValue();
//        if (value <= 20L) {
//            return database0Config.getDatabaseName();
//        } else {
//            return database1Config.getDatabaseName();
//        }
        return "geekdailky";
    }

    @Override
    public Collection<String> doInSharding(Collection<String> availableTargetNames, ShardingValue<Long> shardingValue) {
        Collection<String> result = new LinkedHashSet<>(availableTargetNames.size());
        for (Long value : shardingValue.getValues()) {
//            if (value <= 20L) {
//                result.add(database0Config.getDatabaseName());
//            } else {
//                result.add(database1Config.getDatabaseName());
//            }
            result.add("geekdailky");
        }
        return result;
    }

    @Override
    public Collection<String> doBetweenSharding(Collection<String> availableTargetNames,
                                                ShardingValue<Long> shardingValue) {
        Collection<String> result = new LinkedHashSet<>(availableTargetNames.size());
        Range<Long> range = shardingValue.getValueRange();
        for (Long value = range.lowerEndpoint(); value <= range.upperEndpoint(); value++) {
//            if (value <= 20L) {
//                result.add(database0Config.getDatabaseName());
//            } else {
//                result.add(database1Config.getDatabaseName());
//            }
            result.add("geekdailky");
        }
        return result;
    }
}
