package LTS.Parsing

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class MapTest {

    @Test
    fun test() {
        val map = mutableMapOf<Int, Int>()

        map[0] = 1
        changeMap(map)
        assertEquals(map[0], 99)
    }

    fun changeMap(m : MutableMap<Int, Int>) {
        m[1] = 1
        changeMap2(m)
        m[1] = 2
    }

    fun changeMap2(m : MutableMap<Int, Int>) {
        m[0] = 99
    }
}