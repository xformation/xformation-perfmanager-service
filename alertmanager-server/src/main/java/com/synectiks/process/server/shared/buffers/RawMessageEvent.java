/*
 * */
package com.synectiks.process.server.shared.buffers;

import com.synectiks.process.server.uuid.UUID;
import com.google.common.base.MoreObjects;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.synectiks.process.server.plugin.journal.RawMessage;

import org.joda.time.DateTime;

import java.nio.ByteBuffer;

public class RawMessageEvent {

    // the rawmessage will get be nulled as soon as the encoded raw message is being generated
    private RawMessage rawMessage;

    // once these fields are set, do NOT rely on rawMessage still being non-null!
    private byte[] messageIdBytes;
    private byte[] encodedRawMessage;

    // We need access to the raw message timestamp after the raw message has been cleared
    private DateTime messageTimestamp;

    public static final EventFactory<RawMessageEvent> FACTORY = new EventFactory<RawMessageEvent>() {
        @Override
        public RawMessageEvent newInstance() {
            return new RawMessageEvent();
        }
    };
    public static final EventTranslatorOneArg<RawMessageEvent, RawMessage> TRANSLATOR = new EventTranslatorOneArg<RawMessageEvent, RawMessage>() {
        @Override
        public void translateTo(RawMessageEvent event, long sequence, RawMessage arg0) {
            event.setRawMessage(arg0);
        }
    };

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("raw", getRawMessage())
                .add("uuid", getMessageId())
                .add("encodedLength", getEncodedRawMessage().length)
                .toString();
    }

    public RawMessage getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(RawMessage rawMessage) {
        this.rawMessage = rawMessage;
    }

    public byte[] getEncodedRawMessage() {
        return encodedRawMessage;
    }

    public void setEncodedRawMessage(byte[] encodedRawMessage) {
        this.encodedRawMessage = encodedRawMessage;
    }

    public void setMessageIdBytes(byte[] messageIdBytes) {
        this.messageIdBytes = messageIdBytes;
    }

    public byte[] getMessageIdBytes() {
        return messageIdBytes;
    }

    public DateTime getMessageTimestamp() {
        return messageTimestamp;
    }

    public void setMessageTimestamp(DateTime messageTimestamp) {
        this.messageTimestamp = messageTimestamp;
    }

    // performance doesn't matter, it's only being called during tracing
    public UUID getMessageId() {
        final ByteBuffer wrap = ByteBuffer.wrap(messageIdBytes);
        return new UUID(wrap.asLongBuffer().get(0), wrap.asLongBuffer().get(1));
    }
}
