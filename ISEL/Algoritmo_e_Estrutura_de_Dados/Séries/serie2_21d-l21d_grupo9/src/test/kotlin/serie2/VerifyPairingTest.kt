package serie2

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class VerifyPairingTest {
    @Test
    fun verifyPairing_empty_String() {
        assertTrue(verifyPairing(""))
    }

    @Test
    fun verifyPairing_simple_pair() {
        assertTrue(verifyPairing("{}"))
        assertTrue(verifyPairing("()"))
        assertTrue(verifyPairing("[]"))
        assertTrue(verifyPairing("..{...}"))
        assertTrue(verifyPairing(".(.)."))
        assertTrue(verifyPairing("[.].."))
    }

    @Test
    fun verifyPairing_open_only() {
        assertFalse(verifyPairing("{"))
        assertFalse(verifyPairing("["))
        assertFalse(verifyPairing("("))
        assertFalse(verifyPairing("..{.."))
        assertFalse(verifyPairing(".["))
        assertFalse(verifyPairing("(."))
    }

    @Test
    fun verifyPairing_close_only() {
        assertFalse(verifyPairing("}"))
        assertFalse(verifyPairing(")"))
        assertFalse(verifyPairing("]"))
        assertFalse(verifyPairing("..}.."))
        assertFalse(verifyPairing(".)"))
        assertFalse(verifyPairing("]."))
    }

    @Test
    fun verifyPairing_close_and_open_inverted() {
        assertFalse(verifyPairing("}{"))
        assertFalse(verifyPairing(")("))
        assertFalse(verifyPairing("]["))
        assertFalse(verifyPairing("..}{.."))
        assertFalse(verifyPairing(".)("))
        assertFalse(verifyPairing("][."))
        assertFalse(verifyPairing("][."))
    }

    @Test
    fun verifyPairing_different_pair() {
        assertFalse(verifyPairing("{]"))
        assertFalse(verifyPairing("(}"))
        assertFalse(verifyPairing("[)"))
        assertFalse(verifyPairing("..{...)"))
        assertFalse(verifyPairing(".(.]."))
        assertFalse(verifyPairing("[.}.."))
        assertFalse(verifyPairing("{)."))
    }

    @Test
    fun verifyPairing_pairs() {
        assertTrue(verifyPairing("{(-)[]([--{--} -]--)-{-}-}---"))
        assertTrue(verifyPairing("{{-}(()[])[--{--} -]-(-)-{-}-}---"))
        assertFalse(verifyPairing("{(-)[]([-- {--} -]--)-----"))
        assertFalse(verifyPairing("{(-)[]([----} -]--)-{-}----"))
    }
}