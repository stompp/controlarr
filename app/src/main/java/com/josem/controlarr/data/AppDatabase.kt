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

@Database(entities = [Server::class, Host::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun serverDao(): ServerDao
    abstract fun hostDao(): HostDao
}
