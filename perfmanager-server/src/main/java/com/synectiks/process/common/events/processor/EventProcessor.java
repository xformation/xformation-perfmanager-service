/*
 * */
package com.synectiks.process.common.events.processor;

import com.synectiks.process.common.events.event.Event;
import com.synectiks.process.common.events.event.EventFactory;
import com.synectiks.process.common.events.event.EventWithContext;
import com.synectiks.process.server.plugin.MessageSummary;

import java.util.List;
import java.util.function.Consumer;

/**
 * Interface to be implemented by event processors.
 */
public interface EventProcessor {
    interface Factory<TYPE extends EventProcessor> {
        TYPE create(EventDefinition eventDefinition);
    }

    /**
     * Creates events by using the given {@link EventFactory} and passing them to the given {@link EventConsumer}.
     *
     * @param eventFactory   the event factory to create new {@link com.synectiks.process.common.events.event.Event} instances
     * @param parameters     the event processor execution parameters
     * @param eventsConsumer the event consumer
     * @throws EventProcessorException             if the execution fails
     * @throws EventProcessorPreconditionException if any preconditions are not met
     */
    void createEvents(EventFactory eventFactory, EventProcessorParameters parameters, EventConsumer<List<EventWithContext>> eventsConsumer) throws EventProcessorException;

    /**
     * Gets all source messages for the given {@link Event} and passes them to the {@code messageConsumer}.
     *
     * @param event           the event to get all source messages for
     * @param messageConsumer the consumer that all source messages will be passed into
     * @param limit           the maximum number of messages to get
     */
    void sourceMessagesForEvent(Event event, Consumer<List<MessageSummary>> messageConsumer, long limit) throws EventProcessorException;
}
