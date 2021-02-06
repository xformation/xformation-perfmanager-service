/*
 * */
package com.synectiks.process.server.audit;

import java.util.Map;

public class NullAuditEventSender implements AuditEventSender {
    @Override
    public void success(AuditActor actor, AuditEventType type) {
    }

    @Override
    public void success(AuditActor actor, AuditEventType type, Map<String, Object> context) {
    }

    @Override
    public void failure(AuditActor actor, AuditEventType type) {
    }

    @Override
    public void failure(AuditActor actor, AuditEventType type, Map<String, Object> context) {
    }
}
