package ng.softcom.nibss.libbvncapture

import androidx.test.core.app.ApplicationProvider
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class BVNCapturerTest {

    @get:Rule
    var initializeExceptionRule: ExpectedException = ExpectedException.none()

    @Test
    fun initialize_requireAppID() {
        RuntimeEnvironment.setMainThread(Thread.currentThread())
        initializeExceptionRule.expect(IllegalStateException::class.java)
        initializeExceptionRule.expectMessage("Please call BVNCapturer.initialize() and pass the appVersion and appID")

        val capturer = BVNCapturer.getInstance(ApplicationProvider.getApplicationContext())

        capturer.startDataCapture()
    }

}