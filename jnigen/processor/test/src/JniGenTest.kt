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
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.extension
import kotlin.io.path.isDirectory
import kotlin.streams.toList

@OptIn(ExperimentalCompilerApi::class)
class JniGenTest {

    @Tag("GENERATOR")
    @EnabledIfEnvironmentVariable(named = "JNIGEN_ENABLE_TEST_GENERATION", matches = "true")
    @ParameterizedTest(name = "generateOutput: {0}")
    @MethodSource("inputDirectories")
    fun generateOutput(name: String, dir: Path) {
        val sources = Files.list(dir / "input")
            .filter { setOf("kt", "java").contains(it.extension)}
            .sorted(compareBy { it.fileName.toString() })
            .map { SourceFile.fromPath(it.toFile()) }.toList()

        val args = loadArguments(dir / "args.properties")
        val workingDir = Path(System.getProperty("test.working.dir")!!) / "generateOutput" / name
        val cppPath = workingDir / "cpp"
        workingDir.toFile().deleteRecursively()
        val result = compileFiles(workingDir, cppPath, sources, args)
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        val expectedDir = dir / "kapt-output"
        listPath(expectedDir).forEach { it.toFile().deleteRecursively() }
        listPath(cppPath)
            .filter { setOf("h", "cpp", "txt").contains(it.extension) }
            .forEach {
                Files.createDirectories(expectedDir)
                Files.copy(it, expectedDir / it.fileName)
            }

        val outputLines = collectOutput(result)
        Files.write(dir / "kapt-output.txt", outputLines)
    }

    @ParameterizedTest(name = "cleanRun: {0}")
    @MethodSource("inputDirectories")
    fun cleanRun(name: String, dir: Path) {
        val sources = Files.list(dir / "input")
            .filter { setOf("kt", "java").contains(it.extension)}
            .map { SourceFile.fromPath(it.toFile()) }.toList()

        val args = loadArguments(dir / "args.properties")
        val workingDir = Path(System.getProperty("test.working.dir")!!) / "cleanRun" / name
        val cppPath = workingDir / "cpp"
        workingDir.toFile().deleteRecursively()
        val result = compileFiles(workingDir, cppPath, sources, args)
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        val expectedDir = dir / "kapt-output"
        listPath(expectedDir)
            .filter {setOf("h", "cpp", "txt").contains(it.extension) }
            .forEach {
                assertFileContent(cppPath/it.fileName, it.toFile().readLines())
            }
        listPath(cppPath)
            .filter {setOf("h", "cpp", "txt").contains(it.extension) }
            .forEach {
                Assertions.assertTrue(Files.exists(expectedDir / it))
            }

        val outputLines = collectOutput(result)
        val outputSet = outputLines.toSet()

        listFile(cppPath / "outputs.txt").forEach {
            assertThat(outputSet, Matchers.hasItem("JNIGen: Generating $it:  written"))
        }

        assertEquals(listFile(dir / "kapt-output.txt").toSet(), outputSet)
    }

    @ParameterizedTest(name = "incremental: {0}")
    @MethodSource("inputDirectories")
    fun incremental(name: String, dir: Path) {
        val sources = Files.list(dir / "input")
            .filter { setOf("kt", "java").contains(it.extension)}
            .map { SourceFile.fromPath(it.toFile()) }.toList()

        val args = loadArguments(dir / "args.properties")
        val workingDir = Path(System.getProperty("test.working.dir")!!) / "cleanRun" / name
        val cppPath = workingDir / "cpp"
        workingDir.toFile().deleteRecursively()
        var result = compileFiles(workingDir, cppPath, sources, args)
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        result = compileFiles(workingDir, cppPath, sources, args)
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        val outputLines = collectOutput(result)
        val outputSet = outputLines.toSet()

        listFile(cppPath / "outputs.txt").forEach {
            assertThat(outputSet, Matchers.hasItem("JNIGen: Generating $it:  up-to-date"))
        }
    }

    companion object {
        private val testDataLocation = Path(System.getProperty("test.data.location")!!)

        @JvmStatic
        fun inputDirectories(): Stream<Arguments> {
            val sourcesDir = testDataLocation / "sources"
            return Files.list(sourcesDir)
                .filter { it.isDirectory() }
                .map { Arguments.of(it.fileName.toString(), it) }
        }
    }
}