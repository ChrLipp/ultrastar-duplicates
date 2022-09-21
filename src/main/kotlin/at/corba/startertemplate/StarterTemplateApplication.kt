package at.corba.startertemplate

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import picocli.CommandLine
import kotlin.system.exitProcess

@SpringBootApplication
class StarterTemplateApplication(
    private val commandLineParameter: StarterTemplateParameter
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        CommandLine(commandLineParameter).execute(*args)
    }
}

fun main(args: Array<String>) {
    val context = runApplication<StarterTemplateApplication>(*args)
    exitProcess(SpringApplication.exit(context))
}
