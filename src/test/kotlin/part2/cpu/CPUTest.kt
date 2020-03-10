package part2.cpu

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

internal class CPUTest {

    private fun getLts(): List<String> {
        return getFileNames().map { name -> CPUTest::class.java.getResource(name).readText() }
    }

    private fun getFileNames(): List<String> {
        return listOf(
            "german_linear_2.1.aut",
            "german_linear_3.1.aut",
            "german_linear_4.1.aut",
            "german_linear_5.1.aut"
        )
    }

    private fun getFormulas(): List<String> {
        return getInvariantNames().map { name -> CPUTest::class.java.getResource(name).readText() }
    }

    private fun getInvariantNames(): List<String> {
        return listOf(
            "infinite_run_no_access.mcf",
            "infinitely_often_exclusive.mcf",
            "invariantly_eventually_fair_shared_access.mcf",
            "invariantly_inevitably_exclusive_access.mcf",
            "invariantly_possibly_exclusive_access.mcf"
        )
    }

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

    @Test
    @Disabled
    fun modalCheckerTest() {
        val resultFile = SimpleDateFormat("'cpu_results.'yyyyMMddHHmm'.csv'").format(Date())

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