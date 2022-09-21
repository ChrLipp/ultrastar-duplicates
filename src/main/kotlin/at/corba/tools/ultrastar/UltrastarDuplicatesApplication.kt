package at.corba.tools.ultrastar

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import picocli.CommandLine
import kotlin.system.exitProcess

@SpringBootApplication
class UltrastarDuplicatesApplication(
    private val commandLineParameter: UltrastarDuplicatesParameter
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        CommandLine(commandLineParameter).execute(*args)
    }
}

fun main(args: Array<String>) {
    val context = runApplication<UltrastarDuplicatesApplication>(*args)
    exitProcess(SpringApplication.exit(context))
}
