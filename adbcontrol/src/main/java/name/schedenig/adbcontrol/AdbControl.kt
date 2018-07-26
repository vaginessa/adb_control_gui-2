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

import java.awt.BorderLayout
import java.io.File
import java.io.FileInputStream
import java.io.IOException

import javax.swing.JFrame

class AdbControl @Throws(IOException::class)
constructor(configFile: File) : JFrame() {
    init {
        val config = Config()

        FileInputStream(configFile).use { `in` -> config.load(`in`) }

        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        title = "ADB Control"
        setSize(720 / 3, 1080 / 3)

        val panel = AdbControlPanel(config)
        panel.adbHelper = AdbHelper(config)
        contentPane.add(panel, BorderLayout.CENTER)
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val configFile: File

            if (args.size == 0) {
                configFile = File("config.properties")
            } else {
                configFile = File(args[0])
            }

            try {
                val frame = AdbControl(configFile)
                frame.isVisible = true
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }
    }
}
