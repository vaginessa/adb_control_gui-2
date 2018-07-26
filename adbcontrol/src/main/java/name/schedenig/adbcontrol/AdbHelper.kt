/*******************************************************************************
 * Copyright (c) 2014 Marian Schedenig
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Marian Schedenig - initial API and implementation
 */
package name.schedenig.adbcontrol

import java.io.File
import java.net.URL
import java.text.MessageFormat

private val httpUploadCmd = listOf("""
            |echo "--FILEUPLOAD\r\n
            |Content-Disposition: form-data; name=\"file\"; filename=\"{0}\";\r\n
            |Content-Type: application/octet-stream\r\n
            |Media Type: application/octet-stream\r\n\r" > {2}
            |""".trimMargin().replace("\n", ""),
        """cat {1} >> {2}""",
        """echo "\r\n--FILEUPLOAD--\r" >> {2}""",
        """
            |echo "POST {3} HTTP/1.1\r\n
            |User-Agent: Wget/1.19.4 \(linux-gnu\)\r\n
            |Accept: */*\r\nAccept-Encoding: identity\r\n
            |Host: {4}:{5}\r\n
            |Connection: Keep-Alive\r\n
            |Content-type: multipart/form-data;boundary=FILEUPLOAD\r\n
            |Content-Length: `stat -c %s {2}`\r\n\r" > {6}
            |""".trimMargin().replace("\n", ""),
        """cat {2} >> {6}""",
        """cat {6} | nc {4} {5}"""
).joinToString(" && ")


class AdbHelper(private val config: Config) {

    var isSetupUpload = false

    private fun executeShellCommand(vararg cmd: String) {
        executeCommand("shell", *cmd)
    }

    fun executeCommand(vararg cmd: String) {
//        val cmdLine = "${config.adbCommand} ${cmd.joinToString(" ")}"
//        println(cmdLine)

        try {
            ProcessBuilder()
                    .redirectErrorStream(true)
//                    .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                    .command(config.adbCommand, *cmd)
                    .start().waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendClick(x: Int, y: Int) {
        println("Click at: $x/$y")
        executeShellCommand(MessageFormat.format("input tap {0,number,#####} {1,number,#####}", x, y))
    }

    fun sendText(text: String) {
        executeShellCommand(MessageFormat.format("input text {0}", text))
    }

    fun sendKey(key: AndroidKey) {
        executeShellCommand(MessageFormat.format("input keyevent {0}", key.code))
    }

    fun screenshot(target: File) {
        val fileName = config.phoneImageFilePath
        executeShellCommand("screencap", "-p $fileName")
        if (config.httpUpload == null) {
            adbPull(target, fileName!!)
        } else {
            if (!isSetupUpload) setupUpload(config.httpUpload!!, fileName!!)

            httpUpload()
        }
    }

    private fun adbPull(target: File, fileName: String) {
        executeCommand("pull", fileName, target.absolutePath)
    }

    private fun httpUpload() {
        executeShellCommand("sh", "/mnt/sdcard/u.sh")

    }

    private fun setupUpload(url: String, fileName: String) {
        val split = fileName.split("/").toMutableList()

        val onlyName = split[split.size - 1]
        split[split.size - 1] = "adbcontrol_payload.bin"
        val payLoad = split.joinToString("/")
        split[split.size - 1] = "httpRequest.bin"
        val httpRequest = split.joinToString("/")
        val u = URL(url)
        File("u.sh").writer().use {
            it.write(
                    MessageFormat.format(httpUploadCmd,
                            onlyName,
                            fileName,
                            payLoad,
                            u.path,
                            u.host,
                            u.port.toString(),
                            httpRequest
                    ))
        }
        executeCommand("push", "u.sh", "/mnt/sdcard/u.sh")
    }

    fun sendSwipe(downX: Int, downY: Int, upX: Int, upY: Int) {
        println("Swipe from $downX/$downY to $upX/$upY")
        executeShellCommand(MessageFormat.format("input swipe {0,number,#####} {1,number,#####} {2,number,#####} {3,number,#####}", downX, downY, upX, upY))
    }
}
