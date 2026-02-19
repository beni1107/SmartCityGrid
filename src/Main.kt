/**
 * 🏗️ The "Smart City" Evolution: The Hard Twist
 * In this version, we are adding dynamic state. Some power sources can be "Active" or "Inactive," and some have "Maintenance Alerts."
 *
 * The Data Structure
 * Interface Maintainable:
 *
 * Property: lastServiceDays: Int
 *
 * Property: isUnderRepair: Boolean
 */
interface Maintainable {
    val lastServiceDays: Int
    val isUnderRepair: Boolean
}

/** Base Class PowerSource:
 *
 * Properties: id: Int, name: String, outputKW: Double, isEmergencyMode: Boolean
 */
abstract class PowerSource {
    abstract val id: Int
    abstract val name: String
    abstract val outputKW: Double
    abstract val isEmergencyMode: Boolean
}
/* * Subclasses:
 *
 * SolarPanel: Implements Maintainable, plus a efficiencyRating: Double (0.0 to 1.0).
 *
 * NuclearPlant: Implements Maintainable, plus a coreTemp: Double.
 */
data class SolarPanel(
    override val id: Int,
    override val name:String,
    override val outputKW: Double,
    override val isEmergencyMode: Boolean,
     val efficiencyRating:Double,
    override val lastServiceDays: Int,
    override val isUnderRepair: Boolean) :
    PowerSource(),
    Maintainable {

}

data class NuclearPlant (
    override val id: Int,
    override val name: String,
    override  val outputKW: Double,
    override  val isEmergencyMode: Boolean,
    val coreTemp: Double,
    override val lastServiceDays: Int,
    override val isUnderRepair: Boolean
) : PowerSource(),
    Maintainable {

}
/* * The Manager: CityGridManager
 * Your class should manage a collection of these sources. Here are your 5 "Boss" requirements:
 */
class CityGridManager {
    private val powerSources = mutableListOf<PowerSource>()

    /**
     * Add new item to list
     */
    fun addElement(element: PowerSource?) {
            if (element != null) {
                println("Adding new PowerSource to the GridManager")
                powerSources.add(element)
            }else {
                println("Element is null and canot be added!")
            }
    }

    /**
     * getCriticalMaintenance	None	List<PowerSource>	Find every source that is currently under repair OR hasn't been
     *  * serviced in more than 365 days.
     */
    fun getCriticalMaintenance(): List<PowerSource> {
        return powerSources.filterIsInstance<Maintainable>()
            .filter { source -> source.lastServiceDays > 365 || source.isUnderRepair }
            .filterIsInstance<PowerSource>() // tole casta nazaj cel list Maintainnable v PowerSource
    }

    /**
     *  * calculateTrueOutput	lossFactor: Double	Double	Calculate the total power of the whole city, but multiply
     *  * the final sum by the loss factor (e.g., 0.9 for 10% line loss).
     *  *
     */
    fun calculateTrueOutput(lossFactor: Double) : Double {
        val sumPower = powerSources.ifEmpty { return 0.0 }.sumOf { source -> source.outputKW }
        val multiplier = when {
            lossFactor < 10 -> 0.9
            lossFactor < 20 -> 0.13
            lossFactor < 30 -> 0.15
            else -> 1.0 // Default if no logic matches
        }
        return sumPower * multiplier
        }

    /**
     * isCityInDarkness	None	Boolean	Determine if the system has reached a state where absolutely zero power
     *  sources are providing any output at all.
     */
    fun isCityInDarkness(): Boolean {
        return powerSources.none { source -> source.outputKW > 0.0 }
    }

/**
 *  * getHealthReport	id: Int	String	Look for a specific source. If it exists, check its emergency mode:
 *  * if it's on, return "WARNING: [Name] in Emergency"; if off, return "[Name] is stable." If the ID doesn't exist, return "System error:
 *  * Source not found."
 *  *
 */
fun getHealthReport (id: Int) :String{
    return powerSources.find { source -> source.id == id }?.let {
        source ->
        if (source.isEmergencyMode) {
             "Warning : ${source.name} is Emergency"
        }else {
             "${source.name} is stable"
        }
    }  ?:  "System error: Source not found"
}

/**
 * * canHandlePeakLoad	requiredKW: Double	Boolean	Check if there is at least one single power
 *  * source in the entire city that is NOT under repair and is capable of providing the required kilowatts on its own.
 */
fun canHandlePeakLoad (requiredKW: Double): Boolean {
    return powerSources.filterIsInstance<Maintainable>().
           filter { source -> source.isUnderRepair != true }.
            filterIsInstance<PowerSource>().any{source -> source.outputKW > requiredKW}
}



}


/*
 * Function Name	Parameters	Return Type	The Mission (Plain Text)
 *




 *
 * canHandlePeakLoad	requiredKW: Double	Boolean	Check if there is at least one single power
 * source in the entire city that is NOT under repair and is capable of providing the required kilowatts on its own.
 *
 *
 */