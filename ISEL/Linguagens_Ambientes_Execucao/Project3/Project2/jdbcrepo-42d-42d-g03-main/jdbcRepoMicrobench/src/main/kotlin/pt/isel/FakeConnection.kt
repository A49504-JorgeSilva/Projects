package pt.isel

import java.sql.Blob
import java.sql.CallableStatement
import java.sql.Clob
import java.sql.Connection
import java.sql.DatabaseMetaData
import java.sql.NClob
import java.sql.PreparedStatement
import java.sql.SQLWarning
import java.sql.SQLXML
import java.sql.Savepoint
import java.sql.Statement
import java.sql.Struct
import java.util.Properties
import java.util.concurrent.Executor

class FakeConnection : Connection {
    override fun prepareStatement(sql: String?): PreparedStatement {
        if (sql == null) throw IllegalArgumentException("SQL cannot be null")

        return if (sql.contains("SELECT * FROM channels")) {
            FakePreparedStatement()
        } else if (sql.contains("SELECT * FROM engines")) {
            buildEnginesFakePreparedStatement(null)
        } else if (sql.contains("SELECT * FROM engines WHERE id = ")) {
            buildEnginesFakePreparedStatement(sql.split("WHERE id = ")[1].toIntOrNull())
        } else if (sql.contains("SELECT * FROM transmissions")) {
            buildTransmissionsFakePreparedStatement(null)
        } else if (sql.contains("SELECT * FROM transmissions WHERE id = ")) {
            buildTransmissionsFakePreparedStatement(sql.split("WHERE id = ")[1].toIntOrNull())
        } else if (sql.contains("SELECT * FROM cars")) {
            buildCarsFakePreparedStatement(null)
        } else if (sql.contains("SELECT * FROM cars WHERE id = ")) {
            buildCarsFakePreparedStatement(sql.split("WHERE id = ")[1].toIntOrNull())
        } else {
            throw IllegalArgumentException("SQL not supported: $sql")
        }
    }

    override fun prepareStatement(
        p0: String?,
        p1: Int,
        p2: Int,
    ): PreparedStatement {
        TODO("Not yet implemented")
    }

    override fun prepareStatement(
        p0: String?,
        p1: Int,
        p2: Int,
        p3: Int,
    ): PreparedStatement {
        TODO("Not yet implemented")
    }

    override fun prepareStatement(
        p0: String?,
        p1: Int,
    ): PreparedStatement {
        TODO("Not yet implemented")
    }

    override fun prepareStatement(
        p0: String?,
        p1: IntArray?,
    ): PreparedStatement {
        TODO("Not yet implemented")
    }

    override fun prepareStatement(
        p0: String?,
        p1: Array<out String>?,
    ): PreparedStatement {
        TODO("Not yet implemented")
    }

    override fun prepareCall(p0: String?): CallableStatement {
        TODO("Not yet implemented")
    }

    override fun prepareCall(
        p0: String?,
        p1: Int,
        p2: Int,
    ): CallableStatement {
        TODO("Not yet implemented")
    }

    override fun prepareCall(
        p0: String?,
        p1: Int,
        p2: Int,
        p3: Int,
    ): CallableStatement {
        TODO("Not yet implemented")
    }

    // Other methods throw an exception since they're not needed for this test
    override fun close() {}

    override fun createStatement(): Statement? = null

    override fun createStatement(
        p0: Int,
        p1: Int,
    ): Statement {
        TODO("Not yet implemented")
    }

    override fun createStatement(
        p0: Int,
        p1: Int,
        p2: Int,
    ): Statement {
        TODO("Not yet implemented")
    }

    override fun getAutoCommit(): Boolean = false

    override fun commit() {}

    override fun rollback() {}

    override fun rollback(p0: Savepoint?) {
        TODO("Not yet implemented")
    }

    override fun setAutoCommit(autoCommit: Boolean) {}

    override fun isClosed(): Boolean = false

    override fun getMetaData(): DatabaseMetaData? = null

    override fun isReadOnly(): Boolean = false

    override fun setCatalog(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun getCatalog(): String {
        TODO("Not yet implemented")
    }

    override fun setReadOnly(readOnly: Boolean) {}

    override fun getTransactionIsolation(): Int = 0

    override fun setTransactionIsolation(level: Int) {}

    override fun getSchema(): String? = null

    override fun abort(p0: Executor?) {
        TODO("Not yet implemented")
    }

    override fun setSchema(schema: String?) {}

    override fun createClob(): Clob? = null

    override fun createBlob(): Blob? = null

    override fun createNClob(): NClob {
        TODO("Not yet implemented")
    }

    override fun createSQLXML(): SQLXML {
        TODO("Not yet implemented")
    }

    override fun isValid(p0: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun setClientInfo(
        p0: String?,
        p1: String?,
    ) {
        TODO("Not yet implemented")
    }

    override fun setClientInfo(p0: Properties?) {
        TODO("Not yet implemented")
    }

    override fun getClientInfo(p0: String?): String {
        TODO("Not yet implemented")
    }

    override fun getClientInfo(): Properties {
        TODO("Not yet implemented")
    }

    override fun createArrayOf(
        p0: String?,
        p1: Array<out Any>?,
    ): java.sql.Array {
        TODO("Not yet implemented")
    }

    override fun createStruct(
        p0: String?,
        p1: Array<out Any>?,
    ): Struct {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> unwrap(p0: Class<T>?): T {
        TODO("Not yet implemented")
    }

    override fun isWrapperFor(iface: Class<*>?): Boolean = false

    override fun nativeSQL(sql: String?): String? = null

    override fun getWarnings(): SQLWarning? = null

    override fun clearWarnings() {}

    override fun getTypeMap(): MutableMap<String, Class<*>> {
        TODO("Not yet implemented")
    }

    override fun setTypeMap(p0: MutableMap<String, Class<*>>?) {
        TODO("Not yet implemented")
    }

    override fun setHoldability(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun getHoldability(): Int {
        TODO("Not yet implemented")
    }

    override fun setSavepoint(): Savepoint {
        TODO("Not yet implemented")
    }

    override fun setSavepoint(p0: String?): Savepoint {
        TODO("Not yet implemented")
    }

    override fun releaseSavepoint(p0: Savepoint?) {
        TODO("Not yet implemented")
    }

    override fun getNetworkTimeout(): Int = 0

    override fun setNetworkTimeout(
        executor: java.util.concurrent.Executor?,
        milliseconds: Int,
    ) {}
}
