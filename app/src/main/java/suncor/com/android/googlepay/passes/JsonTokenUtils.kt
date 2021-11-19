package suncor.com.android.googleapis.passes

import android.apache.commons.codec.binary.Base64
import android.apache.commons.codec.binary.StringUtils
import com.google.common.base.Joiner
import com.google.gson.Gson
import com.google.gson.JsonObject

/**
 * Some utility functions for [JsonToken]s.
 */
internal object JsonTokenUtils {
    const val DELIMITER = "."
    fun toBase64(json: JsonObject?): String {
        return convertToBase64(toJson(json))
    }

    fun toJson(json: JsonObject?): String {
        return Gson().toJson(json)
    }

    fun convertToBase64(source: String?): String {
        return Base64.encodeBase64URLSafeString(StringUtils.getBytesUtf8(source))
    }

    fun toDotFormat(vararg parts: String?): String {
        return Joiner.on('.').useForNull("").join(parts)
    }
}