package part2.dining

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

class DiningTest : MeasurementTestCase() {
    override fun getFileNames(): List<String> {
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

    override fun getInvariantNames(): List<String> {
        return listOf(
            "invariantly_inevitably_eat.mcf",
            "invariantly_plato_starves.mcf",
            "invariantly_possibly_eat.mcf",
            "plato_infinitely_often_can_eat.mcf"
        )
    }

    override fun getResultFileFormat(): String {
        return "'dining_result.'yyyyMMddHHmm'.csv'"
    }
}