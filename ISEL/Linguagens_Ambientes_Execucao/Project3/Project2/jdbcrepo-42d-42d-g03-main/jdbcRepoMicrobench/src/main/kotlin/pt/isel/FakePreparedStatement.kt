package pt.isel

import java.io.InputStream
import java.io.Reader
import java.math.BigDecimal
import java.net.URL
import java.sql.Blob
import java.sql.Clob
import java.sql.Connection
import java.sql.Date
import java.sql.NClob
import java.sql.ParameterMetaData
import java.sql.PreparedStatement
import java.sql.Ref
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.RowId
import java.sql.SQLWarning
import java.sql.SQLXML
import java.sql.Time
import java.sql.Timestamp
import java.util.Calendar

open class FakePreparedStatement : PreparedStatement {
    override fun executeQuery(): ResultSet = FakeResultSet()

    override fun executeQuery(p0: String?): ResultSet {
        TODO("Not yet implemented")
    }

    override fun setString(
        parameterIndex: Int,
        x: String?,
    ) {}

    override fun setBytes(
        p0: Int,
        p1: ByteArray?,
    ) {
        TODO("Not yet implemented")
    }

    override fun close() {}

    override fun executeUpdate(): Int = 1

    override fun executeUpdate(p0: String?): Int {
        TODO("Not yet implemented")
    }

    override fun executeUpdate(
        p0: String?,
        p1: Int,
    ): Int {
        TODO("Not yet implemented")
    }

    override fun executeUpdate(
        p0: String?,
        p1: IntArray?,
    ): Int {
        TODO("Not yet implemented")
    }

    override fun executeUpdate(
        p0: String?,
        p1: Array<out String>?,
    ): Int {
        TODO("Not yet implemented")
    }

    override fun getMaxFieldSize(): Int {
        TODO("Not yet implemented")
    }

