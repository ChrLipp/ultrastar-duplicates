package at.corba.tools.ultrastar.duplicates

import at.corba.tools.ultrastar.duplicates.libs.FileVersionProvider
import at.corba.tools.ultrastar.duplicates.logic.DuplicatesFinderService
import org.springframework.stereotype.Component
import picocli.CommandLine
import java.nio.file.Path
import java.util.concurrent.Callable

/**
 * Define your options here. Remove the example option.
 */
@CommandLine.Command(
    name = "Template",
    description = ["Description"],
    mixinStandardHelpOptions = true,
    versionProvider = FileVersionProvider::class)
@Component
class UltrastarDuplicatesCommand(
    private val duplicatesService: DuplicatesFinderService
) : Callable<Int> {
    @CommandLine.ArgGroup(exclusive = false)
    lateinit var dependent: Dependent

    class Dependent {
        @CommandLine.Option(
            names = ["-c", "--check"],
            description = ["Directory to check for duplicate"],
            required = true
        )
        lateinit var checkDir : Path

        @CommandLine.Option(
            names = ["-s", "--source"],
            description = ["Source directory"],
            required = true
        )
        lateinit var sourceDir : Path
    }

    /**
     * Place your application logic entry point here.
     */
    override fun call(): Int {
        duplicatesService.process(dependent.checkDir, dependent.sourceDir)
        return 0
    }
}