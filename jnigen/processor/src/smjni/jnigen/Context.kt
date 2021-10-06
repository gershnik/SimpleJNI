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

import java.io.File
import javax.annotation.processing.ProcessingEnvironment
import javax.tools.Diagnostic


internal class Context(env: ProcessingEnvironment) {

    private enum class Options(val externalName: String, val defaultValue: String) {
        DEST_PATH("smjni.jnigen.dest.path", "jnigen"),
        OWN_DEST_PATH("smjni.jnigen.own.dest.path", "false"),
        EXPOSE_EXTRA("smjni.jnigen.expose.extra", ""),
        TYPE_HEADER_NAME("smjni.jnigen.type.header.name", "type_mapping.h"),
        ALL_HEADER_NAME("smjni.jnigen.all.header.name", "all_classes.h"),
        OUTPUT_LIST_NAME("smjni.jnigen.output.list.name", "outputs.txt"),
        EXPOSE_ANNOTATION_NAME("smjni.jnigen.expose.annotation.name", "smjni.jnigen.ExposeToNative"),
        CALLED_ANNOTATION_NAME("smjni.jnigen.called.annotation.name", "smjni.jnigen.CalledByNative"),
        CTOR_NAME("smjni.jnigen.ctor.name", "ctor"),
        PRINT_TO_STDOUT("smjni.jnigen.print.to.stdout", "true");

        fun extract(env: ProcessingEnvironment): String {

            return env.options[externalName] ?: defaultValue
        }
    }

    val elementUtils = env.elementUtils!!
    val messager = env.messager!!
    val destPath: String = File(Options.DEST_PATH.extract(env)).absolutePath
    val ownDestPath = Options.OWN_DEST_PATH.extract(env) == "true"
    val exposeExtra: Map<String, String>
    val headerName = Options.TYPE_HEADER_NAME.extract(env)
    val allHeaderName = Options.ALL_HEADER_NAME.extract(env)
    val outputListName = Options.OUTPUT_LIST_NAME.extract(env)
    val exposedAnnotation = Options.EXPOSE_ANNOTATION_NAME.extract(env)
    val calledByNativeAnnotation = Options.CALLED_ANNOTATION_NAME.extract(env)
    val ctorName = Options.CTOR_NAME.extract(env)
    private val printToStdOut = Options.PRINT_TO_STDOUT.extract(env) == "true"

    init {

        val exposedRegex = Regex("""([^(]+)(?:\(([^)]+)\))?""")
        val exposeMap = HashMap<String, String>()
        for (exposed in Options.EXPOSE_EXTRA.extract(env).split(';').filter{ it.trim().isNotEmpty() }) {

            val match = exposedRegex.matchEntire(exposed)
            if (match != null) {
                val javaName = match.groups[1]!!.value
                val stem = match.groups[2]?.value ?: ""
                exposeMap[javaName] = stem
            }
        }
        exposeExtra = exposeMap
    }

    internal fun getSupportedOptions(): MutableSet<String> {

        return Options.values().map{it.externalName}.toMutableSet()
    }

    fun print(message: String) {
        if (printToStdOut)
            println(message)
        else
            messager.printMessage(Diagnostic.Kind.NOTE, message)
    }

}