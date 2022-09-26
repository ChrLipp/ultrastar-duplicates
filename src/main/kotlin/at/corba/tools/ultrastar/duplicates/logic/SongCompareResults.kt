package at.corba.tools.ultrastar.duplicates.logic

import at.corba.tools.ultrastar.duplicates.logic.FileCompareResults.*
import mu.KotlinLogging

class SongCompareResults(
    private var txtCompareResult : FileCompareResults = BOTH_FILES_ARE_NOT_EXISTING,
    private var mp3CompareResult : FileCompareResults = BOTH_FILES_ARE_NOT_EXISTING,
    private var videoCompareResult : FileCompareResults = BOTH_FILES_ARE_NOT_EXISTING,
    private var backgroundCompareResult : FileCompareResults = BOTH_FILES_ARE_NOT_EXISTING,
    private var coverCompareResult : FileCompareResults = BOTH_FILES_ARE_NOT_EXISTING
) {
    /** The logger */
    private val log = KotlinLogging.logger {}

    fun setCompareResult(attribute: String, compareResults: FileCompareResults) {
        when (attribute) {
            "TXT" -> txtCompareResult = compareResults
            "MP3" -> mp3CompareResult = compareResults
            "VIDEO" -> videoCompareResult = compareResults
            "BACKGROUND" -> backgroundCompareResult = compareResults
            "COVER" -> coverCompareResult = compareResults
        }
    }

    fun songsAreIdentical() : Boolean {
        return (
            (txtCompareResult == FILES_ARE_EQUAL) &&
            (mp3CompareResult == FILES_ARE_EQUAL) &&
            (videoCompareResult == FILES_ARE_EQUAL || videoCompareResult == BOTH_FILES_ARE_NOT_EXISTING) &&
            (backgroundCompareResult == FILES_ARE_EQUAL || backgroundCompareResult == BOTH_FILES_ARE_NOT_EXISTING) &&
            (coverCompareResult == FILES_ARE_EQUAL || coverCompareResult == BOTH_FILES_ARE_NOT_EXISTING))
    }

    fun logResult(compareResults: FileCompareResults, extension: String) {
        when (compareResults) {
            BOTH_FILES_ARE_NOT_EXISTING ->
                log.info("- both $extension files are not existing")
            FIRST_FILE_IS_NOT_EXISTING ->
                log.info("- first $extension file is not existing")
            SECOND_FILE_IS_NOT_EXISTING ->
                log.info("- second $extension file is not existing")
            FILES_ARE_EQUAL ->
                log.info("- $extension files are identical")
            FILES_ARE_DIFFERENT ->
                log.info("- $extension files are different")
        }
    }
}

enum class FileCompareResults(val index: Int) {
    BOTH_FILES_ARE_NOT_EXISTING(0),
    FIRST_FILE_IS_NOT_EXISTING(1),
    SECOND_FILE_IS_NOT_EXISTING(2),
    FILES_ARE_EQUAL(3),
    FILES_ARE_DIFFERENT(4)
}
