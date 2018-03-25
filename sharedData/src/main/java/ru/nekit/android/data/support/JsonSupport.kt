package ru.nekit.android.data.support

import com.google.gson.Gson
import com.google.gson.JsonObject

object JsonHelper {

    fun readInt(name: String, jo: JsonObject, defaultValue: Int = 0): Int =
            if (jo.has(name))
                jo.get(name).asInt
            else defaultValue

    fun readString(name: String, jo: JsonObject, defaultValue: String = ""): String =
            if (jo.has(name))
                jo.get(name).asString
            else defaultValue

    fun readFloat(name: String, jo: JsonObject, defaultValue: Float = 0F): Float =
            if (jo.has(name))
                jo.get(name).asFloat
            else defaultValue

    fun readDouble(name: String, jo: JsonObject, defaultValue: Double = 0.0): Double =
            if (jo.has(name))
                jo.get(name).asDouble
            else defaultValue

    fun readBoolean(name: String, jo: JsonObject, defaultValue: Boolean = false): Boolean =
            if (jo.has(name))
                jo.get(name).asBoolean
            else defaultValue

    fun readBooleanAsInt(name: String, jo: JsonObject, defaultValue: Int = -1): Int =
            if (jo.has(name))
                if (jo.get(name).asBoolean) 1 else 0
            else defaultValue

    fun <R> readStringListWithConverter(name: String, json: Gson, jo: JsonObject, converter: ((String) -> R)): List<R> {
        val result = ArrayList<R>()
        if (jo.has(name)) {
            json.fromJson(jo.get(name), Array<String>::class.java).forEach {
                result.add(converter(it))
            }
        }
        return result
    }

    fun readListListOfInt(name: String, json: Gson, jo: JsonObject): List<List<Int>> {
        val result: MutableList<List<Int>> = ArrayList()
        if (jo.has(name))
            json.fromJson(jo.get(name), Array<IntArray>::class.java).forEach {
                result.add(it.toList())
            }

        return result
    }

}