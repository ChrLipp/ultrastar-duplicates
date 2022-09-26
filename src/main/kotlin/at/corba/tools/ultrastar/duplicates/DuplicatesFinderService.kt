package at.corba.tools.ultrastar.duplicates

import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors
import java.util.zip.CRC32
import kotlin.io.path.absolutePathString
import kotlin.io.path.name

@Component
class DuplicatesFinderService {
    /** The logger */
    private val log = KotlinLogging.logger {}

    /** When testmode is true, nothing will be deleted */
    private var isTestMode = true

    /**
     * Loads both directories and checks if there are duplicates.
     * @args checkDir   Directory to check (might contain duplicates to delete)
     * @args sourceDir  Directory which contains the songs
     */
    fun process(checkDir: Path, sourceDir: Path) {
        val filesToCheck = Files.walk(checkDir)
            .filter { Files.isRegularFile(it) }
            .filter { it.toString().lowercase().endsWith(".txt") }
            .collect(Collectors.toMap(Path::name, Path::absolutePathString) {
                    path1, path2 -> "$path1#$path2"
            })

        val filesAvailableSongs = Files.walk(sourceDir)
            .filter { !it.toAbsolutePath().toString().startsWith(checkDir.toAbsolutePath().toString()) }
            .filter { Files.isRegularFile(it) }
            .filter { it.toString().lowercase().endsWith(".txt") }
            .collect(Collectors.toMap(Path::name, Path::absolutePathString) {
                    path1, path2 -> "$path1#$path2"
            })

        filesToCheck.forEach { (name, path) ->
            if (filesAvailableSongs.containsKey(name)) {
                val pathList = path.split("#").toMutableList()
                pathList.addAll(filesAvailableSongs.getValue(name).split("#"))
                checkForDelete(name, pathList)
            }
        }
    }

    /**
     * Logs song and all file paths, deletes what it can.
     * @args name   Name of the song
     * @args args   List of files for the song
     */
    fun checkForDelete(name: String, args: List<String>) {
        // log what we have
        log.info(name)
        args.forEach {
            log.info("- $it")
        }

        // delete what we can
        if (!checkForEdition(args)) {
            compareEntries(args)
        }

        // close log
        log.info("---")
    }

    /**
     * Checks for an edition (has '[VIDEO]' in it's directory name.
     * This is the best quality, so args[0] can be deleted.
     * @args args   List of files for the song
     */
    private fun checkForEdition(args: List<String>) : Boolean {
        if (hasVideoInDir(args)) {
            val deleteDir = File(args[0]).parentFile

            if (isTestMode) {
                log.info("- Would delete '${deleteDir.absolutePath}'")
            }
            else {
                log.info("- Deleting '${deleteDir.absolutePath}'")
                if (!deleteDir.deleteRecursively()) {
                    log.error("  *** Problems while deleting '${deleteDir.absolutePath}'")
                }
            }
            return true
        }
        else
            return false
    }

    /**
     * Checks indizes 1..n it it is an edition.
     * @args args   List of files for the song
     * @return true if an edition is available
     */
    fun hasVideoInDir(args: List<String>) : Boolean {
        var isFirst = true
        for (entry in args) {
            if (isFirst) {
                isFirst = false
                continue
            }
            if (entry.contains("[VIDEO]")) {
                return true
            }
        }
        return false
    }

    private fun compareEntries(args: List<String>) {
//        val result = SongCompareResults()
//
       // check if TXT files are identically
        val txtCompareResult = areFilesIdentically(args[0], args[1])
        logResult(txtCompareResult, "TXT")

        // check for MP3
        val mp3Index0 = getSongAttribute(args[0], "MP3")
        val mp3Index1 = getSongAttribute(args[1], "MP3")

        val mp3CompareResult = areFilesIdentically(mp3Index0, mp3Index1)
        logResult(mp3CompareResult, "MP3")

        // check for VIDEO
        val videoIndex0 = getSongAttribute(args[0], "VIDEO")
        val videoIndex1 = getSongAttribute(args[1], "VIDEO")

        val videoCompareResult = areFilesIdentically(videoIndex0, videoIndex1)
        logResult(videoCompareResult, "VIDEO")

        // check for BACKGROUND
        val backgroundIndex0 = getSongAttribute(args[0], "BACKGROUND")
        val backgroundIndex1 = getSongAttribute(args[1], "BACKGROUND")

        val backgroundCompareResult = areFilesIdentically(backgroundIndex0, backgroundIndex1)
        logResult(backgroundCompareResult, "BACKGROUND")

        // check for COVER
        val coverIndex0 = getSongAttribute(args[0], "COVER")
        val coverIndex1 = getSongAttribute(args[1], "COVER")

        val coverCompareResult = areFilesIdentically(coverIndex0, coverIndex1)
        logResult(coverCompareResult, "COVER")
    }

    fun compareEntry(args: List<String>, attribute: String) {
        val index0 = getSongAttribute(args[0], attribute)
        val index1 = getSongAttribute(args[1], attribute)

        val compareResult = areFilesIdentically(index0, index1)
        logResult(compareResult, attribute)
    }

    fun getCRC(file : File) : Long {
        val crc32 = CRC32()
        crc32.update(Files.readAllBytes(file.toPath()))
        return crc32.value
    }

    fun areFilesIdentically(filePathAsString1 : String, filePathAsString2 : String) : FileCompareResults {
        val file1 = File(filePathAsString1)
        val file2 = File(filePathAsString2)

        var state: Int = 0
        if (filePathAsString1.isNotEmpty() && file1.exists()) {
            state = 1
        }
        if (filePathAsString2.isNotEmpty() && file2.exists()) {
            state += 2
        }

        when (state) {
            0 -> return FileCompareResults.BOTH_FILES_ARE_NOT_EXISTING
            1 -> return FileCompareResults.SECOND_FILE_IS_NOT_EXISTING
            2 -> return FileCompareResults.FIRST_FILE_IS_NOT_EXISTING
        }

        val firstCRC = getCRC(file1)
        val secondCRC = getCRC(file2)

        if (firstCRC == secondCRC) {
            return FileCompareResults.FILES_ARE_EQUAL
        } else {
            return FileCompareResults.FILES_AFRE_DIFFERENT
        }
    }

    fun getSongAttribute(filePathAsString : String, attribute : String) : String {
        var returnValue = ""
        File(filePathAsString).forEachLine { line ->
            if (line.trim().startsWith("#")) {
                val prefix = "#${attribute.uppercase()}:"
                if (line.trim().startsWith(prefix)) {
                    returnValue = line.removePrefix(prefix)
                    return@forEachLine
                }
            } else {
                return@forEachLine
            }
        }
        if (returnValue.isNotEmpty()) {
            returnValue = File(File(filePathAsString).parent, returnValue).absolutePath
        }
        return returnValue
    }

    fun logResult(compareResults: FileCompareResults, extension: String) {
        when (compareResults) {
            FileCompareResults.BOTH_FILES_ARE_NOT_EXISTING ->
                log.info("- both $extension files are not existing")
            FileCompareResults.FIRST_FILE_IS_NOT_EXISTING ->
                log.info("- first $extension file is not existing")
            FileCompareResults.SECOND_FILE_IS_NOT_EXISTING ->
                log.info("- second $extension file is not existing")
            FileCompareResults.FILES_ARE_EQUAL ->
                log.info("- $extension files are identical")
            FileCompareResults.FILES_AFRE_DIFFERENT ->
                log.info("- $extension files are different")
        }
    }
}
