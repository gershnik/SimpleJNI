/*
 Copyright 2021 SmJNI Contributors

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

import com.google.devtools.ksp.KspExperimental
import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.configureKsp
import com.tschuchort.compiletesting.kspProcessorOptions
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.`is` as Is
import org.hamcrest.MatcherAssert.assertThat
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Assertions.assertTrue
import smjni.jnigen.ksp.Provider
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.stream.Stream
import kotlin.io.path.exists

@OptIn(ExperimentalCompilerApi::class)
@KspExperimental
fun compileFiles(workingDir: Path,
                 cppPath: Path,
                 sources: List<SourceFile>,
                 processorArgs: Map<String, String> = emptyMap()) : JvmCompilationResult  {

    return KotlinCompilation().apply {
        this.workingDir = workingDir.toFile()
        this.sources = sources

        configureKsp(useKsp2 = true) {
            symbolProcessorProviders += listOf(Provider())
            kspProcessorOptions = processorArgs.toMutableMap().apply {
                this.putIfAbsent("smjni.jnigen.dest.path", "$cppPath")
            }
        }

        inheritClassPath = true
        messageOutputStream = System.out
        verbose = false
    }.compile()
}

fun assertFileContent(path: Path, content: List<String>) {
    assertTrue(Files.exists(path))
    assertThat(path.toFile().readLines(), Is(equalTo(content)))
}

fun loadArguments(argsPath: Path) : Map<String, String> {
    return argsPath.takeIf { it.exists() }?.let { path->
        Properties().apply {
            Files.newBufferedReader(path).use { load(it) }
        }.entries.associate { (key, value) -> Pair("$key", "$value") }
    } ?: emptyMap()
}

fun listPath(path: Path): Stream<Path> {
    return if (Files.exists(path))
        Files.list(path)
    else
        Stream.empty()
}

fun listFile(path: Path): List<String> {
    return if (Files.exists(path))
        Files.readAllLines(path)
    else
        emptyList()
}

@OptIn(ExperimentalCompilerApi::class)
fun collectOutput(result: JvmCompilationResult) : List<String> {
    return result.messages.lines().mapNotNull {
        if (it.startsWith("i: [ksp] JNIGen:"))
            it.removePrefix("i: [ksp] ")
        else
            null
    }
}