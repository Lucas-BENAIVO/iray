package mg.iray.app.service

import mg.iray.app.service.dto.ArrayValue
import mg.iray.app.service.dto.FieldValue

/**
 * Encodage/décodage d'une Map<String, Any> vers le format "fields" de l'API REST
 * Firestore (stringValue, integerValue, booleanValue, distinctArrayValues...).
 */
object FirestoreFieldCodec {

    fun toFields(data: Map<String, Any>): Map<String, FieldValue> =
        data.mapValues { (_, value) -> value.toFieldValue() }

    fun toData(fields: Map<String, FieldValue>?): Map<String, Any> =
        fields.orEmpty().mapNotNull { (key, value) ->
            value.toValue()?.let { key to it }
        }.toMap()
}

private fun Any.toFieldValue(): FieldValue = when (this) {
    is String -> FieldValue(stringValue = this)
    is Long -> FieldValue(integerValue = this.toString())
    is Int -> FieldValue(integerValue = this.toString())
    is Double -> FieldValue(doubleValue = this)
    is Float -> FieldValue(doubleValue = this.toDouble())
    is Boolean -> FieldValue(booleanValue = this)
    is List<*> -> FieldValue(arrayValue = ArrayValue(this.mapNotNull { it?.toFieldValue() }))
    else -> FieldValue(stringValue = this.toString())
}

private fun FieldValue.toValue(): Any? = when {
    stringValue != null -> stringValue
    integerValue != null -> integerValue.toLong()
    doubleValue != null -> doubleValue
    booleanValue != null -> booleanValue
    arrayValue != null -> arrayValue.values.orEmpty().mapNotNull { it.toValue() }
    else -> null
}