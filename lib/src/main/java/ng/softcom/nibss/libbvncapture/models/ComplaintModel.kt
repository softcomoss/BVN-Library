package ng.softcom.nibss.libbvncapture.models

data class ComplaintModel(
    val _id: String,
    val agent: String,
    val comments: List<String>,
    val createdAt: String,
    val deleted: Boolean,
    val description: String,
    val issueId: String,
    val status: String,
    val title: String,
    val resolutionNote: String?,
    val updatedAt: String
)