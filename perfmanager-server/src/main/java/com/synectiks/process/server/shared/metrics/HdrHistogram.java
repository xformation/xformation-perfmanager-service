/*
 * */
package com.synectiks.process.server.shared.metrics;

import com.codahale.metrics.ExponentiallyDecayingReservoir;
import com.codahale.metrics.Snapshot;
import org.HdrHistogram.AtomicHistogram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class HdrHistogram extends com.codahale.metrics.Histogram {
    private static final Logger log = LoggerFactory.getLogger(HdrHistogram.class);
    private final AtomicHistogram hdrHistogram;

    public HdrHistogram(AtomicHistogram hdrHistogram) {
        super(new ExponentiallyDecayingReservoir());
        this.hdrHistogram = hdrHistogram;
    }

    public HdrHistogram(final long highestTrackableValue, final int numberOfSignificantValueDigits) {
        this(new AtomicHistogram(highestTrackableValue, numberOfSignificantValueDigits));
    }

    @Override
    public long getCount() {
        return hdrHistogram.getTotalCount();
    }

    @Override
    public Snapshot getSnapshot() {
        final AtomicHistogram copy = hdrHistogram.copy();
        return new Snapshot() {
            @Override
            public double getValue(double quantile) {
                return copy.getValueAtPercentile(quantile * 100);
            }

            @Override
            public long[] getValues() {
                return new long[0];
            }

            @Override
            public int size() {
                return 0;
            }

            @Override
            public long getMax() {
                return copy.getMaxValue();
            }

            @Override
            public double getMean() {
                final double mean = copy.getMean();
                return Double.isNaN(mean) ? 0 : mean;
            }

            @Override
            public long getMin() {
                final long minValue = copy.getMinValue();
                return minValue == Long.MAX_VALUE ? 0 : minValue;
            }

            @Override
            public double getStdDev() {
                final double stdDeviation = copy.getStdDeviation();
                return Double.isNaN(stdDeviation) ? 0 : stdDeviation;
            }

            @Override
            public void dump(OutputStream output) {
                final PrintStream printStream;
                try {
                    printStream = new PrintStream(output, false, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                copy.outputPercentileDistribution(printStream, 1d);
            }
        };
    }


    @Override
    public void update(int value) {
        update((long)value);
    }

    @Override
    public void update(long value) {
        try {
            hdrHistogram.recordValue(value);
        } catch (ArrayIndexOutOfBoundsException e) {
            log.debug("Ignoring value {} for HdrHistogram, it exceeds the highest trackable value {}", value, hdrHistogram.getHighestTrackableValue());
        }
    }
}
