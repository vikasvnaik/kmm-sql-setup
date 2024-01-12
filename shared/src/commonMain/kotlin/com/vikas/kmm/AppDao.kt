package com.vikas.kmm

import com.vikas.kmm.data.Database
import com.vikas.kmm.db.Hello

class AppDao(databaseDriverFactory: DatabaseDriverFactory) {
    var database = Database(databaseDriverFactory)
    private val platform: Platform = getPlatform()

    fun insertData() {
        database.insert("hello")
        database.insert("hello1")
    }

    fun getData():List<Hello> {
        return database.getData()
    }
    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}