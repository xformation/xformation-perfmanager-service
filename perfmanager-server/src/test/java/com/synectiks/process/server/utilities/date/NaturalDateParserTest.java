/*
 * */
package com.synectiks.process.server.utilities.date;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.synectiks.process.server.plugin.utilities.date.NaturalDateParser;

import static org.assertj.jodatime.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class NaturalDateParserTest {
    private NaturalDateParser naturalDateParser;

    @Before
    public void setUp() {
        naturalDateParser = new NaturalDateParser();
    }

    @Test
    public void testParse() throws Exception {
        NaturalDateParser.Result today = naturalDateParser.parse("today");
        assertNotNull(today.getFrom());
        assertNotNull(today.getTo());

        // It's enough if this does not throw exceptions because we are not testing the underlying library.
        naturalDateParser.parse("today");
        naturalDateParser.parse("last week to today");
    }

    @Test(expected = NaturalDateParser.DateNotParsableException.class)
    public void testParseFailsOnUnparsableDate() throws Exception {
        naturalDateParser.parse("LOLWUT");
    }

    @Test(expected = NaturalDateParser.DateNotParsableException.class)
    public void testParseFailsOnEmptyDate() throws Exception {
        naturalDateParser.parse("");
    }

    @Test
    @Ignore
    public void testTemporalOrder() throws Exception {
        NaturalDateParser p = new NaturalDateParser();

        NaturalDateParser.Result result1 = p.parse("last hour");
        assertTrue(result1.getFrom().compareTo(result1.getTo()) < 0);

        NaturalDateParser.Result result2 = p.parse("last one hour");
        assertTrue(result2.getFrom().compareTo(result2.getTo()) < 0);
    }

    // https://github.com/Graylog2/graylog2-server/issues/1226
    @Test
    public void issue1226() throws Exception {
        NaturalDateParser.Result result99days = naturalDateParser.parse("last 99 days");
        assertThat(result99days.getFrom()).isEqualToIgnoringMillis(result99days.getTo().minusDays(99));

        NaturalDateParser.Result result100days = naturalDateParser.parse("last 100 days");
        assertThat(result100days.getFrom()).isEqualToIgnoringMillis(result100days.getTo().minusDays(100));

        NaturalDateParser.Result result101days = naturalDateParser.parse("last 101 days");
        assertThat(result101days.getFrom()).isEqualToIgnoringMillis(result101days.getTo().minusDays(101));
    }
}
