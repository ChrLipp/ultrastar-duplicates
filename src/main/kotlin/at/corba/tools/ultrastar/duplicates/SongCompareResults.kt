package at.corba.tools.ultrastar.duplicates

import at.corba.tools.ultrastar.duplicates.FileCompareResults.BOTH_FILES_ARE_NOT_EXISTING

class SongCompareResults(
    var txtCompareResult : FileCompareResults = BOTH_FILES_ARE_NOT_EXISTING,
    var mp3CompareResult : FileCompareResults = BOTH_FILES_ARE_NOT_EXISTING,
    var videoCompareResult : FileCompareResults = BOTH_FILES_ARE_NOT_EXISTING,
    var backgroundCompareResult : FileCompareResults = BOTH_FILES_ARE_NOT_EXISTING,
    var coverCompareResult : FileCompareResults = BOTH_FILES_ARE_NOT_EXISTING
) {
    fun setCompareResult(attribute: String, compareResults: FileCompareResults) {
        when (attribute) {
            "TXT" -> txtCompareResult = compareResults
            "MP3" -> mp3CompareResult = compareResults
            "VIDEO" -> videoCompareResult = compareResults
            "BACKGROUND" -> backgroundCompareResult = compareResults
            "COVER" -> coverCompareResult = compareResults
        }
    }
}

enum class FileCompareResults(val index: Int) {
    BOTH_FILES_ARE_NOT_EXISTING(0),
    FIRST_FILE_IS_NOT_EXISTING(1),
    SECOND_FILE_IS_NOT_EXISTING(2),
    FILES_ARE_EQUAL(3),
    FILES_AFRE_DIFFERENT(4)
}
