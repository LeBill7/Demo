package com.autopulse.app.domain.engine

import com.autopulse.app.domain.model.ComponentCategory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MechanicTranslatorEngineTest {

    private val translator = MechanicTranslatorEngine()

    @Test
    fun `translates Blown Head Gasket into plain English with urgency 5 and fair price`() {
        val res = translator.translateJargon("Blown Head Gasket", quotedAmountUsd = 1800)
        assertTrue(res.isSuccess)
        val data = res.getOrNull()!!
        assertEquals("Blown Head Gasket", data.mechanicTerm)
        assertEquals(5, data.urgencyScore)
        assertEquals(
            "Damaged Engine Seal Between Combustion Chamber and Coolant",
            data.simpleEnglishTitle
        )
        assertTrue(data.questionsToAskMechanic.size >= 2)
        assertEquals(PriceFairnessVerdict.FAIR_MARKET_PRICE, data.quoteEvaluation?.verdict)
    }

    @Test
    fun `flags overpriced repair estimate accurately`() {
        val res = translator.translateJargon("Tie Rod Play", quotedAmountUsd = 1600)
        assertTrue(res.isSuccess)
        val data = res.getOrNull()!!
        assertEquals(
            PriceFairnessVerdict.OVERPRICED_SECOND_OPINION_RECOMMENDED,
            data.quoteEvaluation?.verdict
        )
    }

    @Test
    fun `searches over 100 essential car parts in the visual encyclopedia`() {
        val allRes = translator.searchPartsEncyclopedia("")
        assertTrue(allRes.isSuccess)
        val allParts = allRes.getOrNull()!!
        assertTrue(
            allParts.size >= 100,
            "Expected >= 100 encyclopedia parts, found ${allParts.size}"
        )

        val brakingRes = translator.searchPartsEncyclopedia("caliper", ComponentCategory.BRAKING)
        assertTrue(brakingRes.isSuccess)
        assertTrue(brakingRes.getOrNull()!!.size >= 2)
    }

    @Test
    fun `returns ValidationError on blank mechanic term`() {
        val res = translator.translateJargon("   ")
        assertTrue(res.isFailure)
        assertEquals("VALIDATION_ERROR", res.errorOrNull()?.code)
    }

    @Test
    fun `handles custom unlisted repair description gracefully`() {
        val res = translator.translateJargon("Custom Subframe Reinforcement Bushings", 500)
        assertTrue(res.isSuccess)
        assertNotNull(res.getOrNull()?.simpleEnglishTitle)
    }
}