    override fun setMaxFieldSize(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun getMaxRows(): Int {
        TODO("Not yet implemented")
    }

    override fun setMaxRows(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun setEscapeProcessing(p0: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getQueryTimeout(): Int {
        TODO("Not yet implemented")
    }

    override fun setQueryTimeout(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun cancel() {
        TODO("Not yet implemented")
    }

    override fun getWarnings(): SQLWarning {
        TODO("Not yet implemented")
    }

    override fun clearWarnings() {
        TODO("Not yet implemented")
    }

    override fun setCursorName(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun getGeneratedKeys(): ResultSet = FakeResultSet()

    override fun getResultSetHoldability(): Int {
        TODO("Not yet implemented")
    }

    override fun isClosed(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setPoolable(p0: Boolean) {
        TODO("Not yet implemented")
    }

    override fun isPoolable(): Boolean {
        TODO("Not yet implemented")
    }

    override fun closeOnCompletion() {
        TODO("Not yet implemented")
    }

    override fun isCloseOnCompletion(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setNull(
        p0: Int,
        p1: Int,
    ) {
        TODO("Not yet implemented")
    }

    override fun setNull(
        p0: Int,
        p1: Int,
        p2: String?,
    ) {
        TODO("Not yet implemented")
    }

    // Unused methods
    override fun execute(): Boolean = false

    override fun execute(p0: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun execute(
        p0: String?,
        p1: Int,
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun execute(
        p0: String?,
        p1: IntArray?,
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun execute(
        p0: String?,
        p1: Array<out String>?,
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun clearParameters() {}

    override fun setObject(
        p0: Int,
        p1: Any?,
        p2: Int,
    ) {
        TODO("Not yet implemented")
    }

    override fun setObject(
        p0: Int,
        p1: Any?,
    ) { }

    override fun setObject(
        p0: Int,
        p1: Any?,
        p2: Int,
        p3: Int,
    ) {
        TODO("Not yet implemented")
    }

    override fun setCharacterStream(
        p0: Int,
        p1: Reader?,
        p2: Int,
    ) {
        TODO("Not yet implemented")
    }

    override fun setCharacterStream(
        p0: Int,
        p1: Reader?,
        p2: Long,
    ) {
        TODO("Not yet implemented")
    }

    override fun setCharacterStream(
        p0: Int,
        p1: Reader?,
    ) {
        TODO("Not yet implemented")
    }

    override fun setRef(
        p0: Int,
        p1: Ref?,
    ) {
        TODO("Not yet implemented")
    }

    override fun setBlob(
        p0: Int,
        p1: Blob?,
    ) {
        TODO("Not yet implemented")
    }

    override fun setBlob(
        p0: Int,
        p1: InputStream?,
        p2: Long,
    ) {
        TODO("Not yet implemented")
    }

    override fun setBlob(
        p0: Int,
        p1: InputStream?,
    ) {
        TODO("Not yet implemented")
    }

    override fun setClob(
        p0: Int,
        p1: Clob?,
    ) {
        TODO("Not yet implemented")
    }

    override fun setClob(
        p0: Int,
        p1: Reader?,
        p2: Long,
    ) {
        TODO("Not yet implemented")
    }

    override fun setClob(
        p0: Int,
        p1: Reader?,
    ) {
        TODO("Not yet implemented")
    }

    override fun setArray(
        p0: Int,
        p1: java.sql.Array?,
    ) {
        TODO("Not yet implemented")
    }

    override fun getMetaData(): ResultSetMetaData {
        TODO("Not yet implemented")
    }

    override fun setURL(
        p0: Int,
        p1: URL?,
    ) {
        TODO("Not yet implemented")
    }

    override fun getParameterMetaData(): ParameterMetaData {
        TODO("Not yet implemented")
    }

    override fun setRowId(
        p0: Int,
        p1: RowId?,
    ) {
        TODO("Not yet implemented")
    }

    override fun setNString(
        p0: Int,
        p1: String?,
    ) {
        TODO("Not yet implemented")
    }

    override fun setNCharacterStream(
        p0: Int,
        p1: Reader?,
        p2: Long,
    ) {
        TODO("Not yet implemented")
    }

    override fun setNCharacterStream(
        p0: Int,
        p1: Reader?,
    ) {
        TODO("Not yet implemented")
    }

    override fun setNClob(
        p0: Int,
        p1: NClob?,
    ) {
        TODO("Not yet implemented")
    }

    override fun setNClob(
        p0: Int,
        p1: Reader?,
        p2: Long,
    ) {
        TODO("Not yet implemented")
    }

    override fun setNClob(
        p0: Int,
        p1: Reader?,
    ) {
        TODO("Not yet implemented")
    }

    override fun setSQLXML(
        p0: Int,
        p1: SQLXML?,
    ) {
        TODO("Not yet implemented")
    }

    override fun setLong(
        parameterIndex: Int,
        x: Long,
    ) {}

    override fun setFloat(
        p0: Int,
        p1: Float,
    ) {
        TODO("Not yet implemented")
    }

    override fun setDouble(
        p0: Int,
        p1: Double,
    ) {
        TODO("Not yet implemented")
    }

    override fun setBigDecimal(
        p0: Int,
        p1: BigDecimal?,
    ) {
        TODO("Not yet implemented")
    }

    override fun setInt(
        parameterIndex: Int,
        x: Int,
    ) {}

    override fun setBoolean(
        parameterIndex: Int,
        x: Boolean,
    ) {}

    override fun setByte(
        p0: Int,
        p1: Byte,
    ) {
        TODO("Not yet implemented")
    }

    override fun setShort(
        p0: Int,
        p1: Short,
    ) {
        TODO("Not yet implemented")
    }

    override fun setDate(
        parameterIndex: Int,
        x: Date?,
    ) {}

    override fun setDate(
        p0: Int,
        p1: Date?,
        p2: Calendar?,
    ) {
        TODO("Not yet implemented")
    }

    override fun setTime(
        p0: Int,
        p1: Time?,
    ) {
        TODO("Not yet implemented")
    }

    override fun setTime(
        p0: Int,
        p1: Time?,
        p2: Calendar?,
    ) {
        TODO("Not yet implemented")
    }

    override fun setTimestamp(
        p0: Int,
        p1: Timestamp?,
    ) {
        TODO("Not yet implemented")
    }

    override fun setTimestamp(
        p0: Int,
        p1: Timestamp?,
        p2: Calendar?,
    ) {
        TODO("Not yet implemented")
    }

    override fun setAsciiStream(
        p0: Int,
        p1: InputStream?,
        p2: Int,
    ) {
        TODO("Not yet implemented")
    }

    override fun setAsciiStream(
        p0: Int,
        p1: InputStream?,
        p2: Long,
    ) {
        TODO("Not yet implemented")
    }

    override fun setAsciiStream(
        p0: Int,
        p1: InputStream?,
    ) {
        TODO("Not yet implemented")
    }

    override fun setUnicodeStream(
        p0: Int,
        p1: InputStream?,
        p2: Int,
    ) {
        TODO("Not yet implemented")
    }

    override fun setBinaryStream(
        p0: Int,
        p1: InputStream?,
        p2: Int,
    ) {
        TODO("Not yet implemented")
    }

    override fun setBinaryStream(
        p0: Int,
        p1: InputStream?,
        p2: Long,
    ) {
        TODO("Not yet implemented")
    }

    override fun setBinaryStream(
        p0: Int,
        p1: InputStream?,
    ) {
        TODO("Not yet implemented")
    }

    override fun getResultSet(): ResultSet? = null

    override fun getUpdateCount(): Int = 0

    override fun getMoreResults(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getMoreResults(p0: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun setFetchDirection(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun getFetchDirection(): Int {
        TODO("Not yet implemented")
    }

    override fun setFetchSize(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun getFetchSize(): Int {
        TODO("Not yet implemented")
    }

    override fun getResultSetConcurrency(): Int {
        TODO("Not yet implemented")
    }

    override fun getResultSetType(): Int {
        TODO("Not yet implemented")
    }

    override fun addBatch() {
        TODO("Not yet implemented")
    }

    override fun addBatch(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun clearBatch() {
        TODO("Not yet implemented")
    }

    override fun executeBatch(): IntArray {
        TODO("Not yet implemented")
    }

    override fun getConnection(): Connection {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> unwrap(p0: Class<T>?): T {
        TODO("Not yet implemented")
    }

    override fun isWrapperFor(iface: Class<*>?): Boolean = false
}
