package pt.isel.pc.problemsets.set2


import kotlin.test.Test
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue


class SafeResourceManagerTest {
    class MockResource : AutoCloseable {
        var closed = false
        override fun close() {
            closed = true
        }
    }

    @Test
    fun `test resource manager`() {
        val resource = MockResource()
        val manager = SafeResourceManager(resource, 1)
        val th1 = Thread {
            manager.release()
        }
        th1.start()
        th1.join()
        assertTrue(resource.closed)
    }

    @Test
    fun `test resource manager with multiple usages`() {
        val resource = MockResource()
        val manager = SafeResourceManager(resource, 2)
        val th1 = Thread {
            manager.release()
        }
        val th2 = Thread {
            manager.release()
        }
        th1.start()
        th1.join()
        assertTrue(!resource.closed)
        th2.start()
        th2.join()
        assertTrue(resource.closed)
    }

    @Test
    fun `test resource manager with zero usages`() {
        val resource = MockResource()
        val manager = SafeResourceManager(resource, 0)

        assertFailsWith<IllegalStateException> {
            val th1 = Thread {
                manager.release()
            }
            th1.start()
            th1.join()
        }
    }

    @Test
    fun `test resource manager with concurrent usage`() {
        val resource = MockResource()
        val manager = SafeResourceManager(resource, 2)
        val t1 = Thread {
            manager.release()
        }
        val t2 = Thread {
            manager.release()
        }
        t1.start()
        t2.start()
        t1.join()
        t2.join()
        assert(resource.closed)

    }
}