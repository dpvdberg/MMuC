import Evaluation.ImprovedChecker
import Evaluation.NaiveChecker
import LTS.Parsing.AldebaranParser
import ModalMu.Parsing.ModalMuParser
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.file

fun main(args: Array<String>) = ModalMuClikt().main(args)

enum class EvaluationMethod {
    NAIVE,
    IMPROVED
}

fun printlndbg(message: Any?) {
    if (ModalMuClikt.verbose) {
        println(message)
    }
}

fun printdbg(message: Any?) {
    if (ModalMuClikt.verbose) {
        print(message)
    }
}

class ModalMuClikt : CliktCommand() {
    private val extension = ".mcf"

    private val ltsFile by argument("LTS_PATH", help = "The path to the LTS file in Aldebaran format").file(
        mustExist = true,
        mustBeReadable = true,
        canBeDir = false,
        canBeFile = true
    )

    private val method by option("-m", "--method", help = "Evaluation method, default: optimized").choice(
        mapOf(
            "naive" to EvaluationMethod.NAIVE,
            "improved" to EvaluationMethod.IMPROVED
        ),
        ignoreCase = true
    ).default(EvaluationMethod.IMPROVED)
    private val formulas by option("-f", "--formula", help = "Modal Mu Formula(s) to check, can be multiple").multiple()
    private val formulaFiles by option(
        "-fp",
        "--formula-path",
        help = "Path to Modal Mu Formula file to check, can be multiple"
    ).file(mustExist = true, mustBeReadable = true, canBeDir = false, canBeFile = true).multiple()
    private val formulaDir by option(
        "-fd",
        "--formula-dir",
        help = "Path to directory that contain Modal Mu Formula files to check, formula files should have $extension extension"
    ).file(mustExist = true, mustBeReadable = true, canBeDir = true, canBeFile = false)
    private val autoFindFormulas by option(
        "-a",
        "--auto",
        help = "Automatically find formulas in the directory of the LTS file, formula files should have $extension extension"
    ).flag()
    private val verbose by option(
        "-v",
        "--verbose",
        help = "Verbose printing, such as evaluation iteration count"
    ).flag()

    companion object {
        var verbose = false
    }

    override fun run() {
        ModalMuClikt.verbose = verbose

        println("Reading LTS file...")
        val lts = AldebaranParser.parse(ltsFile.readText().lineSequence())

        println("Successfully parsed LTS file.")

        println("Reading and parsing formulas...")
        val formulas = formulas.map { f -> ModalMuParser.parse(f) }.toMutableList()
        formulas += formulaFiles.map { f -> ModalMuParser.parse(f.readText()) }

        formulaDir
            ?.listFiles { _, fileName -> fileName.toLowerCase().endsWith(extension) }
            ?.forEach { f -> formulas += ModalMuParser.parse(f.readText()) }

        if (autoFindFormulas) {
            ltsFile.parentFile
                .listFiles { _, fileName -> fileName.toLowerCase().endsWith(extension) }
                ?.forEach { f -> formulas += ModalMuParser.parse(f.readText()) }
        }

        println("Read a total of ${formulas.size} formulas.")

        println("Using $method evaluation method.")
        val formulaChecker = when(method) {
            EvaluationMethod.NAIVE -> NaiveChecker()
            EvaluationMethod.IMPROVED -> ImprovedChecker()
        }

        formulas.forEachIndexed { index, formula ->
            run {
                val result = formulaChecker.check(lts, formula)
                println("Result of formula $index : $result")
            }
        }
    }
}