package com.autopulse.app.domain.engine

import com.autopulse.app.core.logging.StructuredLogger
import com.autopulse.app.core.result.AppError
import com.autopulse.app.core.result.AppResult
import com.autopulse.app.core.result.runCatchingLogged
import com.autopulse.app.domain.catalog.CarPartEncyclopediaEntry
import com.autopulse.app.domain.catalog.CarPartsEncyclopediaCatalog
import com.autopulse.app.domain.catalog.FairPriceRange
import com.autopulse.app.domain.catalog.MechanicJargonCatalog
import com.autopulse.app.domain.model.ComponentCategory

enum class PriceFairnessVerdict {
    FAIR_MARKET_PRICE,
    SLIGHTLY_ABOVE_AVERAGE,
    OVERPRICED_SECOND_OPINION_RECOMMENDED,
    BELOW_MARKET_VERIFY_OEM_PARTS
}

data class QuoteFairnessEvaluation(
    val quotedAmountUsd: Int,
    val verdict: PriceFairnessVerdict,
    val summary: String
)

data class TranslatedMechanicEstimate(
    val mechanicTerm: String,
    val category: ComponentCategory,
    val simpleEnglishTitle: String,
    val whatItDoes: String,
    val whyItFailed: String,
    val analogy: String,
    val urgencyScore: Int, // 1 to 5
    val urgencyLabel: String,
    val riskOfIgnoring: String,
    val fairPriceRange: FairPriceRange,
    val questionsToAskMechanic: List<String>,
    val quoteEvaluation: QuoteFairnessEvaluation? = null
)

class MechanicTranslatorEngine {

    fun translateJargon(
        rawInput: String,
        quotedAmountUsd: Int? = null
    ): AppResult<TranslatedMechanicEstimate> = runCatchingLogged(
        module = "MechanicTranslatorEngine",
        operation = "translateJargon"
    ) {
        val cleaned = rawInput.trim()
        if (cleaned.isEmpty()) {
            throw AppError.ValidationError("Mechanic term or estimate description cannot be blank")
        }

        val normalized = cleaned.lowercase()
        val catalogHit = MechanicJargonCatalog.items.find { item ->
            item.mechanicTerm.lowercase() == normalized ||
                normalized.contains(item.mechanicTerm.lowercase()) ||
                item.aliases.any { normalized.contains(it.lowercase()) }
        }

        val baseItem = if (catalogHit != null) {
            catalogHit
        } else {
            val partMatch = CarPartsEncyclopediaCatalog.parts.find { part ->
                normalized.contains(part.name.lowercase()) || part.name.lowercase().contains(normalized)
            }
            if (partMatch != null) {
                com.autopulse.app.domain.catalog.MechanicJargonItem(
                    mechanicTerm = partMatch.name,
                    aliases = emptyList(),
                    category = partMatch.category,
                    simpleEnglishTitle = "${partMatch.category.name.lowercase().replaceFirstChar { it.uppercase() }} Component: ${partMatch.name}",
                    whatItDoes = partMatch.functionSummary,
                    whyItFailed = "Wear accumulation or symptom observed: ${partMatch.failureSymptom}",
                    analogy = "Acts as a critical link in your vehicle's ${partMatch.category.name.lowercase()} assembly.",
                    urgencyScore = 3,
                    urgencyLabel = "3/5 — RECOMMENDED SERVICE",
                    riskOfIgnoring = "Ignoring ${partMatch.failureSymptom.lowercase()} can cascade into costly adjacent repairs.",
                    fairPriceRange = FairPriceRange(95, 340, 2.2, "$320 - $690"),
                    questionsToAskMechanic = listOf(
                        "Can you show me the worn ${partMatch.name} or diagnostic test results?",
                        "Does this quote use OEM replacement parts and include a labor warranty?"
                    )
                )
            } else {
                com.autopulse.app.domain.catalog.MechanicJargonItem(
                    mechanicTerm = cleaned,
                    aliases = emptyList(),
                    category = ComponentCategory.POWERTRAIN,
                    simpleEnglishTitle = "Diagnostic & Repair Operation for \"$cleaned\"",
                    whatItDoes = "Restores factory mechanical clearances, sealing integrity, and sensor calibration.",
                    whyItFailed = "Normal thermal cycling, mileage wear, or fluid degradation exceeded OEM tolerance.",
                    analogy = "Like servicing a precision mechanical watch before a worn gear damages neighboring gears.",
                    urgencyScore = 3,
                    urgencyLabel = "3/5 — SCHEDULE SERVICE",
                    riskOfIgnoring = "Continued driving may decrease reliability, fuel efficiency, and safety margins.",
                    fairPriceRange = FairPriceRange(110, 380, 2.5, "$350 - $780"),
                    questionsToAskMechanic = listOf(
                        "What specific OBD-II trouble codes or physical measurements prompted this repair?",
                        "Is this an immediate safety item or can it be scheduled with my next oil service?"
                    )
                )
            }
        }

        val quoteEval = if (quotedAmountUsd != null && quotedAmountUsd > 0) {
            evaluateQuoteFairness(quotedAmountUsd, baseItem.fairPriceRange.totalFairRangeUsd)
        } else {
            null
        }

        StructuredLogger.info(
            module = "MechanicTranslatorEngine",
            message = "Translated mechanic jargon term",
            context = mapOf(
                "query" to cleaned,
                "matchedTerm" to baseItem.mechanicTerm,
                "urgencyScore" to baseItem.urgencyScore
            )
        )

        TranslatedMechanicEstimate(
            mechanicTerm = baseItem.mechanicTerm,
            category = baseItem.category,
            simpleEnglishTitle = baseItem.simpleEnglishTitle,
            whatItDoes = baseItem.whatItDoes,
            whyItFailed = baseItem.whyItFailed,
            analogy = baseItem.analogy,
            urgencyScore = baseItem.urgencyScore,
            urgencyLabel = baseItem.urgencyLabel,
            riskOfIgnoring = baseItem.riskOfIgnoring,
            fairPriceRange = baseItem.fairPriceRange,
            questionsToAskMechanic = baseItem.questionsToAskMechanic,
            quoteEvaluation = quoteEval
        )
    }

