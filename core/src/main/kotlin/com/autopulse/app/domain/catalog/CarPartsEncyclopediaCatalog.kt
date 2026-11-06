package com.autopulse.app.domain.catalog

import com.autopulse.app.domain.model.ComponentCategory

data class CarPartEncyclopediaEntry(
    val id: String,
    val name: String,
    val category: ComponentCategory,
    val functionSummary: String,
    val failureSymptom: String,
    val maintenanceBestPractice: String
)

/**
 * Searchable visual encyclopedia of 102 essential automotive parts categorized by system.
 */
object CarPartsEncyclopediaCatalog {

    private val rawEntries: List<Triple<String, ComponentCategory, Pair<String, String>>> = listOf(
        // POWERTRAIN (18)
        Triple("Cylinder Head Gasket", ComponentCategory.POWERTRAIN, "Seals combustion pressure, coolant, and oil between block and head." to "White sweet exhaust smoke, overheating, milky oil."),
        Triple("Forged Crankshaft", ComponentCategory.POWERTRAIN, "Converts linear piston strokes into rotational torque." to "Deep metallic bottom-end knock and low oil pressure."),
        Triple("Connecting Rod Bearings", ComponentCategory.POWERTRAIN, "Supports connecting rods on a hydrodynamic oil film." to "Rhythmic knock that speeds up with engine RPM."),
        Triple("VVT Solenoid Actuator", ComponentCategory.POWERTRAIN, "Routes oil pressure to advance or retard camshaft timing." to "Rough idle, sluggish acceleration, code P0011."),
        Triple("Intake & Exhaust Camshafts", ComponentCategory.POWERTRAIN, "Opens and closes cylinder poppet valves in sequence." to "Top-end valvetrain ticking and cylinder misfire."),
        Triple("Timing Belt / Roller Chain", ComponentCategory.POWERTRAIN, "Synchronizes crankshaft and camshaft at a 2:1 ratio." to "No-compression cranking or rattling on cold start."),
        Triple("Turbocharger Turbine & Wastegate", ComponentCategory.POWERTRAIN, "Uses exhaust gas energy to compress intake air charge." to "Siren whine, bluish smoke, loss of boost pressure."),
        Triple("Mass Air Flow (MAF) Sensor", ComponentCategory.POWERTRAIN, "Measures incoming air mass for fuel injection calculation." to "Hesitation under load, rich/lean fuel trim codes."),
        Triple("Electronic Throttle Body", ComponentCategory.POWERTRAIN, "Modulates airflow entering the intake manifold via servo plate." to "Erratic idle RPM and reduced power warning."),
        Triple("Direct Fuel Injector", ComponentCategory.POWERTRAIN, "Atomizes fuel at up to 3,000 PSI into the combustion chamber." to "Hard cold start, fuel smell, cylinder misfire."),
        Triple("Iridium Spark Plug", ComponentCategory.POWERTRAIN, "Ignites compressed air-fuel mixture via a 25,000V spark arc." to "Stumbling on hills, flashing check engine light."),
        Triple("PCV Crankcase Valve", ComponentCategory.POWERTRAIN, "Evacuates blow-by gases from the crankcase into the intake." to "Oil leaks at gaskets and whistling engine noise."),
        Triple("Harmonic Balancer Pulley", ComponentCategory.POWERTRAIN, "Absorbs torsional crankshaft vibration via bonded elastomer." to "Wobbling drive belt and front seal oil leak."),
        Triple("Gerotor Oil Pump", ComponentCategory.POWERTRAIN, "Pressurizes lubricating oil galleries at 30-65 PSI." to "Red oil pressure warning light and valvetrain clatter."),
        Triple("Exhaust Manifold Header", ComponentCategory.POWERTRAIN, "Funnels hot exhaust gases from cylinder ports to the catalyst." to "Loud ticking when cold and exhaust smell in cabin."),
        Triple("Three-Way Catalytic Converter", ComponentCategory.POWERTRAIN, "Scrubs hydrocarbons, CO, and NOx using precious metals." to "Rotten-egg sulfur odor and code P0420."),
        Triple("Upstream Wideband O2 Sensor", ComponentCategory.POWERTRAIN, "Measures pre-catalyst oxygen to regulate closed-loop fuel trim." to "15-20% drop in fuel economy and surging."),
        Triple("Hydraulic Engine Mount", ComponentCategory.POWERTRAIN, "Isolates the chassis from engine vibration and torque roll." to "Clunk when shifting Drive to Reverse."),

        // BRAKING (17)
        Triple("Tandem Brake Master Cylinder", ComponentCategory.BRAKING, "Converts pedal force into dual-circuit hydraulic pressure." to "Brake pedal slowly sinks to the floor at stops."),
        Triple("Vacuum Brake Booster", ComponentCategory.BRAKING, "Amplifies driver foot force using intake vacuum or pump." to "Stiff brake pedal requiring heavy leg effort."),
        Triple("ABS Hydraulic Modulator", ComponentCategory.BRAKING, "Pulses caliper pressure 15 times/sec to prevent wheel lockup." to "ABS warning light and wheel skid on wet roads."),
        Triple("Ventilated Front Brake Rotor", ComponentCategory.BRAKING, "Cast-iron disc with cooling vanes that dissipates braking heat." to "Steering wheel shake during highway braking."),
        Triple("Ceramic Brake Pads", ComponentCategory.BRAKING, "High-friction compound that clamps the spinning rotor." to "High-pitched squealer noise or metal grinding."),
        Triple("Floating Caliper Piston", ComponentCategory.BRAKING, "Applies hydraulic clamping force to inner and outer brake pads." to "Vehicle pulls sharply left or right when braking."),
        Triple("Caliper Guide Slide Pins", ComponentCategory.BRAKING, "Allows floating caliper housing to center evenly over the rotor." to "Uneven inner vs. outer brake pad wear."),
        Triple("EPDM Flexible Brake Hose", ComponentCategory.BRAKING, "Carries pressurized fluid from chassis line to moving wheel." to "Spongy brake pedal feel or cracked outer hose."),
        Triple("Hall-Effect Wheel Speed Sensor", ComponentCategory.BRAKING, "Reads tone ring teeth to report real-time wheel RPM to ABS." to "ABS and Traction Control lights illuminate together."),
        Triple("Electronic Parking Brake Motor", ComponentCategory.BRAKING, "Electrically clamps rear brake pads when parked." to "EPB fault alert or stuck rear brakes."),
        Triple("Brake Pad Wear Sensor Lead", ComponentCategory.BRAKING, "Triggers dashboard warning when pad lining reaches 3mm." to "Amber brake lining warning light on cluster."),
        Triple("Electronic Brakeforce Distribution", ComponentCategory.BRAKING, "Balances front-to-rear brake bias based on load transfer." to "Rear stability twitch during panic stops."),
        Triple("Brake Rotor Splash Shield", ComponentCategory.BRAKING, "Protects brake disc from road stones and deep puddle splash." to "Loud metallic screech from trapped gravel."),
        Triple("Stainless Pad Abutment Clips", ComponentCategory.BRAKING, "Guides brake pad ears smoothly inside the caliper bracket." to "Metallic rattle over bumps that stops when braking."),
        Triple("Caliper Bleeder Valve", ComponentCategory.BRAKING, "Purges trapped air bubbles and degraded brake fluid." to "Seepage around caliper top nipple."),
        Triple("Regenerative Brake Blender", ComponentCategory.BRAKING, "Blends electric motor regen torque with hydraulic friction." to "Inconsistent low-speed brake pedal feel."),
        Triple("Rigid Steel Brake Hardlines", ComponentCategory.BRAKING, "Routes hydraulic brake pressure underneath the vehicle floor." to "Corrosion pitting in road-salt climates."),

        // FLUIDS & COOLING (17)
        Triple("DOT 4 Glycol Brake Fluid", ComponentCategory.FLUIDS, "Transfers hydraulic braking force with high boiling point (>446F)." to "Moisture above 3% causes vapor lock on hills."),
        Triple("OAT/HOAT Engine Coolant", ComponentCategory.FLUIDS, "Transfers combustion heat and prevents freezing/corrosion." to "Sludge in reservoir or overheating gauge."),
        Triple("Full Synthetic Motor Oil", ComponentCategory.FLUIDS, "Lubricates, cools, and cleans internal engine bearings." to "Dark gritty oil on dipstick and ticking noise."),
        Triple("Synthetic ATF / DCT Fluid", ComponentCategory.FLUIDS, "Actuates transmission clutches and cools planetary gears." to "Burnt smell and delayed gear engagement."),
        Triple("75W-90 Hypoid Gear Oil", ComponentCategory.FLUIDS, "Protects high-load ring and pinion differential teeth." to "Whining sound from front or rear axle."),
        Triple("Differential Pinion Oil Seal", ComponentCategory.FLUIDS, "Seals the input shaft entering the differential housing." to "Gear oil spots beneath the axle housing."),
        Triple("AWD Transfer Case Fluid", ComponentCategory.FLUIDS, "Lubricates AWD chain drive and electro-hydraulic clutch pack." to "Shuddering during tight low-speed turns."),
        Triple("Aluminum Crossflow Radiator", ComponentCategory.FLUIDS, "Rejects engine coolant heat to oncoming airflow." to "Crusty coolant stains along side tank seams."),
        Triple("Centrifugal Water Pump", ComponentCategory.FLUIDS, "Circulates coolant continuously through block and head." to "Coolant dripping from pump weep hole."),
        Triple("Wax-Pellet Thermostat", ComponentCategory.FLUIDS, "Regulates coolant flow to maintain 195F operating temp." to "Lukewarm cabin heat or rapid overheating."),
        Triple("Radiator Pressure Cap (18 PSI)", ComponentCategory.FLUIDS, "Raises coolant boiling point by pressurizing the loop." to "Coolant boiling into recovery bottle early."),
        Triple("EPDM Radiator Coolant Hoses", ComponentCategory.FLUIDS, "Carries hot coolant between vibrating engine and radiator." to "Soft swollen hose feel or steam leak."),
        Triple("Engine Oil Cooler Heat Exchanger", ComponentCategory.FLUIDS, "Uses engine coolant to stabilize oil temperature under load." to "Oil sheen floating inside coolant tank."),
        Triple("Transmission Fluid Cooler", ComponentCategory.FLUIDS, "Cools hot automatic transmission fluid via radiator loop." to "Transmission high-temperature warning."),
        Triple("Variable-Displacement A/C Compressor", ComponentCategory.FLUIDS, "Pumps refrigerant and PAG oil through the HVAC loop." to "Warm A/C air and noisy compressor clutch."),
        Triple("Cabin Heater Core", ComponentCategory.FLUIDS, "Uses hot engine coolant to warm interior cabin air." to "Sweet smell in cabin and foggy windshield."),
        Triple("Pressurized Coolant Degas Bottle", ComponentCategory.FLUIDS, "Allows heated coolant expansion and deaeration." to "Hairline plastic cracks and low coolant alert."),

        // ELECTRICAL (17)
        Triple("12V AGM Starter Battery", ComponentCategory.ELECTRICAL, "Supplies 650-850 CCA for starting and stabilizing electronics." to "Slow cranking on cold mornings."),
        Triple("Brushless 3-Phase Alternator", ComponentCategory.ELECTRICAL, "Generates 14.2V DC power to run loads and charge battery." to "Red battery light and dimming headlamps."),
        Triple("Planetary Starter Motor", ComponentCategory.ELECTRICAL, "Spins the engine flywheel ring gear during ignition start." to "Single loud click with no engine cranking."),
        Triple("Engine Control Module (ECM)", ComponentCategory.ELECTRICAL, "Computes fuel injection and spark timing in real time." to "No-start condition and U0100 bus fault."),
        Triple("Body Control Module (BCM)", ComponentCategory.ELECTRICAL, "Controls locks, windows, interior lighting, and security." to "Flickering cabin lights or dead power locks."),
        Triple("CAN Bus Differential Network", ComponentCategory.ELECTRICAL, "High-speed twisted-pair wiring linking 30+ vehicle ECUs." to "Multiple dashboard warning lights at once."),
        Triple("OBD-II J1962 Diagnostic Port", ComponentCategory.ELECTRICAL, "16-pin connector under dash for telemetry and DTC scanning." to "Scan tool fails to power up (blown fuse)."),
        Triple("Crankshaft Position Sensor", ComponentCategory.ELECTRICAL, "Tracks crank angle and RPM for ignition synchronization." to "Random hot stalling and no-restart."),
        Triple("Camshaft Position Sensor", ComponentCategory.ELECTRICAL, "Identifies cylinder phase for sequential fuel injection." to "Long cranking time and code P0340."),
        Triple("Direct Ignition Coil-on-Plug", ComponentCategory.ELECTRICAL, "Transforms 12V into 30,000V directly atop each spark plug." to "Misfire under highway passing load."),
        Triple("433MHz TPMS Wheel Sensor", ComponentCategory.ELECTRICAL, "Transmits live tire pressure and temperature to the dash." to "Flashing amber TPMS warning icon."),
        Triple("Steering Column Clockspring", ComponentCategory.ELECTRICAL, "Maintains electrical contact to airbag and horn while turning." to "Airbag SRS light and dead horn buttons."),
        Triple("77GHz Forward Radar Sensor", ComponentCategory.ELECTRICAL, "Measures vehicle distance for Adaptive Cruise and AEB." to "Pre-Collision System Unavailable message."),
        Triple("Ultrasonic Parking Transducer", ComponentCategory.ELECTRICAL, "Emits acoustic pulses to detect curbs while parking." to "False continuous beeping in Reverse."),
        Triple("High-Amp Fusible Link Block", ComponentCategory.ELECTRICAL, "Protects main battery cables from short-circuit overload." to "Complete loss of power to major circuits."),
        Triple("Windshield Rain/Light Sensor", ComponentCategory.ELECTRICAL, "Optical prism triggering auto wipers and headlamps." to "Auto wipers sweep on dry glass."),
        Triple("Transponder Immobilizer Antenna", ComponentCategory.ELECTRICAL, "Verifies encrypted key fob rolling code before start." to "Flashing security key icon on cluster."),

        // SUSPENSION & DRIVETRAIN (18)
        Triple("MacPherson Strut Assembly", ComponentCategory.SUSPENSION, "Combines coil spring and hydraulic damper into one pivot." to "Bouncing after bumps and cupped tire wear."),
        Triple("Forged Lower Control Arm", ComponentCategory.SUSPENSION, "Links chassis subframe to the steering knuckle." to "Wandering steering and inner tire wear."),
        Triple("Control Arm Hydro-Bushing", ComponentCategory.SUSPENSION, "Fluid-filled rubber bushing isolating road harshness." to "Clunking under floorboards when braking."),
        Triple("Load-Bearing Ball Joint", ComponentCategory.SUSPENSION, "Spherical pivot allowing steering knuckle articulation." to "Squeaking on turns and clunk over potholes."),
        Triple("Outer Tie Rod End", ComponentCategory.SUSPENSION, "Connects steering rack rod to the wheel knuckle arm." to "Feathered front tires and loose steering play."),
        Triple("Inner Tie Rod Axial Socket", ComponentCategory.SUSPENSION, "Ball joint inside steering rack boot transmitting steer force." to "Horizontal wheel play at 3 and 9 o'clock."),
        Triple("Electric Power Steering Rack", ComponentCategory.SUSPENSION, "Converts steering wheel rotation into linear wheel movement." to "Heavy steering effort and EPS warning light."),
        Triple("Anti-Roll Stabilizer Bar", ComponentCategory.SUSPENSION, "Torsion bar resisting body lean during cornering." to "Excessive body roll on highway ramps."),
        Triple("Sway Bar End Link", ComponentCategory.SUSPENSION, "Connects stabilizer bar ends to the strut housing." to "Rapid clattering noise over small bumps."),
        Triple("Strut Upper Bearing Mount", ComponentCategory.SUSPENSION, "Lets the front strut and spring rotate smoothly when steering." to "Popping spring sound when turning while parked."),
        Triple("Tempered Helical Coil Spring", ComponentCategory.SUSPENSION, "Supports vehicle corner weight and ride height." to "Sagging corner ride height."),
        Triple("Rear Multi-Link Trailing Arm", ComponentCategory.SUSPENSION, "Controls rear wheel toe and longitudinal position." to "Rear instability over mid-corner bumps."),
        Triple("Cast Steering Knuckle", ComponentCategory.SUSPENSION, "Holds wheel hub, brake caliper, strut, and tie rod." to "Bent camber angle after curb impact."),
        Triple("Sealed Wheel Hub Bearing", ComponentCategory.SUSPENSION, "Enables low-friction wheel rotation under full vehicle load." to "Growling hum that changes pitch when swerving."),
        Triple("CV Axle Outer Joint", ComponentCategory.SUSPENSION, "Transfers drive torque to wheels while steering and flexing." to "Loud clicking when accelerating in a turn."),
        Triple("CV Axle Neoprene Boot", ComponentCategory.SUSPENSION, "Retains molybdenum grease inside the CV joint." to "Thick dark grease flung inside wheel well."),
        Triple("Propeller Shaft U-Joint", ComponentCategory.SUSPENSION, "Needle-bearing hinge allowing driveshaft articulation." to "Metallic clank when shifting Reverse to Drive."),
        Triple("Subframe Cradle Bushings", ComponentCategory.SUSPENSION, "Mounts the main engine/suspension cradle to the unibody." to "Heavy chassis shift during hard braking."),

        // FILTERS & INTAKE/CABIN (15)
        Triple("Pleated Engine Air Filter", ComponentCategory.FILTERS, "Traps abrasive road dust before intake air enters cylinders." to "Reduced MPG and sluggish throttle response."),
        Triple("Activated-Carbon Cabin Filter", ComponentCategory.FILTERS, "Filters pollen, brake dust, and exhaust fumes from HVAC air." to "Musty smell and weak dash vent airflow."),
        Triple("Spin-On Synthetic Oil Filter", ComponentCategory.FILTERS, "Captures metal wear particles down to 20 microns in oil." to "Oil pressure delay on cold start if bypass sticks."),
        Triple("Anti-Drainback Oil Filter Valve", ComponentCategory.FILTERS, "Silicone flap keeping oil in the filter when engine stops." to "Dry 2-second rattle on morning startup."),
        Triple("High-Pressure Inline Fuel Filter", ComponentCategory.FILTERS, "Screens rust and sediment before fuel reaches injectors." to "Engine sputtering at highway speeds."),
        Triple("In-Tank Fuel Pump Strainer Sock", ComponentCategory.FILTERS, "Pre-filters fuel drawn from the bottom of the gas tank." to "Whining fuel pump and fuel starvation."),
        Triple("Transmission Sump Media Filter", ComponentCategory.FILTERS, "Captures clutch friction material inside the ATF pan." to "Delayed transmission shifts when cold."),
        Triple("EVAP Charcoal Canister Filter", ComponentCategory.FILTERS, "Adsorbs fuel tank hydrocarbons to prevent vapor emissions." to "Check engine code P0442 or gas pump clicking off."),
        Triple("Crankcase Breather Filter Element", ComponentCategory.FILTERS, "Separates oil mist from crankcase ventilation vapors." to "Oil film inside turbo intake ducting."),
        Triple("HVAC Refrigerant Receiver-Drier", ComponentCategory.FILTERS, "Desiccant bag removing moisture and debris from A/C loop." to "Reduced A/C cooling efficiency."),
        Triple("Variable-Valve Timing Oil Screen", ComponentCategory.FILTERS, "Micro-mesh screen protecting VVT actuators from sludge." to "Cam timing over-retarded fault codes."),
        Triple("Turbocharger Oil Feed Banjo Screen", ComponentCategory.FILTERS, "Protects turbo journal bearings from oil debris." to "Premature turbocharger bearing starvation."),
        Triple("Power Steering Reservoir Screen", ComponentCategory.FILTERS, "Filters metal particles from hydraulic steering fluid." to "Foaming fluid and steering pump whine."),
        Triple("Differential Axle Breather Cap", ComponentCategory.FILTERS, "One-way vent allowing axle air expansion while blocking water." to "Blown pinion or axle shaft oil seals."),
        Triple("Hybrid Battery Cooling Intake Filter", ComponentCategory.FILTERS, "Keeps pet hair and dust out of HV battery blower fan." to "Hybrid battery thermal throttling alert.")
    )

    val parts: List<CarPartEncyclopediaEntry> = rawEntries.mapIndexed { index, triple ->
        val (name, category, details) = triple
        CarPartEncyclopediaEntry(
            id = "PART-%03d".format(index + 1),
            name = name,
            category = category,
            functionSummary = details.first,
            failureSymptom = details.second,
            maintenanceBestPractice = "Inspect during scheduled ${category.name.lowercase()} interval and replace using OEM-spec hardware."
        )
    }
}
