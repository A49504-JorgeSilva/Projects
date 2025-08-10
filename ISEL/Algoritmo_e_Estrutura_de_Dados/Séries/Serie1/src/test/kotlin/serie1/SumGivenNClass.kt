package serie1

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import serie1.sumGivenN

class SumGivenNClass {

        @Test
        fun sumGivenN_onZero() {
            assertEquals(1, sumGivenN(0))
        }
        // 7 8 9
        //24
        @Test
        fun sumGivenN_onGiven24() {
            assertEquals(2, sumGivenN(24))
        }
        //3 4 5 6 7
        // 1 2 13
        //25
        @Test
        fun sumGivenN_onGiven25() {
            assertEquals(3, sumGivenN(25))
        }

        //5 6 7 8
       //26
        @Test
        fun sumGivenN_onGiven26() {
            assertEquals(2, sumGivenN(26))
        }

        //2 3 4 5 6 7
        //8 9 10
        //13 14
        //27
        @Test
        fun sumGivenN_onGiven27() {
            assertEquals(4, sumGivenN(27))
        }

        //1 2 3 4 5 6 7
        //28
        @Test
        fun sumGivenN_onGiven28() {
            assertEquals(2, sumGivenN(28))
        }

    }