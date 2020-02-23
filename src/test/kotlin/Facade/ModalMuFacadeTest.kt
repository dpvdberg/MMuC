package Facade

import Evaluation.ImprovedChecker
import Evaluation.NaiveChecker
import ModalMu.ForAll
import ModalMu.Operator
import ModalMu.Parsing.Grammar.ModalMuLexer
import ModalMu.Parsing.Grammar.ModalMuParser
import ModalMu.Parsing.Grammar.VisitModalMu
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class ModalMuFacadeTest {

    @Test
    fun modalCheckerTest() {
        val lts = """
            des (0,12,10)
            (0,"lock(p2, f2)",1)
            (0,"lock(p1, f1)",2)
            (1,"lock(p1, f1)",3)
            (1,"lock(p2, f1)",4)
            (2,"lock(p2, f2)",3)
            (2,"lock(p1, f2)",5)
            (4,"eat(p2)",6)
            (5,"eat(p1)",7)
            (6,"free(p2, f2)",8)
            (7,"free(p1, f1)",9)
            (8,"free(p2, f1)",0)
            (9,"free(p1, f2)",0)
        """.trimIndent()

        var formula = "true"

        var result = ModalMuFacade.checkFormulaOnLTS(lts, formula, NaiveChecker())

        assertTrue(result)

        formula = "false"

        result = ModalMuFacade.checkFormulaOnLTS(lts, formula, NaiveChecker())

        assertFalse(result)
    }

    @Test
    fun booleanTest() {
        val lts = """
            des (0,14,8)                                                 
            (0,"tau",1)
            (0,"tau",2)
            (1,"tau",3)
            (1,"tau",4)
            (2,"tau",5)
            (2,"tau",4)
            (3,"b",0)
            (3,"a",6)
            (4,"tau",7)
            (4,"tau",6)
            (5,"a",0)
            (5,"a",7)
            (6,"tau",2)
            (7,"b",1)
        """.trimIndent()

        val formula = listOf<String>("false", "true", "(false && true)", "(true && false)", "(false || true)", "(true && true)", "(false || false)", "(true || false)", "(true || true)")
        val expectedResult = listOf<Boolean>(false, true, false, false, true, true, false, true, true)

        for ((i, f) in formula.withIndex()) {
            val resultNaive = ModalMuFacade.checkFormulaOnLTS(lts, f, NaiveChecker())
            val resultImproved = ModalMuFacade.checkFormulaOnLTS(lts, f, ImprovedChecker())

            assert(resultNaive == expectedResult[i])
            assert(resultImproved == expectedResult[i])
        }
    }

    @Test
    fun combinedTest() {
        val lts = """
             des (0,14,8)                                                 
             (0,"tau",1)
             (0,"tau",2)
             (1,"tau",3)
             (1,"tau",4)
             (2,"tau",5)
             (2,"tau",4)
             (3,"b",0)
             (3,"a",6)
             (4,"tau",7)
             (4,"tau",6)
             (5,"a",0)
             (5,"a",7)
             (6,"tau",2)
             (7,"b",1)
        """.trimIndent()

        val formula = listOf<String>("nu X. <tau>X", "nu X. (<tau>X && mu Y. (<tau>Y || [a]false))", "nu X. mu Y. ( <tau>Y || <a>X)", "nu X. mu Y. ( (<tau>Y || <a>Y) || <b>X)", "mu X. ([tau]X && (<tau>true || <a>true))")
        val expectedResult = listOf<Boolean>(true, true, true, true, false)

        for ((i, f) in formula.withIndex()) {
            val resultNaive = ModalMuFacade.checkFormulaOnLTS(lts, f, NaiveChecker())
            val resultImproved = ModalMuFacade.checkFormulaOnLTS(lts, f, ImprovedChecker())

            assert(resultNaive == expectedResult[i])
            assert(resultImproved == expectedResult[i])
        }
    }

    @Test
    fun fixedPointTest() {
        val lts = """
            des (0,14,8)                                                 
            (0,"tau",1)
            (0,"tau",2)
            (1,"tau",3)
            (1,"tau",4)
            (2,"tau",5)
            (2,"tau",4)
            (3,"b",0)
            (3,"a",6)
            (4,"tau",7)
            (4,"tau",6)
            (5,"a",0)
            (5,"a",7)
            (6,"tau",2)
            (7,"b",1)
        """.trimIndent()

        val formula = listOf<String>("nu X. X", "mu Y. Y", "nu X. mu Y. (X || Y)", "nu X. mu Y. (X && Y)", "nu X. (X &&  mu Y. Y)")
        val expectedResult = listOf<Boolean>(true, false, true, false, false)

        for ((i, f) in formula.withIndex()) {
            val resultNaive = ModalMuFacade.checkFormulaOnLTS(lts, f, NaiveChecker())
            val resultImproved = ModalMuFacade.checkFormulaOnLTS(lts, f, ImprovedChecker())

            assert(resultNaive == expectedResult[i])
            assert(resultImproved == expectedResult[i])
        }
    }

    @Test
    fun modalOperatorsTest() {
        val lts = """
            des (0,14,8)                                                 
            (0,"tau",1)
            (0,"tau",2)
            (1,"tau",3)
            (1,"tau",4)
            (2,"tau",5)
            (2,"tau",4)
            (3,"b",0)
            (3,"a",6)
            (4,"tau",7)
            (4,"tau",6)
            (5,"a",0)
            (5,"a",7)
            (6,"tau",2)
            (7,"b",1)
            
        """.trimIndent()

        val formula = listOf<String>("[tau]true", "<tau>[tau]true", "[tau]false", "<tau>[tau]false", "<tau>false")
        val expectedResult = listOf<Boolean>(true, true, false, false, false)

        for ((i, f) in formula.withIndex()) {
            val resultNaive = ModalMuFacade.checkFormulaOnLTS(lts, f, NaiveChecker())
            val resultImproved = ModalMuFacade.checkFormulaOnLTS(lts, f, ImprovedChecker())

            assert(resultNaive == expectedResult[i])
            assert(resultImproved == expectedResult[i])
        }
    }
}
