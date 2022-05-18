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

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.`is` as Is
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import smjni.jnigen.Processor
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.stream.Stream
import kotlin.io.path.exists

fun compileFiles(workingDir: Path,
                 cppPath: Path,
                 sources: List<SourceFile>,
                 processorArgs: Map<String, String> = emptyMap()) : KotlinCompilation.Result  {

    return KotlinCompilation().apply {
        this.workingDir = workingDir.toFile()
        this.sources = sources

        annotationProcessors = listOf(Processor())

        kaptArgs =  processorArgs.toMutableMap().apply {
            this.putIfAbsent("smjni.jnigen.dest.path", "$cppPath")
            this.putIfAbsent("smjni.jnigen.print.to.stdout", "false")
        }

        inheritClassPath = true
        messageOutputStream = System.out // see diagnostics in real time
        verbose = false
    }.compile()
}

fun assertFileContent(path: Path, content: String) {
    assertTrue(Files.exists(path))
    assertThat(Files.readString(path), Is(equalTo(content)))
}

fun loadArguments(argsPath: Path) : Map<String, String> {
    return argsPath.takeIf { it.exists() }?.let { path->
        Properties().apply {
            Files.newBufferedReader(path).use { load(it) }
        }.entries.associate { (key, value) -> Pair("$key", "$value") }
    } ?: emptyMap()
}

fun listPath(path: Path): Stream<Path> {
    if (Files.exists(path))
        return Files.list(path)
    return Stream.empty()
}

fun listFile(path: Path): List<String> {
    return if (Files.exists(path))
        Files.readAllLines(path)
    else
        emptyList()
}

fun collectOutput(result: KotlinCompilation.Result) : List<String> {
    return result.messages.lines().mapNotNull {
        if (it.startsWith("i: Note: JNIGen:"))
            it.removePrefix("i: Note: ")
        else if (it.startsWith("e: Note: JNIGen:"))
            it.removePrefix("e: Note: ")
        else
            null
    }
}

