package pt.isel.pc.problemsets.set1

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


private val log = LoggerFactory.getLogger(CountDownLatchTests::class.java)

class ExchangerTests {

    val exchanger = Exchanger<Int>()

    private fun threadMethod(i : Int){
        val name = Thread.currentThread().name
        log.info("Running on thread '{}'", name)
        log.info("'{}' - calling exchange('{}')", name, i)
        val x = exchanger.exchange(i)
        log.info("'{}' - returned with '{}'", name, x)
    }

    private fun threadMethodTimeout(i : Int){
        val name = Thread.currentThread().name
        log.info("Running on thread '{}'", name)
        log.info("'{}' - calling exchange('{}', timeout)", name, i)
        val x = exchanger.exchange(i, 3,TimeUnit.SECONDS)
        log.info("'{}' - returned with '{}'", name, x)
    }


    @Test
    fun `exchange with multiple threads`(){
        val ths = listOf(
            Thread{ threadMethod(1)},
            Thread{ threadMethod(0)},
            Thread{ threadMethod(2)},
            Thread{ threadMethod(3)}
        )

        ths.forEach{thread -> thread.start() }

        ths.forEach { thread -> thread.join() }
    }

    @Test
    fun `exchange with timeout exception`(){
        val th = Thread {
            assertThrows<TimeoutException> {
                threadMethodTimeout(0)
            }
        }
        th.start()
        th.join()
    }

    @Test
    fun `stress exchange`(){
        val ths = listOf(
            Thread{ threadMethodTimeout(1)},
            Thread{ threadMethodTimeout(2)},
            Thread{ threadMethodTimeout(3)},
            Thread{ threadMethodTimeout(4)},
            Thread{ threadMethodTimeout(5)},
            Thread{ threadMethodTimeout(6)},
            Thread{ threadMethodTimeout(7)},
            Thread{ threadMethodTimeout(8)},
            Thread{ threadMethodTimeout(9)},
            Thread{ threadMethodTimeout(10)},
            Thread{ threadMethodTimeout(11)},
            Thread{ threadMethodTimeout(12)},
            Thread{ threadMethodTimeout(13)},
            Thread{ threadMethodTimeout(14)},
            Thread{ threadMethodTimeout(15)},
            Thread{ threadMethodTimeout(16)},
            Thread{ threadMethodTimeout(17)},
            Thread{ threadMethodTimeout(18)},
            Thread{ threadMethodTimeout(19)},
            Thread{ threadMethodTimeout(20)},

        )
        ths.forEach { thread -> thread.start() }

        ths.forEach { thread -> thread.join() }
    }


}