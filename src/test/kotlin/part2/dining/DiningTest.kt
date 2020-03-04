package part2.dining

import Evaluation.ImprovedChecker
import Evaluation.NaiveChecker
import Facade.ModalMuFacade
import LTS.Parsing.AldebaranParserTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File

internal class DiningTest {

    private fun getLts() : List<String> {
        return listOf(
            DiningTest::class.java.getResource("dining_2.aut").readText(),
            DiningTest::class.java.getResource("dining_3.aut").readText(),
            DiningTest::class.java.getResource("dining_4.aut").readText(),
            DiningTest::class.java.getResource("dining_5.aut").readText(),
            DiningTest::class.java.getResource("dining_6.aut").readText(),
            DiningTest::class.java.getResource("dining_7.aut").readText(),
            DiningTest::class.java.getResource("dining_8.aut").readText(),
            DiningTest::class.java.getResource("dining_9.aut").readText(),
            DiningTest::class.java.getResource("dining_10.aut").readText(),
            DiningTest::class.java.getResource("dining_11.aut").readText()
        )
    }

    private fun getFileNames() : List<String> {
        return listOf<String>("dining_2.aut", "dining_3.aut", "dining_4.aut", "dining_5.aut", "dining_6.aut", "dining_7.aut", "dining_8.aut", "dining_9.aut", "dining_10.aut", "dining_11.aut"
            )
    }

    private fun getFormulas() : List<String> {
        return listOf(
            DiningTest::class.java.getResource("invariantly_inevitably_eat.mcf").readText(),
            DiningTest::class.java.getResource("invariantly_plato_starves.mcf").readText(),
            DiningTest::class.java.getResource("invariantly_possibly_eat.mcf").readText(),
            DiningTest::class.java.getResource("plato_infinitely_often_can_eat.mcf").readText()
        )
    }

    private fun getInvariantNames() : List<String> {
        return listOf<String>("invariantly_inevitably_eat.mcf", "invariantly_plato_starves.mcf", "invariantly_possibly_eat.mcf", "plato_infinitely_often_can_eat.mcf"
            )
    }

    private fun printResult(resultNaive: Boolean, resultImproved: Boolean, fileName: String, formula: String) {
        File("dining_results.txt").writeText("$fileName, $formula: naive: $resultNaive, improved: $resultImproved")
    }

    @Test
    fun modalCheckerTest() {
        val lts = getLts()
        val formula = getFormulas()

        val fileNames = getFileNames()

        val invNames = getInvariantNames()

        for ((i, l) in lts.withIndex()) {
            for ((j, f) in formula.withIndex()) {
                val resultNaive = ModalMuFacade.checkFormulaOnLTS(l, f, NaiveChecker())
                val resultImproved = ModalMuFacade.checkFormulaOnLTS(l, f, ImprovedChecker())

                print("result of ${fileNames[i]} with ${invNames[j]} is $resultNaive and $resultImproved")

                printResult(resultNaive, resultImproved, fileNames[i], invNames[j])
            }
        }
    }


}