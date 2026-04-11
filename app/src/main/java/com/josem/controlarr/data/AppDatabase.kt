package com.josem.controlarr.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Converters {
    @TypeConverter
    fun fromServerType(value: ServerType): String = value.name

    @TypeConverter
    fun toServerType(value: String): ServerType = ServerType.valueOf(value)

    @TypeConverter
    fun fromTokenType(value: TokenType): String = value.name

    @TypeConverter
    fun toTokenType(value: String): TokenType = TokenType.valueOf(value)
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `hosts` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`name` TEXT NOT NULL, " +
                "`address` TEXT NOT NULL)"
        )
        db.execSQL(
            "INSERT INTO `hosts` (`name`, `address`) " +
                "SELECT DISTINCT host, host FROM `servers`"
        )
        db.execSQL("ALTER TABLE `servers` ADD COLUMN `hostId` INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE `servers` ADD COLUMN `username` TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE `servers` ADD COLUMN `password` TEXT NOT NULL DEFAULT ''")
        db.execSQL(
            "UPDATE `servers` SET `hostId` = (" +
                "SELECT `id` FROM `hosts` WHERE `hosts`.`address` = `servers`.`host`)"
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_servers_hostId` ON `servers` (`hostId`)")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `servers` ADD COLUMN `sortOrder` INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `tokens` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`name` TEXT NOT NULL, " +
                "`type` TEXT NOT NULL, " +
                "`value` TEXT NOT NULL, " +
                "`notes` TEXT NOT NULL DEFAULT '', " +
                "`createdAt` INTEGER NOT NULL DEFAULT 0)"
        )
    }
}

@Database(entities = [Server::class, Host::class, Token::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun serverDao(): ServerDao
    abstract fun hostDao(): HostDao
    abstract fun tokenDao(): TokenDao
}
