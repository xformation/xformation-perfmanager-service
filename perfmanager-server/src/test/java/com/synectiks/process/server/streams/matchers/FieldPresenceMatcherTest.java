/*
 * */
package com.synectiks.process.server.streams.matchers;

import org.junit.Test;

import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.streams.StreamRule;
import com.synectiks.process.server.plugin.streams.StreamRuleType;
import com.synectiks.process.server.streams.matchers.StreamRuleMatcher;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Dennis Oelkers <dennis@torch.sh>
 */
public class FieldPresenceMatcherTest extends MatcherTest {
    @Test
    public void testBasicMatch() throws Exception {
        StreamRule rule = getSampleRule();
        rule.setField("message");
        rule.setType(StreamRuleType.PRESENCE);
        rule.setInverted(false);

        Message message = getSampleMessage();

        StreamRuleMatcher matcher = getMatcher(rule);

        Boolean result = matcher.match(message, rule);
        assertTrue(result);
    }

    @Test
    public void testBasicNonMatch() throws Exception {
        StreamRule rule = getSampleRule();
        rule.setField("nonexistentField");
        rule.setType(StreamRuleType.PRESENCE);
        rule.setInverted(false);

        Message message = getSampleMessage();

        StreamRuleMatcher matcher = getMatcher(rule);

        Boolean result = matcher.match(message, rule);
        assertFalse(result);
    }

    @Test
    public void testInvertedBasicMatch() throws Exception {
        StreamRule rule = getSampleRule();
        rule.setField("message");
        rule.setType(StreamRuleType.PRESENCE);
        rule.setInverted(true);

        Message message = getSampleMessage();

        StreamRuleMatcher matcher = getMatcher(rule);

        Boolean result = matcher.match(message, rule);
        assertFalse(result);
    }

    @Test
    public void testInvertedBasicNonMatch() throws Exception {
        StreamRule rule = getSampleRule();
        rule.setField("nonexistentField");
        rule.setType(StreamRuleType.PRESENCE);
        rule.setInverted(true);

        Message message = getSampleMessage();

        StreamRuleMatcher matcher = getMatcher(rule);

        Boolean result = matcher.match(message, rule);
        assertTrue(result);
    }

    @Test
    public void testNulledFieldNonMatch() throws Exception {
        String fieldName = "sampleField";
        StreamRule rule = getSampleRule();
        rule.setField(fieldName);
        rule.setType(StreamRuleType.PRESENCE);
        rule.setInverted(false);

        Message message = getSampleMessage();
        message.addField(fieldName, null);

        StreamRuleMatcher matcher = getMatcher(rule);

        Boolean result = matcher.match(message, rule);
        assertFalse(result);
    }

    @Test
    public void testInvertedNulledFieldMatch() throws Exception {
        String fieldName = "sampleField";
        StreamRule rule = getSampleRule();
        rule.setField(fieldName);
        rule.setType(StreamRuleType.PRESENCE);
        rule.setInverted(true);

        Message message = getSampleMessage();
        message.addField(fieldName, null);

        StreamRuleMatcher matcher = getMatcher(rule);

        Boolean result = matcher.match(message, rule);
        assertTrue(result);
    }

    @Test
    public void testEmptyStringFieldNonMatch() throws Exception {
        String fieldName = "sampleField";
        StreamRule rule = getSampleRule();
        rule.setField(fieldName);
        rule.setType(StreamRuleType.PRESENCE);
        rule.setInverted(false);

        Message message = getSampleMessage();
        message.addField(fieldName, "");

        StreamRuleMatcher matcher = getMatcher(rule);

        Boolean result = matcher.match(message, rule);
        assertFalse(result);
    }

    @Test
    public void testInvertedEmptyStringFieldMatch() throws Exception {
        String fieldName = "sampleField";
        StreamRule rule = getSampleRule();
        rule.setField(fieldName);
        rule.setType(StreamRuleType.PRESENCE);
        rule.setInverted(true);

        Message message = getSampleMessage();
        message.addField(fieldName, "");

        StreamRuleMatcher matcher = getMatcher(rule);

        Boolean result = matcher.match(message, rule);
        assertTrue(result);
    }

    @Test
    public void testRandomNumberFieldNonMatch() throws Exception {
        String fieldName = "sampleField";
        Integer randomNumber = 4;
        StreamRule rule = getSampleRule();
        rule.setField(fieldName);
        rule.setType(StreamRuleType.PRESENCE);
        rule.setInverted(false);

        Message message = getSampleMessage();
        message.addField(fieldName, randomNumber);

        StreamRuleMatcher matcher = getMatcher(rule);

        Boolean result = matcher.match(message, rule);
        assertTrue(result);
    }

    @Test
    public void testInvertedRandomNumberFieldMatch() throws Exception {
        String fieldName = "sampleField";
        Integer randomNumber = 4;
        StreamRule rule = getSampleRule();
        rule.setField(fieldName);
        rule.setType(StreamRuleType.PRESENCE);
        rule.setInverted(true);

        Message message = getSampleMessage();
        message.addField(fieldName, randomNumber);

        StreamRuleMatcher matcher = getMatcher(rule);

        Boolean result = matcher.match(message, rule);
        assertFalse(result);
    }
}
