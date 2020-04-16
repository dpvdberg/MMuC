import Evaluation.ImprovedChecker
import Evaluation.NaiveChecker
import Exception.ParsingException
import LTS.Parsing.AldebaranParser
import ModalMu.ModalFormula
import ModalMu.Parsing.ModalMuParser
import com.andreapivetta.kolor.red
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.file
import java.util.concurrent.TimeUnit


fun main(args: Array<String>) = MMuC()
    .main(args)

enum class EvaluationMethod {
    NAIVE,
    IMPROVED
}

fun printlndbg(message: Any?) {
    if (MMuC.verbose) {
        println(message)
    }
}

fun printdbg(message: Any?) {
    if (MMuC.verbose) {
        print(message)
    }
}


fun toHMS(units : Long, base : TimeUnit = TimeUnit.MILLISECONDS) : String {
    return java.lang.String.format(
        "%02dh:%02dm:%02ds", base.toHours(units),
        base.toMinutes(units) % TimeUnit.HOURS.toMinutes(1),
        base.toSeconds(units) % TimeUnit.MINUTES.toSeconds(1)
    )
}

fun toHMSandMs(ms : Long) : String {
    return toHMS(ms) + " ($ms ms)"
}

class MMuC : CliktCommand() {
    private val extension = ".mcf"

    private val ltsFile by argument("LTS_PATH", help = "The path to the LTS file in Aldebaran format").file(
        mustExist = true,
        mustBeReadable = true,
        canBeDir = false,
        canBeFile = true
    )

    private val method by option("-m", "--method", help = "Evaluation method, default: improved").choice(
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
    private val timer by option(
        "-t",
        "--timer",
        help = "Enable a timer for each formula"
    ).flag()

    companion object {
        var verbose = false
    }

    override fun run() {
        MMuC.verbose = verbose

        println("Reading LTS file...")
        val lts = AldebaranParser.parse(ltsFile.readText().lineSequence())

        println("Successfully parsed LTS file.")

        println("Reading and parsing formulas...")
        val formulasWithName = mutableListOf<Pair<ModalFormula, String>>()
        try {
            formulasWithName += formulas.mapIndexed { i, s -> Pair(ModalMuParser.parse(s), "Textual formula $i") }
            formulasWithName += formulaFiles.map { f ->
                Pair(
                    ModalMuParser.parse(f.readText()),
                    "Formula file: ${f.name}"
                )
            }

            formulaDir
                ?.listFiles { _, fileName -> fileName.toLowerCase().endsWith(extension) }
                ?.forEach { f ->
                    formulasWithName += Pair(
                        ModalMuParser.parse(f.readText()),
                        "Formula file in directory: ${f.name}"
                    )
                }

            if (autoFindFormulas) {
                ltsFile.parentFile
                    .listFiles { _, fileName -> fileName.toLowerCase().endsWith(extension) }
                    ?.forEach { f ->
                        formulasWithName += Pair(
                            ModalMuParser.parse(f.readText()),
                            "Formula file found in LTS dir: ${f.name}"
                        )
                    }
            }
        } catch (e : ParsingException) {
            println("Stopping due to parsing errors.".red())
            return
        }

        if (formulasWithName.size <= 0) {
            println("There are no formulas to check.".red())
            println("Use one or more flags to add formulas to check, see the help section (--help flag)".red())
            return
        }
        println("Read a total of ${formulasWithName.size} formulas.")

        println("Using $method evaluation method.")
        val formulaChecker = when(method) {
            EvaluationMethod.NAIVE -> NaiveChecker()
            EvaluationMethod.IMPROVED -> ImprovedChecker()
        }

        formulasWithName.forEachIndexed { index, formula ->
            run {
                println("formula $index: ${formula.second}")
                if (timer) {
                    val (result, msElapsed) = formulaChecker.checkTimed(lts, formula.first)
                    val hms = toHMSandMs(msElapsed)
                    println("Result of formula: $result")
                    println("Elapsed time: $hms")
                } else {
                    val result = formulaChecker.check(lts, formula.first)
                    println("Result of formula: $result")
                }
                println()
            }
        }
    }
}