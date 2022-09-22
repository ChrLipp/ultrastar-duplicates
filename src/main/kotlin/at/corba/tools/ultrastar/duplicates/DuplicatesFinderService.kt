package at.corba.tools.ultrastar.duplicates

import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors
import kotlin.io.path.absolutePathString
import kotlin.io.path.name

@Component
class DuplicatesFinderService {
    /** The logger */
    private val log = KotlinLogging.logger {}

    fun process(checkDir: Path, sourceDir: Path) {
        val filesToCheck = Files.walk(checkDir)
            .filter { Files.isRegularFile(it) }
            .filter { it.toString().lowercase().endsWith(".txt") }
            .collect(Collectors.toMap(Path::name, Path::absolutePathString) {
                    path1, path2 -> "$path1, $path2"
            })

        val filesAvailableSongs = Files.walk(sourceDir)
            .filter { !it.toAbsolutePath().toString().startsWith(checkDir.toAbsolutePath().toString()) }
            .filter { Files.isRegularFile(it) }
            .filter { it.toString().lowercase().endsWith(".txt") }
            .collect(Collectors.toMap(Path::name, Path::absolutePathString) {
                    path1, path2 -> "$path1, $path2"
            })

        filesToCheck.forEach { (name, path) ->
            if (filesAvailableSongs.containsKey(name)) {
                val otherPath = filesAvailableSongs.get(name)
                val otherPathList = otherPath?.split(", ")
                log.info(name)
                log.info("- $path")
                otherPathList?.forEach {
                    log.info("- $it")
                }
                log.info("---")
            }
        }
    }
}