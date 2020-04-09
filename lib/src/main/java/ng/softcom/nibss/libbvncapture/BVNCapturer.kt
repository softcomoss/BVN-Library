package ng.softcom.nibss.libbvncapture

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import ng.softcom.nibss.libbvncapture.models.*

/**
 * The DataBeaver SDK class.
 * It is the base class through which the SDK can be communicated with.
 * @param context is the context from which the form capture is launched.
 */
class BVNCapturer private constructor(private var context: Context) {

    private var appId: String? = null
    private var appVersion: String? = null
    private var entriesUri = Uri.parse("content://ng.softcom.nibss.entryprovider")
    private var agentUri = Uri.parse("content://ng.nibss.softcom.bvnsdk.agentprovider")
    private var complainsUri = Uri.parse("content://ng.com.nibss.bvncapturesdk.complainprovider")
    private var enrolmentStatsUri =
        Uri.parse("content://ng.com.nibss.bvncapturesdk.enrolmentstatsprovider")
    private val handler = Handler()


    val complaintsLiveData = MutableLiveData<List<ComplaintModel>>()

    val complaintStatsLiveData = MutableLiveData<ComplaintStats>()

    val allEntriesLiveData = MutableLiveData<List<EntryModel>>()

    val syncedEntriesLiveData = MutableLiveData<List<EntryModel>>()

    val offlineEntriesLiveData = MutableLiveData<List<EntryModel>>()

    val enrollmentStatsLiveData = MutableLiveData<EnrollmentStats>()

    val agentLiveData = MutableLiveData<UserModel>()

    fun initialize(appId: String, appVersion: String) {
        this.appId = appId
        this.appVersion = appVersion
        updateData()
    }

    private fun updateData() {
        val stats = getEnrolmentStats()
        enrollmentStatsLiveData.postValue(

            EnrollmentStats(
                getAllEntriesList().size,
                getAllEntriesList().filter { it.timeSynced != null && it.timeSynced!! > 0 }.size,
                stats.successful,
                stats.duplicates,
                filterOfflineEntries().size,
                stats.failed
            )
        )

        offlineEntriesLiveData.postValue(filterOfflineEntries())

        syncedEntriesLiveData.postValue(getAllEntriesList().filter {
            it.timeSynced != null && it.timeSynced!! > 0
        })

        allEntriesLiveData.postValue(getAllEntriesList())

        complaintsLiveData.postValue(getAllComplainList())

        complaintStatsLiveData.postValue(
            ComplaintStats(
                getAllComplainList().size,
                getAllComplainList().filter { it.status == "RESOLVED" }.size
            )
        )

        agentLiveData.postValue(getAgent())

        handler.postDelayed({
            updateData()
        }, 1000)
    }

    private fun filterOfflineEntries(): List<EntryModel> {
        return getAllEntriesList().filter {
            it.timeSynced == null || it.timeSynced!! <= 0
        }
    }

    /**
     * Call this method to start the data capture.
     * @throws IllegalArgumentException if no context is passed in via the constructor or
     * the DSL builder
     * @throws IllegalArgumentException if the dispatchPath is invalid or null
     */
    fun startDataCapture(entryId: String? = null) {
        startSDKApp(CAPTURE_BVN_ACTION, context, entryId)
    }