    fun searchPartsEncyclopedia(
        query: String = "",
        category: ComponentCategory? = null
    ): AppResult<List<CarPartEncyclopediaEntry>> = runCatchingLogged(
        module = "MechanicTranslatorEngine",
        operation = "searchPartsEncyclopedia"
    ) {
        val q = query.trim().lowercase()
        CarPartsEncyclopediaCatalog.parts.filter { entry ->
            val categoryMatch = category == null || entry.category == category
            val textMatch = q.isEmpty() ||
                entry.name.lowercase().contains(q) ||
                entry.functionSummary.lowercase().contains(q) ||
                entry.failureSymptom.lowercase().contains(q)
            categoryMatch && textMatch
        }
    }

    private fun evaluateQuoteFairness(quotedUsd: Int, rangeText: String): QuoteFairnessEvaluation {
        val numbers = Regex("\\d[\\d,]*").findAll(rangeText)
            .mapNotNull { it.value.replace(",", "").toIntOrNull() }
            .toList()
        val low = numbers.getOrElse(0) { 250 }
        val high = numbers.getOrElse(1) { 650 }

        return when {
            quotedUsd < (low * 0.75).toInt() -> QuoteFairnessEvaluation(
                quotedAmountUsd = quotedUsd,
                verdict = PriceFairnessVerdict.BELOW_MARKET_VERIFY_OEM_PARTS,
                summary = "Quote ($$quotedUsd) is below the typical $$low–$$high range. Verify OEM part quality and warranty."
            )
            quotedUsd <= (high * 1.05).toInt() -> QuoteFairnessEvaluation(
                quotedAmountUsd = quotedUsd,
                verdict = PriceFairnessVerdict.FAIR_MARKET_PRICE,
                summary = "Quote ($$quotedUsd) is within the fair national benchmark ($rangeText)."
            )
            quotedUsd <= (high * 1.30).toInt() -> QuoteFairnessEvaluation(
                quotedAmountUsd = quotedUsd,
                verdict = PriceFairnessVerdict.SLIGHTLY_ABOVE_AVERAGE,
                summary = "Quote ($$quotedUsd) is 10–30% above average ($rangeText). Request itemized labor hours."
            )
            else -> QuoteFairnessEvaluation(
                quotedAmountUsd = quotedUsd,
                verdict = PriceFairnessVerdict.OVERPRICED_SECOND_OPINION_RECOMMENDED,
                summary = "Alert: Quote ($$quotedUsd) significantly exceeds fair range ($rangeText). Second opinion advised."
            )
        }
    }
}
