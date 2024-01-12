package com.vikas.kmm.data

import com.vikas.kmm.DatabaseDriverFactory
import com.vikas.kmm.db.LogDb
import com.vikas.kmm.db.Hello

class Database(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = LogDb(databaseDriverFactory.createDriver())
    private val dbQuery = database.logDbQueries


    fun insert(it: String) {
        dbQuery.insertHello(title = it)
    }

    fun getData(): List<Hello> {
        return dbQuery.selectAll().executeAsList()
    }
}

