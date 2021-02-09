/*
 * */
package com.synectiks.process.common.plugins.netflow.v9;

import org.junit.Test;

import com.synectiks.process.common.plugins.netflow.v9.NetFlowV9Header;

import static org.assertj.core.api.Assertions.assertThat;

public class NetFlowV9HeaderTest {
    @Test
    public void prettyHexDump() {
        final NetFlowV9Header header = NetFlowV9Header.create(5, 23, 42L, 1000L, 1L, 1L);
        assertThat(header.prettyHexDump()).isNotEmpty();
    }
}