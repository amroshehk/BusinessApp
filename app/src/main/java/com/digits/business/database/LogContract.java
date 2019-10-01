package com.digits.business.database;

import android.provider.BaseColumns;

public final class LogContract {


    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private LogContract() {
    }

    /**
     * Inner class that defines constant values for the Callers database table.
     * Each entry in the table represents a single log entry.
     */
    public static final class LogEntry implements BaseColumns {

        /**
         * Name of database table for Callers
         */
        public final static String TABLE_NAME = "log";

        /**
         * Unique ID number for the log (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the Caller.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_CALLER_NAME = "name";


        /**
         * Email of the account holder.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_EMAIL = "email";

        /**
         * Number of the Caller.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_CALLER_NUMBER = "phone_no";


        /**
         * Timestamp of the log.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_CALL_TIMESTAMP = "timestamp";


        /**
         * Duration of the call in millis.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_CALL_DURATION = "duration";


        /**
         * Type of the Caller.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_CALLER_TYPE = "type";

    }

}
