package name.schedenig.adbcontrol.test

import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.spyk
import name.schedenig.adbcontrol.AdbHelper
import name.schedenig.adbcontrol.Config
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.io.File
import java.util.logging.LogManager

@ExtendWith(MockKExtension::class)
class TestShell {

    @RelaxedMockK
    lateinit var config: Config

    val cmdSlot = slot<String>()

    companion object {
        @JvmStatic
        @BeforeAll
        fun setUp() {
            LogManager.getLogManager().reset()
        }
    }
    @Test
    fun testPull() {

        every {config.phoneImageFilePath} returns "/mnt/sdcard/screen.png"
        every {config.adbCommand} returns "adb"
        every {config.localImageFilePath} returns "/home/screen.png"
        every {config.httpUpload} returns null
        val adbHelper = spyk(AdbHelper(config))
        every { adbHelper.executeCommand(capture(cmdSlot), any()) } answers { println(cmdSlot.captured) }

        adbHelper.screenshot(File(config.localImageFilePath))

    }

    @Test
    fun testShellWget() {
        every {config.phoneImageFilePath} returns "/mnt/sdcard/screen.png"
        every {config.adbCommand} returns "adb"
        every {config.localImageFilePath} returns "/home/screen.png"
        every {config.httpUpload} returns "http://localhost:5000/upload_screenshot"
        val adbHelper = spyk(AdbHelper(config))
        every { adbHelper.executeCommand(capture(cmdSlot), any()) } answers { println("adb ${cmdSlot.captured}") }
        adbHelper.screenshot(File(config.localImageFilePath))
    }
}
