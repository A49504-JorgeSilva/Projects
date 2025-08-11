package pt.isel

import kotlin.reflect.KCallable
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

fun Any.membersToJson(): String {
    return buildJsonString(this)
}

private fun buildJsonString(obj: Any?):String {
    if (obj == null) return "null"
    return when(obj){
        is String -> "\"${obj.replace("\"", "\\\"")}\""
        is Int, is Boolean, is Long ->  obj.toString()
        is Collection<*> -> buildJsonArray(obj)
        is Array<*> -> buildJsonArray(obj.toList())
        else ->  buildJsonObject(obj)
    }
}
private fun buildJsonArray(obj: Collection<*>):String {
    val jsonValues = obj.joinToString(","){buildJsonString(it!!)}
    return "[$jsonValues]"
}

private fun buildJsonObject(obj: Any):String {
    val kClass = obj::class
    val members = mutableListOf<Pair<String,String>>()
    kClass.memberProperties.forEach { prop ->
        prop.isAccessible = true
        val propName = getJsonPropertyName(prop)
        val propValue = prop.getter.call(obj)
        val jsonValue = formatValue(prop,propValue)
        members.add(propName to jsonValue)
    }

    //Non-void parameterless functions
    kClass.memberFunctions
        .filter {it.parameters.size == 1 && it.returnType.classifier != Unit::class}
        .forEach { func ->
            func.isAccessible = true
            val funcName = getJsonPropertyName(func)
            val funcValue = func.call(obj)
            val jsonValue = formatValue(func,funcValue)
            members.add(funcName to jsonValue)
    }

    val jsonProps = members.joinToString(",") { (propName, jsonValue) ->
        "\"${propName}\":$jsonValue"
    }
    return "{$jsonProps}"
}

private fun getJsonPropertyName(prop: KCallable<*>): String {
    val annotation = prop.findAnnotation<ToJsonPropName>()
    return annotation?.name ?: prop.name
}

private fun formatValue(prop: KCallable<*>, propValue: Any?): String {
    if (propValue == null) return "null"
    val formatterAnnotation = prop.findAnnotation<ToJsonFormatter>()
    return if(formatterAnnotation != null){
        try {
            val formatterClass = formatterAnnotation.formatter.java
            val formatter = formatterClass.getDeclaredConstructor().newInstance()
            if (formatter is ToJsonValueFormatter) {
                "\"${formatter.format(propValue)}\""
            }else  {
                val invokeMethod = formatterClass.methods.find { it.name == "invoke" }
                if (invokeMethod != null) {
                    val formattedValue = invokeMethod.invoke(formatter, propValue) as String
                    "\"$formattedValue\""
                } else {
                    buildJsonString(propValue)
                }
            }
        }catch (e:Exception){
            buildJsonString(propValue)
        }
    } else {
        buildJsonString(propValue)
    }
}
