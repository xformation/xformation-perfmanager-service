/*
 * */
package com.synectiks.process.server.indexer;

import com.google.common.collect.ComparisonChain;
import com.synectiks.process.server.indexer.indexset.IndexSetConfig;
import com.synectiks.process.server.indexer.indices.TooManyAliasesException;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.synectiks.process.server.indexer.MongoIndexSet.RESTORED_ARCHIVE_SUFFIX;

/**
 * This class is being used in plugins for testing, DO NOT move it to the test/ directory without changing the plugins.
 */
public class TestIndexSet implements IndexSet {
    private static final String SEPARATOR = "_";
    private static final String DEFLECTOR_SUFFIX = "deflector";

    private final IndexSetConfig config;
    private final Pattern indexPattern;

    public TestIndexSet(IndexSetConfig config) {
        this.config = config;
        // Part of the pattern can be configured in IndexSetConfig. If set we use the indexMatchPattern from the config.
        if (isNullOrEmpty(config.indexMatchPattern())) {
            // This pattern requires that we check that each index prefix is unique and unambiguous to avoid false matches.
            this.indexPattern = Pattern.compile("^" + config.indexPrefix() + SEPARATOR + "\\d+(?:" + RESTORED_ARCHIVE_SUFFIX + ")?");
        } else {
            // This pattern requires that we check that each index prefix is unique and unambiguous to avoid false matches.
            this.indexPattern = Pattern.compile("^" + config.indexMatchPattern() + SEPARATOR + "\\d+(?:" + RESTORED_ARCHIVE_SUFFIX + ")?");
        }
    }

    @Override
    public String[] getManagedIndices() {
        return new String[0];
    }

    @Override
    public String getWriteIndexAlias() {
        return config.indexPrefix() + SEPARATOR + DEFLECTOR_SUFFIX;
    }

    @Override
    public String getIndexWildcard() {
        return config.indexPrefix() + SEPARATOR + "*";
    }

    @Override
    public String getNewestIndex() throws NoTargetIndexException {
        return null;
    }

    @Override
    public String getActiveWriteIndex() throws TooManyAliasesException {
        return null;
    }

    @Override
    public Map<String, Set<String>> getAllIndexAliases() {
        return null;
    }

    @Override
    public String getIndexPrefix() {
        return null;
    }

    @Override
    public boolean isUp() {
        return false;
    }

    @Override
    public boolean isWriteIndexAlias(String index) {
        return false;
    }

    @Override
    public boolean isManagedIndex(String index) {
        return !isNullOrEmpty(index) && !isWriteIndexAlias(index) && indexPattern.matcher(index).matches();
    }

    @Override
    public void setUp() {

    }

    @Override
    public void cycle() {
    }

    @Override
    public void cleanupAliases(Set<String> indices) {

    }

    @Override
    public void pointTo(String shouldBeTarget, String currentTarget) {

    }

    @Override
    public Optional<Integer> extractIndexNumber(String index) {
        return Optional.empty();
    }

    @Override
    public IndexSetConfig getConfig() {
        return config;
    }

    @Override
    public int compareTo(IndexSet o) {
        return ComparisonChain.start()
                .compare(this.getConfig(), o.getConfig())
                .result();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestIndexSet that = (TestIndexSet) o;
        return Objects.equals(config, that.config);
    }

    @Override
    public int hashCode() {
        return config.hashCode();
    }

    @Override
    public String toString() {
        return "MongoIndexSet{" + "config=" + config + '}';
    }
}
