package com.synectiks.process.server.syslog4j.impl.message;

import com.synectiks.process.server.syslog4j.SyslogMessageIF;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * AbstractSyslogMessage provides support for turning POJO (Plain Ol'
 * Java Objects) into Syslog messages.
 * <p/>
 * <p>More information on the PCI DSS specification is available here:</p>
 * <p/>
 * <p>https://www.pcisecuritystandards.org/security_standards/pci_dss.shtml</p>
 * <p/>
 * <p>Syslog4j is licensed under the Lesser GNU Public License v2.1.  A copy
 * of the LGPL license is available in the META-INF folder in all
 * distributions of Syslog4j and in the base directory of the "doc" ZIP.</p>
 *
 * @author &lt;syslog4j@productivity.org&gt;
 * @version $Id: AbstractSyslogMessage.java,v 1.2 2009/04/17 02:37:04 cvs Exp $
 */
public abstract class AbstractSyslogMessage implements SyslogMessageIF {
    private static final long serialVersionUID = 414124277626756491L;

    public static final String UNDEFINED = "undefined";

    public static final String DEFAULT_DATE_FORMAT = "yyyy/MM/dd";
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    public static final char DEFAULT_DELIMITER = ' ';
    public static final String DEFAULT_REPLACE_DELIMITER = "_";

    protected char getDelimiter() {
        return DEFAULT_DELIMITER;
    }

    protected String getReplaceDelimiter() {
        return DEFAULT_REPLACE_DELIMITER;
    }

    protected String getDateFormat() {
        return DEFAULT_DATE_FORMAT;
    }

    protected String getTimeFormat() {
        return DEFAULT_TIME_FORMAT;
    }

    protected String generateDate() {
        String date = new SimpleDateFormat(getDateFormat(), Locale.ENGLISH).format(new Date());

        return date;
    }

    protected String generateTime() {
        String time = new SimpleDateFormat(getTimeFormat(), Locale.ENGLISH).format(new Date());

        return time;
    }

    protected String[] generateDateAndTime(Date date) {
        String[] dateAndTime = new String[2];

        dateAndTime[0] = new SimpleDateFormat(getDateFormat(), Locale.ENGLISH).format(date);
        dateAndTime[1] = new SimpleDateFormat(getTimeFormat(), Locale.ENGLISH).format(date);

        return dateAndTime;
    }

    protected String generateLocalHostName() {
        String localHostName = UNDEFINED;

        try {
            localHostName = InetAddress.getLocalHost().getHostName();

        } catch (UnknownHostException uhe) {
            //
        }

        return localHostName;
    }

    protected boolean nullOrEmpty(String value) {
        return (value == null || "".equals(value.trim()));
    }

    protected String replaceDelimiter(String fieldName, String fieldValue, char delimiter, String replaceDelimiter) {
        if (replaceDelimiter == null || replaceDelimiter.length() < 1 || fieldValue == null || fieldValue.length() < 1) {
            return fieldValue;
        }

        String newFieldValue = fieldValue.replaceAll("\\" + delimiter, replaceDelimiter);

        return newFieldValue;
    }

    public abstract String createMessage();
}
