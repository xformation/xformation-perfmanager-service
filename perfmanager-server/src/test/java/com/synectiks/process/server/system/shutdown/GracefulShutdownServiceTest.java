/*
 * */
package com.synectiks.process.server.system.shutdown;

import org.junit.Before;
import org.junit.Test;

import com.synectiks.process.server.system.shutdown.GracefulShutdownHook;
import com.synectiks.process.server.system.shutdown.GracefulShutdownService;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GracefulShutdownServiceTest {
    private GracefulShutdownService shutdownService;

    @Before
    public void setUp() throws Exception {
        this.shutdownService = new GracefulShutdownService();
        shutdownService.startAsync().awaitRunning();
    }

    private void stop(GracefulShutdownService service) throws Exception {
        service.stopAsync().awaitTerminated(1, TimeUnit.MINUTES);
    }

    @Test
    public void registerAndShutdown() throws Exception {
        final AtomicBoolean hook1Called = new AtomicBoolean(false);
        final AtomicBoolean hook2Called = new AtomicBoolean(false);

        shutdownService.register(() -> hook1Called.set(true));
        shutdownService.register(() -> hook2Called.set(true));

        assertThat(hook1Called).isFalse();
        assertThat(hook2Called).isFalse();

        stop(shutdownService);

        assertThat(hook1Called).isTrue();
        assertThat(hook2Called).isTrue();
    }

    @Test
    public void withExceptionOnShutdown() throws Exception {
        final AtomicBoolean hook1Called = new AtomicBoolean(false);
        final AtomicBoolean hook2Called = new AtomicBoolean(false);

        shutdownService.register(() -> hook1Called.set(true));
        shutdownService.register(() -> { throw new Exception("eek"); });

        stop(shutdownService);

        assertThat(hook1Called).isTrue();
        assertThat(hook2Called).isFalse();
    }

    @Test
    public void registerAndUnregister() throws Exception {
        final AtomicBoolean hook1Called = new AtomicBoolean(false);
        final AtomicBoolean hook2Called = new AtomicBoolean(false);

        final GracefulShutdownHook hook1 = () -> hook1Called.set(true);
        final GracefulShutdownHook hook2 = () -> hook2Called.set(true);

        shutdownService.register(hook1);
        shutdownService.register(hook2);

        assertThat(hook1Called).isFalse();
        assertThat(hook2Called).isFalse();

        shutdownService.unregister(hook1);

        stop(shutdownService);

        assertThat(hook1Called).isFalse();
        assertThat(hook2Called).isTrue();
    }

    @Test
    public void registerAndUnregisterNull() throws Exception {
        assertThatThrownBy(() -> shutdownService.register(null))
                .hasMessageContaining("shutdownHook")
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> shutdownService.unregister(null))
                .hasMessageContaining("shutdownHook")
                .isInstanceOf(NullPointerException.class);

        stop(shutdownService);
    }

    @Test
    public void registerMoreThanOnce() throws Exception {
        final AtomicInteger value = new AtomicInteger(0);

        final GracefulShutdownHook hook = value::incrementAndGet;

        shutdownService.register(hook);
        shutdownService.register(hook);
        shutdownService.register(hook);

        assertThat(value.get()).isEqualTo(0);

        stop(shutdownService);

        assertThat(value.get()).isEqualTo(1);
    }

    @Test
    public void failRegisterAndUnregisterDuringShutdown() throws Exception {
        final GracefulShutdownHook hook = () -> {};

        shutdownService.register(hook);

        shutdownService.stopAsync().awaitTerminated();

        assertThatThrownBy(() -> shutdownService.register(hook), "register during shutdown should not work")
                .hasMessageContaining("register")
                .isInstanceOf(IllegalStateException.class);

        assertThatThrownBy(() -> shutdownService.unregister(hook), "unregister during shutdown should not work")
                .hasMessageContaining("unregister")
                .isInstanceOf(IllegalStateException.class);
    }
}