package com.autopulse.app.domain.catalog

import com.autopulse.app.domain.model.ComponentCategory
import com.autopulse.app.domain.model.Hotspot3D
import com.autopulse.app.domain.model.MaintenanceRule
import com.autopulse.app.domain.model.UrgencyLevel

/**
 * Pre-loaded OEM Maintenance Rules Catalog covering 42 distinct vehicle subsystems
 * across standard intervals (3,000-5,000 mi up to 100,000 mi).
 */
object OemMaintenanceCatalog {

    val rules: List<MaintenanceRule> = listOf(
        // POWERTRAIN (10 rules)
        MaintenanceRule(
            id = "ENGINE_OIL_SYNTHETIC",
            title = "Full Synthetic Engine Oil & Filter (5W-30)",
            category = ComponentCategory.POWERTRAIN,
            intervalMiles = 5000,
            criticalThresholdMiles = 6000,
            description = "Thermal viscosity breakdown causes rod bearing wear and catastrophic engine seizure.",
            urgencyLevel = UrgencyLevel.CRITICAL_SAFETY,
            estimatedCostUsd = 95,
            hotspot = Hotspot3D(0.35f, 0.42f, 0.0f, "Engine Bay")
        ),
        MaintenanceRule(
            id = "SPARK_PLUGS_IRIDIUM",
            title = "Iridium/Platinum Ignition Spark Plugs",
            category = ComponentCategory.POWERTRAIN,
            intervalMiles = 60000,
            criticalThresholdMiles = 68000,
            description = "Worn electrodes cause cylinder misfires (P0300) and melt catalytic substrates.",
            urgencyLevel = UrgencyLevel.RECOMMENDED,
            estimatedCostUsd = 280,
            hotspot = Hotspot3D(0.30f, 0.48f, 0.02f, "Engine Bay")
        ),
        MaintenanceRule(
            id = "TIMING_BELT_WATER_PUMP",
            title = "Timing Belt, Hydraulic Tensioner & Water Pump",
            category = ComponentCategory.POWERTRAIN,
            intervalMiles = 90000,
            criticalThresholdMiles = 95000,
            description = "Belt snap on interference engines causes piston-to-valve collision and total engine failure.",
            urgencyLevel = UrgencyLevel.CRITICAL_SAFETY,
            estimatedCostUsd = 1150,
            hotspot = Hotspot3D(0.43f, 0.39f, 0.0f, "Engine Bay")
        ),
        MaintenanceRule(
            id = "SERPENTINE_DRIVE_BELT",
            title = "Serpentine Accessory Drive Belt & Idler Pulley",
            category = ComponentCategory.POWERTRAIN,
            intervalMiles = 75000,
            criticalThresholdMiles = 82000,
            description = "Belt snap disables alternator charging, water pump circulation, and hydraulic assist.",
            urgencyLevel = UrgencyLevel.CRITICAL_SAFETY,
            estimatedCostUsd = 210,
            hotspot = Hotspot3D(0.44f, 0.40f, 0.08f, "Engine Bay")
        ),
        MaintenanceRule(
            id = "PCV_VALVE_ASSEMBLY",
            title = "Positive Crankcase Ventilation (PCV) Valve",
            category = ComponentCategory.POWERTRAIN,
            intervalMiles = 60000,
            criticalThresholdMiles = 70000,
            description = "Clogged PCV builds crankcase pressure, blowing cam/main seals and increasing oil burn.",
            urgencyLevel = UrgencyLevel.ROUTINE,
            estimatedCostUsd = 85,
            hotspot = Hotspot3D(0.28f, 0.49f, -0.10f, "Engine Bay")
        ),
        MaintenanceRule(
            id = "THROTTLE_BODY_DECARB",
            title = "Electronic Throttle Body Carbon Cleaning",
            category = ComponentCategory.POWERTRAIN,
            intervalMiles = 45000,
            criticalThresholdMiles = 52000,
            description = "Carbon coking on the butterfly plate triggers unstable idle and limp mode.",
            urgencyLevel = UrgencyLevel.ROUTINE,
            estimatedCostUsd = 135,
            hotspot = Hotspot3D(0.36f, 0.47f, 0.11f, "Engine Bay")
        ),
        MaintenanceRule(
            id = "FUEL_INJECTOR_CLEANING",
            title = "Direct Injection Fuel System & Valve Cleaning",
            category = ComponentCategory.POWERTRAIN,
            intervalMiles = 30000,
            criticalThresholdMiles = 35000,
            description = "Intake valve carbon deposits restrict airflow and degrade cold-start fuel atomization.",
            urgencyLevel = UrgencyLevel.RECOMMENDED,
            estimatedCostUsd = 175,
            hotspot = Hotspot3D(0.32f, 0.45f, -0.08f, "Engine Bay")
        ),
        MaintenanceRule(
            id = "HYDRAULIC_ENGINE_MOUNTS",
            title = "Hydraulic Engine & Torque Strut Mounts",
            category = ComponentCategory.POWERTRAIN,
            intervalMiles = 80000,
            criticalThresholdMiles = 90000,
            description = "Collapsed mounts transfer drivetrain clunking and stress exhaust flex joints.",
            urgencyLevel = UrgencyLevel.RECOMMENDED,
            estimatedCostUsd = 490,
            hotspot = Hotspot3D(0.33f, 0.31f, 0.18f, "Engine Bay")
        ),
        MaintenanceRule(
            id = "VALVE_CLEARANCE_LASH",
            title = "Valvetrain Lash & Cam Lobe Inspection",
            category = ComponentCategory.POWERTRAIN,
            intervalMiles = 100000,
            criticalThresholdMiles = 110000,
            description = "Tight exhaust valve clearance risks burnt valves and cylinder compression loss.",
            urgencyLevel = UrgencyLevel.RECOMMENDED,
            estimatedCostUsd = 420,
            hotspot = Hotspot3D(0.31f, 0.50f, 0.0f, "Engine Bay")
        ),
        MaintenanceRule(
            id = "EXHAUST_CATALYST_O2",
            title = "Upstream Air-Fuel O2 Sensor & Catalyst Audit",
            category = ComponentCategory.POWERTRAIN,
            intervalMiles = 90000,
            criticalThresholdMiles = 100000,
            description = "Lazy oxygen sensor response degrades fuel trim and overheats the catalytic converter.",
            urgencyLevel = UrgencyLevel.RECOMMENDED,
            estimatedCostUsd = 340,
            hotspot = Hotspot3D(-0.10f, 0.22f, 0.0f, "Transmission")
        ),

        // BRAKING (7 rules)
        MaintenanceRule(
            id = "FRONT_BRAKE_PADS",
            title = "Front Ceramic Brake Pads & Wear Sensors",
            category = ComponentCategory.BRAKING,
            intervalMiles = 35000,
            criticalThresholdMiles = 38000,
            description = "Pads worn below 3mm score brake rotors and lengthen emergency stopping distance.",
            urgencyLevel = UrgencyLevel.CRITICAL_SAFETY,
            estimatedCostUsd = 295,
            hotspot = Hotspot3D(0.33f, 0.22f, 0.34f, "Front Axle")
        ),
        MaintenanceRule(
            id = "REAR_BRAKE_PADS",
            title = "Rear Brake Pads & EPB Actuator Calibration",
            category = ComponentCategory.BRAKING,
            intervalMiles = 45000,
            criticalThresholdMiles = 50000,
            description = "Metal-on-metal rear pad wear degrades wet braking stability and parking brake hold.",
            urgencyLevel = UrgencyLevel.CRITICAL_SAFETY,
            estimatedCostUsd = 275,
            hotspot = Hotspot3D(-0.33f, 0.22f, 0.34f, "Rear Suspension")
        ),
        MaintenanceRule(
            id = "FRONT_BRAKE_ROTORS",
            title = "Front Ventilated Brake Disc Rotors",
            category = ComponentCategory.BRAKING,
            intervalMiles = 65000,
            criticalThresholdMiles = 72000,
            description = "Warped or thin rotors below discard thickness cause pedal pulsation and thermal fade.",
            urgencyLevel = UrgencyLevel.CRITICAL_SAFETY,
            estimatedCostUsd = 410,
            hotspot = Hotspot3D(0.33f, 0.22f, -0.34f, "Front Axle")
        ),
        MaintenanceRule(
            id = "REAR_BRAKE_ROTORS",
            title = "Rear Brake Disc Rotors & Hat Inspection",
            category = ComponentCategory.BRAKING,
            intervalMiles = 75000,
            criticalThresholdMiles = 82000,
            description = "Heat-scored rear rotors reduce ABS modulation accuracy.",
            urgencyLevel = UrgencyLevel.RECOMMENDED,
            estimatedCostUsd = 370,
            hotspot = Hotspot3D(-0.33f, 0.22f, -0.34f, "Rear Suspension")
        ),
        MaintenanceRule(
            id = "CALIPER_SLIDE_PINS",
            title = "Brake Caliper Guide Pins & Silicone Lubrication",
            category = ComponentCategory.BRAKING,
            intervalMiles = 30000,
            criticalThresholdMiles = 35000,
            description = "Seized caliper slide pins cause uneven inner pad tapering and dragging brakes.",
            urgencyLevel = UrgencyLevel.RECOMMENDED,
            estimatedCostUsd = 115,
            hotspot = Hotspot3D(0.34f, 0.24f, 0.34f, "Front Axle")
        ),
        MaintenanceRule(
            id = "FLEXIBLE_BRAKE_HOSES",
            title = "Hydraulic EPDM Flexible Brake Hoses",
            category = ComponentCategory.BRAKING,
            intervalMiles = 60000,
            criticalThresholdMiles = 70000,
            description = "Dry-rotted rubber brake lines can rupture under panic braking hydraulic spikes.",
            urgencyLevel = UrgencyLevel.CRITICAL_SAFETY,
            estimatedCostUsd = 260,
            hotspot = Hotspot3D(0.28f, 0.25f, 0.30f, "Front Axle")
        ),
        MaintenanceRule(
            id = "ABS_WHEEL_SPEED_SENSORS",
            title = "ABS Magnetic Tone Rings & Wheel Speed Sensors",
            category = ComponentCategory.BRAKING,
            intervalMiles = 60000,
            criticalThresholdMiles = 70000,
            description = "Metallic debris on ABS sensors disables anti-lock braking and stability control.",
            urgencyLevel = UrgencyLevel.CRITICAL_SAFETY,
            estimatedCostUsd = 180,
            hotspot = Hotspot3D(0.32f, 0.20f, 0.32f, "Front Axle")
        ),

        // FLUIDS (8 rules)
        MaintenanceRule(
            id = "BRAKE_FLUID_DOT4",
            title = "DOT 4 Hygroscopic Brake Fluid Flush",
            category = ComponentCategory.FLUIDS,
            intervalMiles = 30000,
            criticalThresholdMiles = 35000,
            description = "Moisture-laden brake fluid boils under hard braking, causing vapor lock and pedal drop.",
            urgencyLevel = UrgencyLevel.CRITICAL_SAFETY,
            estimatedCostUsd = 145,
            hotspot = Hotspot3D(0.22f, 0.46f, 0.20f, "Engine Bay")
        ),
        MaintenanceRule(
            id = "COOLANT_SYSTEM_FLUSH",
            title = "Engine Radiator OAT/HOAT Coolant Flush",
            category = ComponentCategory.FLUIDS,
            intervalMiles = 30000,
            criticalThresholdMiles = 36000,
            description = "Depleted acidic coolant corrodes aluminum cylinder heads, water pumps, and heater cores.",
            urgencyLevel = UrgencyLevel.CRITICAL_SAFETY,
            estimatedCostUsd = 175,
            hotspot = Hotspot3D(0.45f, 0.41f, 0.0f, "Engine Bay")
        ),
        MaintenanceRule(
            id = "TRANSMISSION_FLUID_EXCHANGE",
            title = "Automatic / Dual-Clutch Transmission Fluid & Filter",
            category = ComponentCategory.FLUIDS,
            intervalMiles = 60000,
            criticalThresholdMiles = 65000,
            description = "Sheared ATF causes clutch pack slippage, harsh shifts, and torque converter shudder.",
            urgencyLevel = UrgencyLevel.CRITICAL_SAFETY,
            estimatedCostUsd = 325,
            hotspot = Hotspot3D(0.16f, 0.30f, 0.0f, "Transmission")
        ),
        MaintenanceRule(
            id = "FRONT_DIFFERENTIAL_OIL",
            title = "Front Transaxle Differential Gear Oil Flush",
            category = ComponentCategory.FLUIDS,
            intervalMiles = 60000,
            criticalThresholdMiles = 66000,
            description = "Worn hypoid gear oil leads to ring-and-pinion whine and bearing pitting.",
            urgencyLevel = UrgencyLevel.RECOMMENDED,
            estimatedCostUsd = 160,
            hotspot = Hotspot3D(0.28f, 0.25f, 0.0f, "Front Axle")
        ),
        MaintenanceRule(
            id = "REAR_DIFFERENTIAL_OIL",
            title = "Rear Differential & Limited-Slip Fluid Service",
            category = ComponentCategory.FLUIDS,
            intervalMiles = 50000,
            criticalThresholdMiles = 55000,
            description = "Degraded rear axle fluid causes LSD chatter and pinion seal overheating.",
            urgencyLevel = UrgencyLevel.RECOMMENDED,
            estimatedCostUsd = 175,
            hotspot = Hotspot3D(-0.32f, 0.25f, 0.0f, "Rear Suspension")
        ),
        MaintenanceRule(
            id = "TRANSFER_CASE_FLUID",
            title = "AWD / 4WD Transfer Case Fluid Exchange",
            category = ComponentCategory.FLUIDS,
            intervalMiles = 45000,
            criticalThresholdMiles = 50000,
            description = "Old transfer case fluid causes AWD clutch binding during low-speed turns.",
            urgencyLevel = UrgencyLevel.RECOMMENDED,
            estimatedCostUsd = 185,
            hotspot = Hotspot3D(0.05f, 0.26f, 0.0f, "Transmission")
        ),
        MaintenanceRule(
            id = "AC_REFRIGERANT_LUBRICATION",
            title = "HVAC Compressor PAG Oil & Refrigerant Charge",
            category = ComponentCategory.FLUIDS,
            intervalMiles = 50000,
            criticalThresholdMiles = 60000,
            description = "Low refrigerant starves the A/C compressor of oil and impairs windshield defogging.",
            urgencyLevel = UrgencyLevel.ROUTINE,
            estimatedCostUsd = 215,
            hotspot = Hotspot3D(0.39f, 0.36f, 0.20f, "Engine Bay")
        ),
        MaintenanceRule(
            id = "WINDSHIELD_WASHER_DEICER",
            title = "Windshield Optical Washer Fluid & Nozzle Purge",
            category = ComponentCategory.FLUIDS,
            intervalMiles = 10000,
            criticalThresholdMiles = 12000,
            description = "Empty or clogged washer nozzles impair forward ADAS camera and driver visibility.",
            urgencyLevel = UrgencyLevel.ROUTINE,
            estimatedCostUsd = 25,
            hotspot = Hotspot3D(0.42f, 0.45f, -0.24f, "Engine Bay")
        ),

        // ELECTRICAL (6 rules)
        MaintenanceRule(
            id = "BATTERY_12V_AGM_TEST",
            title = "12V AGM Battery CCA Load & Conductance Test",
            category = ComponentCategory.ELECTRICAL,
            intervalMiles = 36000,
            criticalThresholdMiles = 42000,
            description = "Sulfated 12V battery cells cause sudden no-start stranding and ECU voltage drops.",
            urgencyLevel = UrgencyLevel.CRITICAL_SAFETY,
            estimatedCostUsd = 245,
            hotspot = Hotspot3D(0.38f, 0.44f, -0.22f, "Engine Bay")
        ),
        MaintenanceRule(
            id = "ALTERNATOR_DIODE_RIPPLE",
            title = "Alternator Voltage Regulator & Diode Ripple Check",
            category = ComponentCategory.ELECTRICAL,
            intervalMiles = 75000,
            criticalThresholdMiles = 85000,
            description = "Failing voltage regulator causes mid-drive stalling once battery reserve depletes.",
            urgencyLevel = UrgencyLevel.CRITICAL_SAFETY,
            estimatedCostUsd = 520,
            hotspot = Hotspot3D(0.41f, 0.39f, -0.12f, "Engine Bay")
        ),
        MaintenanceRule(
            id = "STARTER_DRAW_INSPECTION",
            title = "Starter Motor Amperage Draw & Solenoid Check",
            category = ComponentCategory.ELECTRICAL,
            intervalMiles = 90000,
            criticalThresholdMiles = 100000,
            description = "Pitted solenoid contacts cause intermittent single-click no-crank failures.",
            urgencyLevel = UrgencyLevel.RECOMMENDED,
            estimatedCostUsd = 460,
            hotspot = Hotspot3D(0.25f, 0.33f, 0.05f, "Engine Bay")
        ),
        MaintenanceRule(
            id = "IGNITION_COIL_PACKS",
            title = "Direct Ignition Coil-on-Plug (COP) Insulation Test",
            category = ComponentCategory.ELECTRICAL,
            intervalMiles = 75000,
            criticalThresholdMiles = 85000,
            description = "Cracked coil boots arc high voltage to the cylinder head, causing under-load misfires.",
            urgencyLevel = UrgencyLevel.RECOMMENDED,
            estimatedCostUsd = 360,
            hotspot = Hotspot3D(0.29f, 0.50f, 0.04f, "Engine Bay")
        ),
        MaintenanceRule(
            id = "TPMS_TRANSDUCER_BATTERIES",
            title = "TPMS Tire Pressure Sensor Battery & Stem Audit",
            category = ComponentCategory.ELECTRICAL,
            intervalMiles = 70000,
            criticalThresholdMiles = 80000,
            description = "Depleted TPMS lithium transmitters fail to warn of dangerous highway blowouts.",
            urgencyLevel = UrgencyLevel.RECOMMENDED,
            estimatedCostUsd = 240,
            hotspot = Hotspot3D(-0.33f, 0.18f, -0.36f, "Rear Suspension")
        ),
        MaintenanceRule(
            id = "HEADLAMP_PHOTOMETRIC_AIM",
            title = "LED/Matrix Headlamp Aim & Exterior Lighting Check",
            category = ComponentCategory.ELECTRICAL,
            intervalMiles = 30000,
            criticalThresholdMiles = 36000,
            description = "Misaligned headlamps reduce night hazard reaction time and blind oncoming drivers.",
            urgencyLevel = UrgencyLevel.ROUTINE,
            estimatedCostUsd = 95,
            hotspot = Hotspot3D(0.46f, 0.38f, 0.28f, "Engine Bay")
        ),

        // SUSPENSION (7 rules)
        MaintenanceRule(
            id = "TIRE_ROTATION_BALANCE",
            title = "Tire Rotation, Road-Force Balance & Tread Audit",
            category = ComponentCategory.SUSPENSION,
            intervalMiles = 10000,
            criticalThresholdMiles = 12000,
            description = "Unrotated tires develop cupping wear, hydroplaning risk, and AWD differential strain.",
            urgencyLevel = UrgencyLevel.RECOMMENDED,
            estimatedCostUsd = 65,
            hotspot = Hotspot3D(0.33f, 0.18f, 0.36f, "Front Axle")
        ),
        MaintenanceRule(
            id = "FOUR_WHEEL_ALIGNMENT",
            title = "Four-Wheel Laser Camber, Caster & Toe Alignment",
            category = ComponentCategory.SUSPENSION,
            intervalMiles = 15000,
            criticalThresholdMiles = 18000,
            description = "Improper toe angle shreds inner tire shoulders and causes highway wandering.",
            urgencyLevel = UrgencyLevel.RECOMMENDED,
            estimatedCostUsd = 135,
            hotspot = Hotspot3D(0.31f, 0.20f, -0.35f, "Front Axle")
        ),
        MaintenanceRule(
            id = "STRUTS_SHOCK_DAMPERS",
            title = "Front MacPherson Struts & Rear Shock Dampers",
            category = ComponentCategory.SUSPENSION,
            intervalMiles = 70000,
            criticalThresholdMiles = 80000,
            description = "Leaking hydraulic dampers cause severe nose-dive, body roll, and loss of tire contact.",
            urgencyLevel = UrgencyLevel.CRITICAL_SAFETY,
            estimatedCostUsd = 980,
            hotspot = Hotspot3D(0.30f, 0.35f, 0.28f, "Front Axle")
        ),
        MaintenanceRule(
            id = "CONTROL_ARM_BALL_JOINTS",
            title = "Lower Control Arm Hydro-Bushings & Ball Joints",
            category = ComponentCategory.SUSPENSION,
            intervalMiles = 75000,
            criticalThresholdMiles = 85000,
            description = "Torn bushings and loose ball joints cause clunking and sudden suspension collapse.",
            urgencyLevel = UrgencyLevel.CRITICAL_SAFETY,
            estimatedCostUsd = 640,
            hotspot = Hotspot3D(0.29f, 0.21f, 0.26f, "Front Axle")
        ),
        MaintenanceRule(
            id = "STEERING_TIE_ROD_ENDS",
            title = "Inner & Outer Steering Tie Rod Ends",
            category = ComponentCategory.SUSPENSION,
            intervalMiles = 80000,
            criticalThresholdMiles = 88000,
            description = "Play in tie rod ball sockets creates steering dead-zones and risk of steering separation.",
            urgencyLevel = UrgencyLevel.CRITICAL_SAFETY,
            estimatedCostUsd = 350,
            hotspot = Hotspot3D(0.35f, 0.21f, 0.24f, "Front Axle")
        ),
        MaintenanceRule(
            id = "SWAY_BAR_END_LINKS",
            title = "Anti-Roll Stabilizer Bar Links & Bushings",
            category = ComponentCategory.SUSPENSION,
            intervalMiles = 60000,
            criticalThresholdMiles = 70000,
            description = "Worn sway bar links rattle over bumps and increase cornering body lean.",
            urgencyLevel = UrgencyLevel.ROUTINE,
            estimatedCostUsd = 210,
            hotspot = Hotspot3D(-0.30f, 0.23f, -0.26f, "Rear Suspension")
        ),
        MaintenanceRule(
            id = "CV_AXLE_BOOTS_JOINTS",
            title = "CV Axle Half-Shafts & Neoprene Grease Boots",
            category = ComponentCategory.SUSPENSION,
            intervalMiles = 30000,
            criticalThresholdMiles = 36000,
            description = "Split CV boots fling out grease, causing joint clicking on turns and axle snap.",
            urgencyLevel = UrgencyLevel.CRITICAL_SAFETY,
            estimatedCostUsd = 420,
            hotspot = Hotspot3D(0.30f, 0.24f, 0.25f, "Front Axle")
        ),

        // FILTERS (4 rules)
        MaintenanceRule(
            id = "ENGINE_INTAKE_AIR_FILTER",
            title = "Engine Intake Pleated Air Filter Element",
            category = ComponentCategory.FILTERS,
            intervalMiles = 15000,
            criticalThresholdMiles = 18000,
            description = "Restricted intake airflow reduces horsepower, throttle response, and MAF accuracy.",
            urgencyLevel = UrgencyLevel.ROUTINE,
            estimatedCostUsd = 45,
            hotspot = Hotspot3D(0.40f, 0.46f, 0.14f, "Engine Bay")
        ),
        MaintenanceRule(
            id = "CABIN_HEPA_AIR_FILTER",
            title = "Activated-Carbon HEPA Cabin Air Filter",
            category = ComponentCategory.FILTERS,
            intervalMiles = 12000,
            criticalThresholdMiles = 15000,
            description = "Clogged cabin filter strains the HVAC blower motor and traps allergens/exhaust soot.",
            urgencyLevel = UrgencyLevel.ROUTINE,
            estimatedCostUsd = 55,
            hotspot = Hotspot3D(0.14f, 0.48f, -0.18f, "Engine Bay")
        ),
        MaintenanceRule(
            id = "FUEL_LINE_FILTER_SCREEN",
            title = "High-Pressure Fuel Filter & Water Separator",
            category = ComponentCategory.FILTERS,
            intervalMiles = 45000,
            criticalThresholdMiles = 52000,
            description = "Clogged fuel media starves the high-pressure fuel pump and causes highway hesitation.",
            urgencyLevel = UrgencyLevel.RECOMMENDED,
            estimatedCostUsd = 140,
            hotspot = Hotspot3D(-0.15f, 0.20f, 0.12f, "Transmission")
        ),
        MaintenanceRule(
            id = "MULTI_POINT_SAFETY_INSPECTION",
            title = "50-Point Chassis, Undercarriage & Leak Inspection",
            category = ComponentCategory.FILTERS,
            intervalMiles = 10000,
            criticalThresholdMiles = 12500,
            description = "Catches early fluid seepage, exhaust hangers, and tire hazards before breakdown.",
            urgencyLevel = UrgencyLevel.ROUTINE,
            estimatedCostUsd = 40,
            hotspot = Hotspot3D(0.0f, 0.25f, 0.0f, "Transmission")
        )
    )
}
