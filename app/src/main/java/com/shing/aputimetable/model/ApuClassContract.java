package com.shing.aputimetable.model;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Shing on 31-Jul-17.
 */

public class ApuClassContract {

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.shing.aputimetable";
    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.pets/pets/ is a valid path for
     * looking at pet data. content://com.example.android.pets/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_APU_CLASS = "class";

    private ApuClassContract() {
    }

    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     */
    public static final class ApuClassEntry implements BaseColumns {

        /**
         * The content URI to access the class data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_APU_CLASS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_APU_CLASS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_APU_CLASS;

        /**
         * Name of database table for class
         */
        public final static String TABLE_NAME = "class";

        /**
         * Unique ID number for the class (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Subject of the class.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_CLASS_SUBJECT = "subject";

        /**
         * Date of the class.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_CLASS_DATE = "date";

        /**
         * Day of the class.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_CLASS_DAY = "day";

        /**
         * Time of the class.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_CLASS_TIME = "time";

        /**
         * Location of the class.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_CLASS_LOCATION = "location";

        /**
         * Room of the class.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_CLASS_ROOM = "room";

        /**
         * Lecturer of the class.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_CLASS_LECTURER = "lecturer";

        /**
         * Subject of the class.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_CLASS_TYPE = "type";

    }

}
