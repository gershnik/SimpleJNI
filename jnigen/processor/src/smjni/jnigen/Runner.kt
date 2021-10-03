/*
 Copyright 2014 Smartsheet Inc.
 Copyright 2019 SmJNI Contributors

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

package smjni.jnigen

import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.collections.ArrayList
import com.sun.tools.javac.Main as Javac

object Runner {

    @JvmStatic
    fun main(args: Array<String>) {

        try {
            val javacArgs = ArrayList<String>()
            javacArgs.add("-proc:only")
            javacArgs.add("-processor")
            javacArgs.add(Processor::javaClass.name)

            val files = ArrayList<String>()
            for(i in args.indices) {

                javacArgs.add(args[i])
                if (args[i] == "-sourcepath" && i < args.size - 1) {
                    Files.walk(Paths.get(args[i + 1])).forEach{
                        if (Files.isRegularFile(it) && it.toString().lowercase(Locale.getDefault()).endsWith(".java"))
                                files.add(it.toAbsolutePath().toString())
                    }
                }
            }

            javacArgs += files

            Javac.main(javacArgs.toTypedArray())
        } catch (ex: Exception) {
            throw RuntimeException(ex)
        }
    }

}
