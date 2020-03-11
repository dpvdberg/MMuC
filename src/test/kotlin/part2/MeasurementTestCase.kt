package part2

import Evaluation.ImprovedChecker
import Evaluation.NaiveChecker
import LTS.Parsing.AldebaranParser
import ModalMu.Parsing.ModalMuParser
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import toHMS
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


abstract class MeasurementTestCase {

    fun getLts(): List<String> {
        return getFileNames().map { name -> this::class.java.getResource(name).readText() }
    }

    abstract fun getFileNames(): List<String>

    fun getFormulas(): List<String> {
        return getInvariantNames().map { name -> this::class.java.getResource(name).readText() }
    }

    abstract fun getInvariantNames(): List<String>

    private fun printResult(
        file: String,
        resultNaive: Boolean,
        naiveNs: Long,
        naiveIteration: Int,
        resultImproved: Boolean,
        improvedNs: Long,
        improvedIteration: Int,
        fileName: String,
        formula: String
    ) {
        File(file)
            .appendText(
                "$fileName,$formula,naive,$resultNaive,${toHMS(
                    naiveNs,
                    TimeUnit.NANOSECONDS
                )},$naiveNs,$naiveIteration\r\n"
            )
        File(file)
            .appendText(
                "$fileName,$formula,improved,$resultImproved,${toHMS(
                    improvedNs,
                    TimeUnit.NANOSECONDS
                )},$improvedNs,$improvedIteration\n"
            )
    }

    abstract fun getResultFileFormat() : String

    @Test
    fun modalCheckerTest() {
        val resultFile = SimpleDateFormat(getResultFileFormat()).format(Date())

        val lts = getLts()
        val formula = getFormulas()

        val fileNames = getFileNames()

        val invNames = getInvariantNames()

        for ((i, l) in lts.withIndex()) {
            val parsedLTS = AldebaranParser.parse(l.lineSequence())
            for ((j, f) in formula.withIndex()) {
                val parsedFormula = ModalMuParser.parse(f)

                var naive = NaiveChecker()
                var (resultNaive, naiveNs) = naive.checkTimed(parsedLTS, parsedFormula, false)

                var improved = ImprovedChecker()
                var (resultImproved, improvedNs) = improved.checkTimed(parsedLTS, parsedFormula, false)

                // Fix boot up
                if (i == 0 && j == 0) {
                    naive = NaiveChecker()
                    val naive = naive.checkTimed(parsedLTS, parsedFormula, false)
                    resultNaive = naive.first
                    naiveNs = naive.second

                    improved = ImprovedChecker()
                    val improved = improved.checkTimed(parsedLTS, parsedFormula, false)
                    resultImproved = improved.first
                    improvedNs = improved.second
                }

                println("result of ${fileNames[i]} with ${invNames[j]} is $resultNaive and $resultImproved")

                printResult(
                    resultFile,
                    resultNaive,
                    naiveNs,
                    naive.iteration,
                    resultImproved,
                    improvedNs,
                    improved.iteration,
                    fileNames[i],
                    invNames[j]
                )
            }
        }
    }
}