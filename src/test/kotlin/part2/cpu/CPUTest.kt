package part2.cpu

import Evaluation.ImprovedChecker
import Evaluation.NaiveChecker
import LTS.Parsing.AldebaranParser
import part2.MeasurementTestCase
import ModalMu.Parsing.ModalMuParser
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import toHMS
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class CPUTest : MeasurementTestCase() {
    override fun getFileNames(): List<String> {
        return listOf(
            "german_linear_2.1.aut",
            "german_linear_3.1.aut",
            "german_linear_4.1.aut",
            "german_linear_5.1.aut"
        )
    }

    override fun getInvariantNames(): List<String> {
        return listOf(
            "infinite_run_no_access.mcf",
            "infinitely_often_exclusive.mcf",
            "invariantly_eventually_fair_shared_access.mcf",
            "invariantly_inevitably_exclusive_access.mcf",
            "invariantly_possibly_exclusive_access.mcf"
        )
    }

    override fun getResultFileFormat() : String {
        return "'cpu_results.'yyyyMMddHHmm'.csv'"
    }
}