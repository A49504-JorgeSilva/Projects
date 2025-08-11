package pt.isel

import java.sql.ResultSet

fun buildEnginesFakePreparedStatement(id: Int?): FakePreparedStatement {
    class EnginesFakePreparedStatement : FakePreparedStatement() {
        override fun executeQuery(): ResultSet = buildEnginesFakeResultSet(id)
    }

    return EnginesFakePreparedStatement()
}

fun buildTransmissionsFakePreparedStatement(id: Int?): FakePreparedStatement {
    class TransmissionsFakePreparedStatement : FakePreparedStatement() {
        override fun executeQuery(): ResultSet = buildTransmissionsFakeResultSet(id)
    }

    return TransmissionsFakePreparedStatement()
}

fun buildCarsFakePreparedStatement(id: Int?): FakePreparedStatement {
    class CarsFakePreparedStatement : FakePreparedStatement() {
        override fun executeQuery(): ResultSet = buildCarsFakeResultSet(id)
    }

    return CarsFakePreparedStatement()
}
