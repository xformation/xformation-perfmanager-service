package com.synectiks.process.server.syslog4j.impl.unix;

import com.synectiks.process.server.syslog4j.SyslogMessageProcessorIF;
import com.synectiks.process.server.syslog4j.SyslogRuntimeException;
import com.synectiks.process.server.syslog4j.impl.AbstractSyslog;
import com.synectiks.process.server.syslog4j.impl.AbstractSyslogWriter;
import com.synectiks.process.server.syslog4j.util.OSDetectUtility;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;

/**
 * UnixSyslog is an extension of AbstractSyslog that provides support for
 * Unix-based syslog clients.
 * <p/>
 * <p>This class requires the JNA (Java Native Access) library to directly
 * access the native C libraries utilized on Unix platforms.</p>
 * <p/>
 * <p>Syslog4j is licensed under the Lesser GNU Public License v2.1.  A copy
 * of the LGPL license is available in the META-INF folder in all
 * distributions of Syslog4j and in the base directory of the "doc" ZIP.</p>
 *
 * @author &lt;syslog4j@productivity.org&gt;
 * @version $Id: UnixSyslog.java,v 1.27 2010/10/25 04:21:19 cvs Exp $
 */
public class UnixSyslog extends AbstractSyslog {
    private static final long serialVersionUID = 4973353204252276740L;

    protected UnixSyslogConfig unixSyslogConfig = null;

    protected interface CLibrary extends Library {
        public void openlog(final Memory ident, int option, int facility);

        public void syslog(int priority, final String format, final String message);

        public void closelog();
    }

    protected static Memory identBuffer = null;

    protected static int currentFacility = -1;
    protected static boolean openlogCalled = false;

    protected static CLibrary libraryInstance = null;

    protected static synchronized void loadLibrary(UnixSyslogConfig config) throws SyslogRuntimeException {
        if (!OSDetectUtility.isUnix()) {
            throw new SyslogRuntimeException("UnixSyslog not supported on non-Unix platforms");
        }

        if (libraryInstance == null) {
            libraryInstance = (CLibrary) Native.loadLibrary(config.getLibrary(), CLibrary.class);
        }
    }

    public void initialize() throws SyslogRuntimeException {
        try {
            this.unixSyslogConfig = (UnixSyslogConfig) this.syslogConfig;

        } catch (ClassCastException cce) {
            throw new SyslogRuntimeException("config must be of type UnixSyslogConfig");
        }

        loadLibrary(this.unixSyslogConfig);
    }

    protected static void write(int level, String message, UnixSyslogConfig config) throws SyslogRuntimeException {
        synchronized (libraryInstance) {
            if (currentFacility != config.getFacility()) {
                if (openlogCalled) {
                    libraryInstance.closelog();
                    openlogCalled = false;
                }

                currentFacility = config.getFacility();
            }

            if (!openlogCalled) {
                String ident = config.getIdent();

                if (ident != null && "".equals(ident.trim())) {
                    ident = null;
                }

                if (ident != null) {
                    identBuffer = new Memory(128);
                    identBuffer.setString(0, ident, false);
                }

                libraryInstance.openlog(identBuffer, config.getOption(), currentFacility);
                openlogCalled = true;
            }

            int priority = currentFacility | level;

            libraryInstance.syslog(priority, "%s", message);
        }
    }

    protected void write(int level, byte[] message) throws SyslogRuntimeException {
        // NO-OP
    }

    public void log(SyslogMessageProcessorIF messageProcessor, int level, String message) {
        write(level, message, this.unixSyslogConfig);
    }

    public void flush() throws SyslogRuntimeException {
        synchronized (libraryInstance) {
            libraryInstance.closelog();
            openlogCalled = false;
        }
    }

    public void shutdown() throws SyslogRuntimeException {
        flush();
    }

    public AbstractSyslogWriter getWriter() {
        return null;
    }

    public void returnWriter(AbstractSyslogWriter syslogWriter) {
        //
    }
}
