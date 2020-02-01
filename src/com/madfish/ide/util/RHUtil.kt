package com.madfish.ide.util

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.intellij.CommonBundle
import com.madfish.ide.configurable.RHSettings
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import java.util.*


/**
 * Created by Roger™
 */
class RHUtil {
    companion object {
        val gson: Gson = GsonBuilder().registerTypeAdapter(LocalDateTime::class.java, JsonDeserializer<LocalDateTime> { json, _, _ ->
            try {
                LocalDateTime.parse(json.asJsonPrimitive.asString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            } catch (ex: DateTimeParseException) {
                ZonedDateTime.parse(json.asJsonPrimitive.asString).toLocalDateTime()
            }
        }).registerTypeAdapterFactory(object : TypeAdapterFactory {
            override fun <T : Any?> create(gson: Gson?, type: TypeToken<T>?): TypeAdapter<T>? {
                if (type?.rawType == String::class.java) {
                    @Suppress("unchecked_cast")
                    return object : TypeAdapter<String>() {
                        override fun write(writer: JsonWriter?, value: String?) {
                            if (value == null) {
                                writer?.nullValue()
                                return
                            }
                            writer?.value(value)
                        }

                        override fun read(reader: JsonReader?): String? {
                            if (reader?.peek() == JsonToken.NULL) {
                                reader.nextNull()
                                return ""
                            }
                            return reader?.nextString()
                        }
                    } as TypeAdapter<T>
                }
                            return null
                        }
                    }).create()

        fun getTimeDelta(time: LocalDateTime?): String {
            time?.let {
                val now = LocalDateTime.now(ZoneOffset.UTC)
                val daysDelta = ChronoUnit.DAYS.between(time, now)
                val hoursDelta = ChronoUnit.HOURS.between(time, now)
                val minutesDelta = ChronoUnit.MINUTES.between(time, now)
                return when {
                    daysDelta > 0 -> "$daysDelta${RHUtil.message("View.delta.days")}"
                    hoursDelta > 0 -> "$hoursDelta${RHUtil.message("View.delta.hours")}"
                    minutesDelta > 0 -> "$minutesDelta${RHUtil.message("View.delta.minutes")}"
                    else -> RHUtil.message("View.delta.recent")
                }
            }
            return "N/A"
        }

        fun message(key: String, vararg params: Any): String {
            val filename = "messages.lang-${RHSettings.instance.lang.locale}"
            return CommonBundle.message(ResourceBundle.getBundle(filename, UTF8Control()), key, params)
        }
    }

    class UTF8Control : ResourceBundle.Control() {

        @Throws(IllegalAccessException::class, InstantiationException::class, IOException::class)
        override fun newBundle(baseName: String, locale: Locale, format: String, loader: ClassLoader, reload: Boolean): ResourceBundle? {
            val bundleName = toBundleName(baseName, locale)
            val resourceName = toResourceName(bundleName, "properties")
            var bundle: ResourceBundle? = null
            var stream: InputStream? = null
            if (reload) {
                val url = loader.getResource(resourceName)
                if (url != null) {
                    val connection = url.openConnection()
                    if (connection != null) {
                        connection.useCaches = false
                        stream = connection.getInputStream()
                    }
                }
            } else {
                stream = loader.getResourceAsStream(resourceName)
            }
            if (stream != null) {
                try {
                    bundle = PropertyResourceBundle(InputStreamReader(stream, "UTF-8"))
                } finally {
                    stream.close()
                }
            }
            return bundle
        }
    }
}