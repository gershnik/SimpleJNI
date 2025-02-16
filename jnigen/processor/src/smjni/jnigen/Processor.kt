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

import java.util.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.*
import javax.tools.Diagnostic

@Suppress("unused")
class Processor: javax.annotation.processing.Processor  {

    override fun init(processingEnv: ProcessingEnvironment?) {
        _env = processingEnv
    }

    override fun getSupportedOptions(): MutableSet<String> {

        return _context.getSupportedOptions()
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(_context.exposedAnnotation)
    }

    override fun getCompletions(element: Element?, annotation: AnnotationMirror?, member: ExecutableElement?, userText: String?): MutableIterable<Completion> {
        return Collections.emptyList()
    }

    override fun process(annotations: MutableSet<out TypeElement>?, env: RoundEnvironment?): Boolean {

        if (annotations == null || annotations.size == 0)
            return true

        try {
            val typeMap = TypeMap(_context, env!!)

            val generator = Generator()

            generator.generate(typeMap, _context)

        } catch (ex: ProcessingException) {

            _context.messager.printMessage(Diagnostic.Kind.ERROR, ex.message, ex.element)
        }

        return true
    }

    private var _env: ProcessingEnvironment? = null

    private val _context: Context by lazy(LazyThreadSafetyMode.NONE) {
        Context(_env!!)
    }
}