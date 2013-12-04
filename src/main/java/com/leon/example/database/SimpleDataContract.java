package com.leon.example.database;

import android.provider.BaseColumns;

/**
 * Created by hualu on 13-11-19.
 */
public final class SimpleDataContract {
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + SimpleEntry.TABLE_NAME + " (" +
                    SimpleEntry._ID + " INTEGER PRIMARY KEY," +
                    SimpleEntry.COLUMN_NAME_ENTRY_NAME + TEXT_TYPE + COMMA_SEP +
                    SimpleEntry.COLUMN_NAME_ENTRY_NUMBER + TEXT_TYPE +
                    " )";
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SimpleEntry.TABLE_NAME;

    public SimpleDataContract() {
    }

    /**
     * 表 simple_entry 的contract类
     */
    public static abstract class SimpleEntry implements BaseColumns {
        public static final String TABLE_NAME = "simple_entry";
        public static final String COLUMN_NAME_ENTRY_NAME = "entry_name";
        public static final String COLUMN_NAME_ENTRY_NUMBER = "entry_number";
    }
}
