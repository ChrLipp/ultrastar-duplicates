package at.corba.tools.ultrastar

import at.corba.tools.ultrastar.libs.FileVersionProvider
import mu.KotlinLogging
import org.springframework.stereotype.Component
import picocli.CommandLine

/**
 * Define your options here. Remove the example option.
 */
@CommandLine.Command(
    name = "Template",
    description = ["Description"],
    mixinStandardHelpOptions = true,
    versionProvider = FileVersionProvider::class)
@Component
class UltrastarDuplicatesParameter : Runnable {
    /** The logger */
    private val log = KotlinLogging.logger {}

    @CommandLine.Option(names = ["-o", "--option"], description = ["Provide some option."])
    private var option : String = ""

    /**
     * Place your application logic entry point here.
     */
    override fun run() {
        log.info(option)
    }
}