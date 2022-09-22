package at.corba.tools.ultrastar.duplicates

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import picocli.CommandLine
import kotlin.system.exitProcess

@SpringBootApplication
class UltrastarDuplicatesApplication(
    private val commandLineParameter: UltrastarDuplicatesCommand
) : CommandLineRunner, ExitCodeGenerator {
    /** Variable for passing the exit code */
    private var exitCode: Int = 0

    override fun run(vararg args: String?) {
        exitCode = CommandLine(commandLineParameter).execute(*args)
    }

    override fun getExitCode() : Int {
        return exitCode
    }
}

fun main(args: Array<String>) {
    val context = runApplication<UltrastarDuplicatesApplication>(*args)
    exitProcess(SpringApplication.exit(context))
}
