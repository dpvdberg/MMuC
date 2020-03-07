package part2.dining

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
        file: String,
        resultNaive: Boolean,
        naiveMs: Long,
        naiveIteration: Int,
        resultImproved: Boolean,
        improvedMs: Long,
        improvedIteration: Int,
        fileName: String,
        formula: String
    ) {
        File(file)
            .appendText("$fileName,$formula,naive,$resultNaive,${toHMS(naiveMs)},$naiveMs,$naiveIteration\r\n")
        File(file)
            .appendText("$fileName,$formula,improved,$resultImproved,${toHMS(improvedMs)},$improvedMs,$improvedIteration\n")
    }

    @Test
    @Disabled
    fun modalCheckerTest() {
        val resultFile = SimpleDateFormat("'dining_result.'yyyyMMddHHmm'.csv'").format(Date())

        val lts = getLts()
        val formula = getFormulas()

        val fileNames = getFileNames()

        val invNames = getInvariantNames()

        for ((i, l) in lts.withIndex()) {
            val parsedLTS = AldebaranParser.parse(l.lineSequence())
            for ((j, f) in formula.withIndex()) {
                val parsedFormula = ModalMuParser.parse(f)

                val naive = NaiveChecker()
                val (resultNaive, naiveMs) = naive.checkTimed(parsedLTS, parsedFormula)

                val improved = ImprovedChecker()
                val (resultImproved, improvedMs) = improved.checkTimed(parsedLTS, parsedFormula)


                println("result of ${fileNames[i]} with ${invNames[j]} is $resultNaive and $resultImproved")

                printResult(
                    resultFile,
                    resultNaive,
                    naiveMs,
                    naive.iteration,
                    resultImproved,
                    improvedMs,
                    improved.iteration,
                    fileNames[i],
                    invNames[j]
                )
            }
        }
    }


}