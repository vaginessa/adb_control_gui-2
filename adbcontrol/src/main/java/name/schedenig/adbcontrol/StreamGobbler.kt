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
import java.io.OutputStream

class StreamGobbler(private val `in`: InputStream, private val out: OutputStream?) : Thread() {

    override fun run() {
        val buffer = ByteArray(8192)

        try {
            while (true) {
                if (Thread.interrupted()) {
                    break
                }

                val count = `in`.read(buffer)

                if (count < 0) {
                    break
                }

                if (count > 0 && out != null) {
                    out.write(buffer, 0, count)
                }
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

    }
}