    private fun startSDKApp(
        action: String,
        context: Context,
        entryId: String? = null,
        complaintId: String? = null,
        enrolmentStatusFilter: String? = null
    ) {
        val APP_VERSION = "Application Version"
        val API_KEY = "API Key"
        val ENTRY_ID = "Entry ID"
        val COMPLAINT_ID = "Complaint ID"
        val ENROLMENT_STATUS_FILTER = "Enrolment Status Filter"

        if (appId == null || appVersion == null) {
            throw IllegalStateException("Please call BVNCapturer.initialize() and pass the appVersion and appID")
        }

        val intent = Intent(action)
        intent.putExtra(APP_VERSION, appVersion)
        intent.putExtra(API_KEY, appId)
        intent.putExtra(ENTRY_ID, entryId)
        intent.putExtra(COMPLAINT_ID, complaintId)
        intent.putExtra(ENROLMENT_STATUS_FILTER, enrolmentStatusFilter)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val manager = context.packageManager
        val infos = manager.queryIntentActivities(intent, 0)
        if (infos.size > 0) { //Then there is an Application(s) can handle your intent
            context.startActivity(intent)
        } else { //No Application can handle your intent
            Toast.makeText(
                context,
                "You need to install the BVN SDK from Google Drive.",
                Toast.LENGTH_LONG
            ).show()
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://drive.google.com/open?id=1adnTQD9jIiIZO7VIrGZdAhMgUJn47Xbf")
                ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
            )
        }
    }

    private val uploadedEntriesCount: Int
        get() = getAllEntriesList().filter {
            it.timeSynced != null && it.timeSynced!! > 0
        }.size

    private fun getAgent(): UserModel? {
        val cursor = context.contentResolver.query(agentUri, null, null, null, null)
        Log.d("USER CURSOR", "${cursor?.count}")
        val userModel = UserModel()
        return if (cursor?.moveToNext() == true && cursor.count > 0) {
            userModel.userId = cursor.getString(cursor.getColumnIndex("id"))
            userModel.newAgent = cursor.getInt(cursor.getColumnIndex("newAgent")) == 1
            userModel.status = cursor.getString(cursor.getColumnIndex("status"))
            userModel.firstName = cursor.getString(cursor.getColumnIndex("firstName"))
            userModel.lastName = cursor.getString(cursor.getColumnIndex("lastName"))
            userModel.middleName = cursor.getString(cursor.getColumnIndex("middleName"))
            userModel.email = cursor.getString(cursor.getColumnIndex("email"))
            userModel.classification = cursor.getString(cursor.getColumnIndex("classification"))
            userModel.lastLogin = cursor.getString(cursor.getColumnIndex("lastLogin"))
            userModel.phone = cursor.getString(cursor.getColumnIndex("phone"))
            userModel.state = cursor.getString(cursor.getColumnIndex("state"))
            userModel.lga = cursor.getString(cursor.getColumnIndex("lga"))
            userModel.BVN = cursor.getString(cursor.getColumnIndex("BVN"))
            userModel.role = cursor.getString(cursor.getColumnIndex("role"))
            userModel.bankName = cursor.getString(cursor.getColumnIndex("bankName"))
            userModel.accountNumber = cursor.getString(cursor.getColumnIndex("accountNumber"))
            userModel.accountName = cursor.getString(cursor.getColumnIndex("lastLogin"))

            cursor.close()
            userModel
        } else {
            null
        }
    }

    private fun getEnrolmentStats(): EnrollmentStats {
        val cursor = context.contentResolver.query(enrolmentStatsUri, null, null, null, null)
        Log.d("ENROLMENT STATS CURSOR", "${cursor?.count}")
        val enrollmentStats = EnrollmentStats(0, 0, 0, 0, 0, 0)

        while (cursor?.moveToNext() == true) {
            enrollmentStats.synchronized = cursor.getInt(cursor.getColumnIndex("synchronizedCount"))
            enrollmentStats.successful = cursor.getInt(cursor.getColumnIndex("successfulCount"))
            enrollmentStats.duplicates = cursor.getInt(cursor.getColumnIndex("duplicateCount"))
            enrollmentStats.pending = cursor.getInt(cursor.getColumnIndex("pendingCount"))
            enrollmentStats.failed = cursor.getInt(cursor.getColumnIndex("failedCount"))
            enrollmentStats.enrollments = cursor.getInt(cursor.getColumnIndex("enrolledCount"))

        }
        cursor?.close()

        return enrollmentStats
    }


    private fun getAllComplainList(): MutableList<ComplaintModel> {
        val cursor = context.contentResolver.query(complainsUri, null, null, null, null)
        Log.d("COMPLAIN CURSOR", "${cursor?.count}")
        val list = mutableListOf<ComplaintModel>()
        while (cursor?.moveToNext() == true) {
            val _id: String = cursor.getString(cursor.getColumnIndex("_id"))
            val agent: String = cursor.getString(cursor.getColumnIndex("agent"))
            val createdAt: String = cursor.getString(cursor.getColumnIndex("createdAt"))
            val issueId: String = cursor.getString(cursor.getColumnIndex("issueId"))
            val deleted: Boolean = cursor.getInt(cursor.getColumnIndex("deleted")) == 1
            val description: String = cursor.getString(cursor.getColumnIndex("description"))
            val status: String = cursor.getString(cursor.getColumnIndex("status"))
            val title: String = cursor.getString(cursor.getColumnIndex("title"))
            val resolutionNote: String? = cursor.getString(cursor.getColumnIndex("resolutionNote"))
            val updatedAt: String = cursor.getString(cursor.getColumnIndex("updatedAt"))
            val complainModel = ComplaintModel(
                _id,
                agent,
                emptyList(),
                createdAt,
                deleted,
                description,
                issueId,
                status,
                title,
                resolutionNote,
                updatedAt
            )
            list.add(complainModel)
        }
        cursor?.close()
        return list
    }


    private fun getAllEntriesList(): MutableList<EntryModel> {
        val cursor = context.contentResolver.query(entriesUri, null, null, null, null)
        Log.d("ENTRY CURSOR", "${cursor?.count}")
        val list = mutableListOf<EntryModel>()
        while (cursor?.moveToNext() == true) {
            val entryId: String = cursor.getString(cursor.getColumnIndex("entryId"))
            val currentStep: Int = cursor.getInt(cursor.getColumnIndex("currentStep"))
            val entryTitle: String? = cursor.getString(cursor.getColumnIndex("entryTitle"))
            val dateLastEdited: Long? = cursor.getLong(cursor.getColumnIndex("dateLastEdited"))
            val timeSynced: Long? = cursor.getLong(cursor.getColumnIndex("timeSynced"))
            val offline = EntryModel(
                entryId = entryId,
                currentStep = currentStep,
                lastEdit = dateLastEdited ?: System.currentTimeMillis(),
                timeSynced = timeSynced,
                entryTitle = entryTitle ?: "N/A"
            )
            list.add(offline)
        }
        cursor?.close()
        return list
    }

    fun startComplaintDetails(complaintId: String) =
        startSDKApp(COMPLAINT_DETAIL_ACTION, context, complaintId = complaintId)

    fun startSubmitComplaint() = startSDKApp(SUBMIT_COMPLAINT_ACTION, context)

    fun logOut() = startSDKApp(LOGOUT_OF_BVN_ACTION, context)

    fun reauthenticate() = startSDKApp(REAUTHENTICATE_ACTION, context)

    fun showRemoteEnrolments(statusFilter: String) =
        startSDKApp(REMOTE_ENROLMENTS_ACTION, context, enrolmentStatusFilter = statusFilter)

    companion object : SingletonWrapper<BVNCapturer, Context>(::BVNCapturer) {

        private const val CAPTURE_BVN_ACTION =
            "ng.softcom.databeaver.sdk.intents.CAPTURE_BVN_ACTION"
        private const val SUBMIT_COMPLAINT_ACTION =
            "ng.softcom.databeaver.sdk.intents.SUBMIT_COMPLAINT_ACTION"
        private const val COMPLAINT_COMMENTS_ACTION =
            "ng.softcom.databeaver.sdk.intents.COMPLAINT_COMMENTS_ACTION"
        private const val COMPLAINT_DETAIL_ACTION =
            "ng.softcom.databeaver.sdk.intents.COMPLAINT_DETAIL_ACTION"
        private const val LOGOUT_OF_BVN_ACTION =
            "ng.softcom.databeaver.sdk.intents.LOGOUT_OF_BVN_ACTION"
        private const val REAUTHENTICATE_ACTION =
            "ng.softcom.databeaver.sdk.intents.REAUTHENTICATE_ACTION"
        private const val REMOTE_ENROLMENTS_ACTION =
            "ng.softcom.databeaver.sdk.intents.REMOTE_ENROLMENTS_ACTION"

    }

}