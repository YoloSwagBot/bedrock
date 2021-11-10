@Database(entities = [FirstObject::class, SecondObject::class], version = 1, exportSchema = false)
@TypeConverters(
    XYZConverter::class,
)
abstract class MyAppDB : RoomDatabase() {

    abstract fun MyAppRequestDao(): MyAppRequestDao
    abstract fun MyAppPostDao(): FTPPostDao

    companion object : SingletonHolder<FTPDB, Context>({
            Room.databaseBuilder(
                it.applicationContext,
                FTPDB::class.java,
                DATABASE_NAME
            ).fallbackToDestructiveMigration().build()
        })

}

