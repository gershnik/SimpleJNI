import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
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


class JniGenTest {

    @Tag("GENERATOR")
    @EnabledIfEnvironmentVariable(named = "JNIGEN_ENABLE_TEST_GENERATION", matches = "true")
    @ParameterizedTest(name = "generateOutput: {0}")
    @MethodSource("inputDirectories")
    fun generateOutput(name: String, dir: Path) {
        val sources = Files.list(dir / "input")
            .filter { setOf("kt", "java").contains(it.extension)}
            .map { SourceFile.fromPath(it.toFile()) }.toList()

        val args = loadArguments(dir / "args.properties")
        val workingDir = Path(System.getProperty("test.working.dir")!!) / "generateOutput" / name
        val cppPath = workingDir / "cpp"
        workingDir.toFile().deleteRecursively()
        val result = compileFiles(workingDir, cppPath, sources, args)

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        listPath(dir / "kapt-output").forEach { it.toFile().deleteRecursively() }
        listPath(cppPath)
            .filter { setOf("h", "cpp", "txt").contains(it.extension) }
            .forEach {
                Files.createDirectories(dir / "kapt-output")
                Files.copy(it, dir / "kapt-output" / it.fileName)
            }
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
        listPath(dir / "kapt-output")
            .filter {setOf("h", "cpp", "txt").contains(it.extension) }
            .forEach {
                assertFileContent(cppPath/it.fileName, Files.readString(it))
            }
//        Files.readAllLines(cppPath / "outputs.txt").forEach {
//            assertThat(result.messages, containsString("v: [ksp] JNIGen: Generating $it:  written\n"))
//        }
    }

    companion object {
        val testDataLocation = Path(System.getProperty("test.data.location")!!)

        @JvmStatic
        fun inputDirectories(): Stream<Arguments> {
            val sourcesDir = testDataLocation / "sources"
            return Files.list(sourcesDir)
                .filter { it.isDirectory() }
                .map { Arguments.of(it.fileName.toString(), it) }
        }
    }
}