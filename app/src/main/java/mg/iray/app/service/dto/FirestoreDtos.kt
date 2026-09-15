package mg.iray.app.service.dto

data class FirestoreListResponse(
    val documents: List<FirestoreDocument>? = null
)

data class FirestoreDocument(
    val name: String,
    val fields: Map<String, FieldValue>? = null,
    val createTime: String? = null,
    val updateTime: String? = null
)

data class FieldValue(
    val stringValue: String? = null,
    val integerValue: String? = null,
    val doubleValue: Double? = null,
    val booleanValue: Boolean? = null,
    val arrayValue: ArrayValue? = null
)

data class ArrayValue(
    val values: List<FieldValue>? = null
)

data class FirestoreWriteRequest(
    val fields: Map<String, FieldValue>
)