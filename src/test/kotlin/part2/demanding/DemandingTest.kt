package part2.demanding

import part2.MeasurementTestCase

class DemandingTest : MeasurementTestCase() {
    override fun getFileNames(): List<String> {
        return (2..10).map {i -> "demanding_children_$i.aut"}.toList()
    }

    override fun getInvariantNames(): List<String> {
        return listOf(
            "eventually_child_turn.mcf",
            "eventually_get_answer.mcf",
            "infinitely_often_child_turn.mcf",
            "infinitely_others_turn.mcf",
            "invariantly_eventually_guaranteed_child_turn.mcf",
            "invariantly_eventually_infinitely_others_turn.mcf",
            "no_ask_after_wisdom.mcf",
            "no_playing_after_ask.mcf",
            "no_wisdom_after_playing.mcf"
            )
    }

    override fun getResultFileFormat(): String {
        return "'demanding_results.'yyyyMMddHHmm'.csv'"
    }
}