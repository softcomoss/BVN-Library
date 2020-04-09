package ng.softcom.nibss.libbvncapture.models

data class EnrollmentStats(
    var enrollments: Int,
    var synchronized: Int,
    var successful: Int,
    var duplicates: Int,
    var pending: Int,
    var failed: Int
)