package com.developersam.webcore.gson

import com.developersam.webcore.date.dateToString
import com.developersam.webcore.date.stringToDate
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.util.Date

/**
 * The date adapter consistently used in the app.
 */
object GsonDateAdapter : JsonSerializer<Date>, JsonDeserializer<Date> {

    override fun serialize(src: Date, typeOfSrc: Type,
                           context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(dateToString(date = src));
    }

    override fun deserialize(json: JsonElement, typeOfT: Type,
                             context: JsonDeserializationContext): Date? {
        return stringToDate(date = json.asString)
    }

}