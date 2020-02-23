import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int

fun main(args: Array<String>) = ModalMuClikt().main(args)

enum class EvaluationMethod {
    NAIVE,
    OPTIMIZED
}

class ModalMuClikt : CliktCommand() {
    val ltsPath by argument("LTS_PATH", help = "The path to the LTS file in Aldebaran format").file(
        mustExist = true,
        mustBeReadable = true
    )

    val method by option("-m", "--method", help = "Evaluation method, default: optimized").choice(
        mapOf(
            "naive" to EvaluationMethod.NAIVE,
            "optimized" to EvaluationMethod.OPTIMIZED
        ),
        ignoreCase = true
    ).default(EvaluationMethod.OPTIMIZED)
    val formulas by option("-f", "--formula", help = "Modal Mu Formula(s) to check, can be multiple").multiple()
    val formulaPaths by option("-fp", "--formula-path", help = "Path to Modal Mu Formula file to check, can be multiple").multiple()

    override fun run() {

    }
}