/*
 * */
package com.synectiks.process.server.system.stats.elasticsearch;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class IndicesStats {
    @JsonProperty
    public abstract int indexCount();

    @JsonProperty
    public abstract long storeSize();

    @JsonProperty
    public abstract long fieldDataSize();

    @JsonProperty
    public abstract long idCacheSize();

    @Deprecated
    public static IndicesStats create(int indexCount,
                                      long storeSize,
                                      long fieldDataSize,
                                      long idCacheSize) {
        // "id_cache" has been removed from stats in Elasticsearch 2.x
        // https://www.elastic.co/guide/en/elasticsearch/reference/2.0/breaking_20_stats_info_and_literal_cat_literal_changes.html#_removed_literal_id_cache_literal_from_stats_apis
        return create(indexCount, storeSize, fieldDataSize);
    }

    public static IndicesStats create(int indexCount,
                                      long storeSize,
                                      long fieldDataSize) {
        return new AutoValue_IndicesStats(indexCount, storeSize, fieldDataSize, 0L);
    }
}