package ge.mov.mobile.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migrations {

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE user_subscriptions ADD COLUMN saved_on TEXT")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS 'offline_movies' ('id' INTEGER NOT NULL, " +
                            "'title' TEXT, " +
                            "'image' BLOB, " +
                            "'language' TEXT, " +
                            "'src' TEXT, " +
                            "'savedAt' INTEGER, PRIMARY KEY(`id`))"
                )
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE offline_movies ADD COLUMN season INTEGER")
                database.execSQL("ALTER TABLE offline_movies ADD COLUMN episode INTEGER")
                database.execSQL("ALTER TABLE offline_movies ADD COLUMN expiresAt INTEGER")
                database.execSQL("ALTER TABLE offline_movies ADD COLUMN isExpiringSoon INTEGER")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE offline_movies")

                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS 'offline_movies' (" +
                            "'i' INTEGER," +
                            "'id' INTEGER NOT NULL, " +
                            "'title' TEXT, " +
                            "'image' BLOB, " +
                            "'language' TEXT, " +
                            "'src' TEXT, " +
                            "'savedAt' INTEGER, PRIMARY KEY(`i`))"
                )

                database.execSQL("ALTER TABLE offline_movies ADD COLUMN season INTEGER")
                database.execSQL("ALTER TABLE offline_movies ADD COLUMN episode INTEGER")
                database.execSQL("ALTER TABLE offline_movies ADD COLUMN expiresAt INTEGER")
                database.execSQL("ALTER TABLE offline_movies ADD COLUMN isExpiringSoon INTEGER")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS 'reminders'(" +
                            "'id' INTEGER," +
                            "'adjaraId' INTEGER, PRIMARY KEY('id'));"
                )
            }
        }

        val migrations = arrayOf(
            MIGRATION_1_2,
            MIGRATION_2_3,
            MIGRATION_3_4,
            MIGRATION_4_5,
            MIGRATION_5_6
        )
    }
}