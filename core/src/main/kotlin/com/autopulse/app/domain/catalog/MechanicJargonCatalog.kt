package com.autopulse.app.domain.catalog

import com.autopulse.app.domain.model.ComponentCategory

data class FairPriceRange(
    val partsMinUsd: Int,
    val partsMaxUsd: Int,
    val laborHoursEst: Double,
    val totalFairRangeUsd: String
)

data class MechanicJargonItem(
    val mechanicTerm: String,
    val aliases: List<String>,
    val category: ComponentCategory,
    val simpleEnglishTitle: String,
    val whatItDoes: String,
    val whyItFailed: String,
    val analogy: String,
    val urgencyScore: Int, // 1 (Routine) to 5 (Do Not Drive)
    val urgencyLabel: String,
    val riskOfIgnoring: String,
    val fairPriceRange: FairPriceRange,
    val questionsToAskMechanic: List<String>
)

object MechanicJargonCatalog {
    val items: List<MechanicJargonItem> = listOf(
        MechanicJargonItem(
            mechanicTerm = "Blown Head Gasket",
            aliases = listOf("head gasket", "cylinder head gasket", "coolant in oil", "white exhaust smoke"),
            category = ComponentCategory.POWERTRAIN,
            simpleEnglishTitle = "Damaged Engine Seal Between Combustion Chamber and Coolant",
            whatItDoes = "The head gasket seals the engine cylinder head to the engine block, keeping high-pressure oil, coolant, and combustion fire separated.",
            whyItFailed = "Overheating or extreme thermal stress warped the metal deck surfaces, allowing coolant to breach the cylinders.",
            analogy = "Like a broken seal on a pressure cooker — once the gasket fails, high-pressure steam and liquid escape everywhere.",
            urgencyScore = 5,
            urgencyLabel = "5/5 — CRITICAL: DO NOT DRIVE",
            riskOfIgnoring = "Catastrophic engine hydro-lock, warped aluminum block, or total engine replacement ($4,500+).",
            fairPriceRange = FairPriceRange(150, 400, 8.5, "$1,400 - $2,200"),
            questionsToAskMechanic = listOf(
                "Did the engine overheat, and has the cylinder head been machined and checked for warping?",
                "Are the radiator and thermostat also being replaced so the new gasket doesn't fail?"
            )
        ),
        MechanicJargonItem(
            mechanicTerm = "Differential Pinion Seal Leak",
            aliases = listOf("pinion seal", "diff leak", "rear diff seal", "differential leak"),
            category = ComponentCategory.FLUIDS,
            simpleEnglishTitle = "Leaking Rubber Ring Where the Driveshaft Enters the Axle Gear Box",
            whatItDoes = "Keeps heavy hypoid gear oil sealed inside the differential housing while the driveshaft spins at thousands of RPM.",
            whyItFailed = "Road grit, age, or a clogged axle breather vent wore down the spring-loaded viton sealing lip.",
            analogy = "Like a worn rubber washer at the base of a blender pitcher that slowly lets liquid seep out while spinning.",
            urgencyScore = 3,
            urgencyLabel = "3/5 — SCHEDULE WITHIN 2 WEEKS",
            riskOfIgnoring = "If the differential runs dry, the ring-and-pinion gears grind themselves into metal shavings ($1,800+ axle rebuild).",
            fairPriceRange = FairPriceRange(25, 75, 1.8, "$240 - $420"),
            questionsToAskMechanic = listOf(
                "Did you check if the axle breather vent is clogged and building internal pressure?",
                "Is there any radial play in the pinion bearing, or is it strictly a worn rubber seal?"
            )
        ),
        MechanicJargonItem(
            mechanicTerm = "Tie Rod Play",
            aliases = listOf("tie rod", "inner tie rod", "outer tie rod", "loose steering", "tie rod end"),
            category = ComponentCategory.SUSPENSION,
            simpleEnglishTitle = "Worn Pivot Joint Connecting Your Steering Wheel to the Front Wheels",
            whatItDoes = "Pushes and pulls your front wheels left and right when you steer while pivoting smoothly with suspension travel.",
            whyItFailed = "Potholes, curb impacts, or split rubber grease boots let water and grit grind down the ball-and-socket joint.",
            analogy = "Like a loose shoulder socket — there is a sloppy clunk before your arm actually moves.",
            urgencyScore = 4,
            urgencyLabel = "4/5 — HIGH SAFETY PRIORITY",
            riskOfIgnoring = "Rapid front tire edge destruction and risk of complete tie rod separation, causing total loss of steering control.",
            fairPriceRange = FairPriceRange(60, 180, 2.0, "$280 - $520 (with 4-wheel alignment)"),
            questionsToAskMechanic = listOf(
                "Is the looseness in the inner tie rod, outer tie rod, or both?",
                "Does this estimate include a computerized 4-wheel laser alignment afterward?"
            )
        ),
        MechanicJargonItem(
            mechanicTerm = "Catalytic Converter Efficiency Below Threshold (P0420)",
            aliases = listOf("p0420", "catalytic converter", "cat converter", "exhaust catalyst"),
            category = ComponentCategory.POWERTRAIN,
            simpleEnglishTitle = "Exhaust Pollution Filter Is No Longer Scrubbing Emissions Effectively",
            whatItDoes = "Uses a precious-metal ceramic honeycomb to neutralize toxic exhaust gases into harmless steam and CO2.",
            whyItFailed = "Unburned fuel from worn spark plugs or oil consumption overheated or poisoned the internal honeycomb.",
            analogy = "Like a charcoal water purifier cartridge coated in sludge so water passes through unfiltered.",
            urgencyScore = 3,
            urgencyLabel = "3/5 — MODERATE: FIX BEFORE INSPECTION",
            riskOfIgnoring = "Failed emissions test, reduced fuel economy, and potential melted substrate plugging the exhaust.",
            fairPriceRange = FairPriceRange(450, 1400, 2.5, "$800 - $1,950"),
            questionsToAskMechanic = listOf(
                "Did you test the upstream and downstream O2 sensors first to rule out a faulty sensor?",
                "What root cause (misfire or oil burn) damaged the converter so the new one isn't ruined?"
            )
        ),
        MechanicJargonItem(
            mechanicTerm = "Warped Brake Rotors & Seized Caliper Slide Pins",
            aliases = listOf("warped rotors", "brake pulsation", "seized caliper", "shaking when braking"),
            category = ComponentCategory.BRAKING,
            simpleEnglishTitle = "Uneven Metal Brake Discs & Stuck Clamp Guides Causing Steering Vibration",
            whatItDoes = "Calipers slide on lubricated guide pins to squeeze brake pads evenly against spinning metal discs (rotors).",
            whyItFailed = "Heavy braking heat left uneven pad material on the disc, while corroded slide pins kept one pad dragging.",
            analogy = "Like bicycle rim brakes rubbing on a bent wheel — every turn grabs and releases, shaking the handlebars.",
            urgencyScore = 4,
            urgencyLabel = "4/5 — HIGH SAFETY PRIORITY",
            riskOfIgnoring = "Extended stopping distances, overheated brake fluid, and rapid destruction of brake pads.",
            fairPriceRange = FairPriceRange(180, 360, 2.0, "$390 - $680"),
            questionsToAskMechanic = listOf(
                "Can the caliper slide pins be cleaned and re-greased, or is the hydraulic piston seized?",
                "Are you installing coated high-carbon rotors to prevent rust-jacking?"
            )
        ),
        MechanicJargonItem(
            mechanicTerm = "Torn CV Axle Boot / Clicking Half-Shaft",
            aliases = listOf("cv axle", "cv boot", "cv joint", "clicking on turns"),
            category = ComponentCategory.SUSPENSION,
            simpleEnglishTitle = "Split Rubber Protective Cover on the Drive Axle Allowing Grease to Escape",
            whatItDoes = "Transfers torque from the transmission to the wheels while allowing the wheels to steer and move over bumps.",
            whyItFailed = "Constant flexing split the ribbed neoprene boot, flinging out grease and letting abrasive sand inside.",
            analogy = "Like walking through beach sand with a ripped boot — grit gets inside and grinds the joint with every step.",
            urgencyScore = 4,
            urgencyLabel = "4/5 — REPAIR THIS WEEK",
            riskOfIgnoring = "The CV joint will seize or snap while driving, leaving the vehicle stranded with zero drive power.",
            fairPriceRange = FairPriceRange(120, 290, 2.2, "$350 - $650"),
            questionsToAskMechanic = listOf(
                "Is the joint already clicking on turns (requiring a full axle), or can we reboot and repack it?",
                "Will you inspect the transmission output shaft seal while the axle is removed?"
            )
        )
    )
}
