package ng.softcom.nibss.libbvncapture.models


data class UserModel(
    var userId: String = "",
    var newAgent: Boolean = true,
    var status: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var middleName: String = "",
    var email: String = "",
    var classification: String = "",
    var lastLogin: String = "",
    var phone: String? = null,
    var state: String? = null,
    var lga: String? = null,
    var BVN: String? = null,
    var role: String? = null,
    var bankName: String? = null,
    var accountNumber: String? = null,
    var accountName: String? = null
)