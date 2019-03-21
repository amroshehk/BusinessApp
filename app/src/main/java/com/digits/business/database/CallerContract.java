package com.digits.business.database;

import android.provider.BaseColumns;

public final class CallerContract {


    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private CallerContract() {
    }

    /**
     * Inner class that defines constant values for the Callers database table.
     * Each entry in the table represents a single Callers.
     */
    public static final class CallerEntry implements BaseColumns {

        /**
         * Name of database table for Callers
         */
        public final static String TABLE_NAME = "caller";

        /**
         * Unique ID number for the Caller (only for use in the database table).
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
         * Number of the Caller.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_CALLER_NUMBER = "number";


        /**
         * Timestamp of the Caller.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_CALLER_TIMESTAMP = "timestamp";


        /**
         * Type of the Caller.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_CALLER_TYPE = "type";

    }

}
