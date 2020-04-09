package ng.softcom.nibss.libbvncapture.models

import java.text.DateFormat
import java.util.*

data class EntryModel(
    val entryId: String,
    private val lastEdit: Long,
    val currentStep: Int = 0,
    var timeSynced: Long? = null,
    var entryTitle: String
) {
    val dateToBeDeleted: String
        get() = if (timeSynced == null)
            DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(lastEdit + 259200000))
        else
            "N/A"

    val lastEditDate: String
        get() {
            val date = Date(lastEdit)
            return DateFormat.getDateInstance(DateFormat.MEDIUM).format(date)
        }
    val percentageCompletion: String
        get() {
            val percentage = (currentStep * 100) / 4
            return "$percentage% completed"
        }

    fun entryName(position: Int): String {
        return "Entry $position"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EntryModel

        if (entryId != other.entryId) return false
        if (lastEdit != other.lastEdit) return false
        if (currentStep != other.currentStep) return false

        return true
    }

    override fun hashCode(): Int {
        var result = entryId.hashCode()
        result = 31 * result + lastEdit.hashCode()
        result = 31 * result + currentStep
        return result
    }


}