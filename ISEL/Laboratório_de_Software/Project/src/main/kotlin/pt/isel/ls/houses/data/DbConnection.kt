package pt.isel.ls.houses.data

import org.postgresql.ds.PGSimpleDataSource
import javax.sql.DataSource

//5432 - default port for Postgres
const val DB_URL = "jdbc:postgresql://localhost:5432/houses_db?user=postgres&password=postgres"
const val DB_URL_TEST = "jdbc:postgresql://localhost:5432/houses_db_test?user=postgres&password=postgres"

fun getDataSource(): DataSource {
    val ds = PGSimpleDataSource()

    val envUrl = System.getenv("JDBC_DATABASE_URL")//from Render

    if (envUrl != null) {
        ds.setURL(envUrl)
    } else {
        ds.setURL(DB_URL_TEST)
    }
    return ds
}