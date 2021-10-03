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
        }

        inheritClassPath = true
        messageOutputStream = System.out // see diagnostics in real time
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

