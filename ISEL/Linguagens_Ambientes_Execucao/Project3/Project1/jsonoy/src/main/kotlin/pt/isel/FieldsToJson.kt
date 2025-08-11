package pt.isel

import java.lang.reflect.Field
import kotlin.reflect.full.memberProperties

fun Any.fieldsToJson(): String {
    return buildJsonObject(this)
}

private fun buildJsonString(obj:Any):String{
    return when(obj){
        is String -> "\"$obj\""
        is Int, is Boolean ->  obj.toString()
        is Collection<*> -> buildJsonArray(obj)
        else ->  buildJsonObject(obj)
    }
}

private fun buildJsonArray(obj:Collection<*>):String{
    val jsonValues = obj.joinToString(","){buildJsonString(it!!)}
    return "[$jsonValues]"
}

private fun buildJsonObject(obj:Any):String{
    val fields: Array<Field> = obj.javaClass.declaredFields
    val jsonProps = fields.joinToString(",") { field ->
        field.isAccessible = true
        val propName = field.name
        val propValue = field.get(obj)
        val jsonValue = buildJsonString(propValue)
        "\"${propName}\":$jsonValue"
    }
    return "{$jsonProps}"
}