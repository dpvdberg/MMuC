package part2.dining

import Evaluation.ImprovedChecker
import Evaluation.NaiveChecker
import LTS.Parsing.AldebaranParser
import ModalMu.Parsing.ModalMuParser
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import toHMS
import java.io.File

internal class DiningTest {

    private fun getLts(): List<String> {
        return getFileNames().map { name -> DiningTest::class.java.getResource(name).readText() }
    }

    private fun getFileNames(): List<String> {
        return listOf(
            "dining_2.aut",
            "dining_3.aut",
            "dining_4.aut",
            "dining_5.aut",
            "dining_6.aut",
            "dining_7.aut",
            "dining_8.aut",
            "dining_9.aut",
            "dining_10.aut",
            "dining_11.aut"
        )
    }

    private fun getFormulas(): List<String> {
        return getInvariantNames().map { name -> DiningTest::class.java.getResource(name).readText() }
    }

    private fun getInvariantNames(): List<String> {
        return listOf(
            "invariantly_inevitably_eat.mcf",
            "invariantly_plato_starves.mcf",
            "invariantly_possibly_eat.mcf",
            "plato_infinitely_often_can_eat.mcf"
        )
    }

    private fun printResult(
        resultNaive: Boolean,
        naiveMs: Long,
        resultImproved: Boolean,
        improvedMs: Long,
        fileName: String,
        formula: String
    ) {
        File("dining_results.txt")
            .appendText("$fileName, $formula: naive: $resultNaive, time: ${toHMS(naiveMs)} ($naiveMs ms)\r\n")
        File("dining_results.txt")
            .appendText("$fileName, $formula: improved: $resultImproved, time: ${toHMS(improvedMs)} ($improvedMs ms)\n")
    }

    @Test
    @Disabled
    fun modalCheckerTest() {
        val lts = getLts()
        val formula = getFormulas()

        val fileNames = getFileNames()

        val invNames = getInvariantNames()

        for ((i, l) in lts.withIndex()) {
            val parsedLTS = AldebaranParser.parse(l.lineSequence())
            for ((j, f) in formula.withIndex()) {
                val parsedFormula = ModalMuParser.parse(f)

                val (resultNaive, naiveMs) = NaiveChecker().checkTimed(parsedLTS, parsedFormula)
                val (resultImproved, improvedMs) = ImprovedChecker().checkTimed(parsedLTS, parsedFormula)

                print("result of ${fileNames[i]} with ${invNames[j]} is $resultNaive and $resultImproved")

                printResult(resultNaive, naiveMs, resultImproved, improvedMs, fileNames[i], invNames[j])
            }
        }
    }


}