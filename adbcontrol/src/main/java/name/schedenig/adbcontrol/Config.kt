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

import java.io.IOException
import java.io.InputStream
import java.util.Properties

class Config () {

    var adbCommand: String? = null
    var screenshotDelay: Long = 0
    var localImageFilePath: String? = null
    var phoneImageFilePath: String? = null
    var httpUpload: String? = null

    @Throws(IOException::class)
    fun load(`in`: InputStream) {
        val properties = Properties()
        properties.load(`in`)
        adbCommand = properties.getProperty("adbCommand")
        screenshotDelay = java.lang.Long.parseLong(properties.getProperty("screenshotDelay"))
        localImageFilePath = properties.getProperty("localImageFilePath")
        phoneImageFilePath = properties.getProperty("phoneImageFilePath")
        httpUpload = properties.getProperty("httpUpload")
    }
}
