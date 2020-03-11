package part2.game

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

class GameTest : MeasurementTestCase() {
    override fun getFileNames(): List<String> {
        return listOf(
            "robots_50.aut",
            "robots_100.aut",
            "robots_150.aut",
            "robots_200.aut",
            "robots_250.aut",
            "robots_300.aut",
            "robots_350.aut",
            "robots_400.aut",
            "robots_450.aut",
            "robots_500.aut"
        )
    }

    override fun getInvariantNames(): List<String> {
        return listOf(
            "eventually-won.mcf",
            "player-II-win.mcf",
            "player-II-win-infinite.mcf"
        )
    }

    override fun getResultFileFormat() : String {
        return "'game_results.'yyyyMMddHHmm'.csv'"
    }
}