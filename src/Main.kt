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
    var lastServiceDays: Int
    var isUnderRepair: Boolean
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
    override var lastServiceDays: Int,
    override var isUnderRepair: Boolean) :
    PowerSource(),
    Maintainable {

}

data class NuclearPlant (
    override val id: Int,
    override val name: String,
    override  val outputKW: Double,
    override  val isEmergencyMode: Boolean,
    val coreTemp: Double,
    override var lastServiceDays: Int,
    override var isUnderRepair: Boolean
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
        } else {
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
    fun calculateTrueOutput(lossFactor: Double): Double {
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
    fun getHealthReport(id: Int): String {
        return powerSources.find { source -> source.id == id }?.let { source ->
            if (source.isEmergencyMode) {
                "Warning : ${source.name} is Emergency"
            } else {
                "${source.name} is stable"
            }
        } ?: "System error: Source not found"
    }

    /**
     * * canHandlePeakLoad	requiredKW: Double	Boolean	Check if there is at least one single power
     *  * source in the entire city that is NOT under repair and is capable of providing the required kilowatts on its own.
     */
    fun canHandlePeakLoad(requiredKW: Double): Boolean {
        return powerSources.filterIsInstance<Maintainable>().filter { source -> source.isUnderRepair != true }
            .filterIsInstance<PowerSource>().any { source -> source.outputKW > requiredKW }
    }

    /**
     * 1. The "Priority Repair" Sort
     * Goal: Learn how to organize data for a UI list.
     * Task: Create a function that returns a list of all power sources, but they must be sorted.
     * The sources with the highest outputKW should be at the top. If two sources have the same output, sort them alphabetically by name.
     */

    fun priorityRepairSort(): List<PowerSource> {
        return powerSources
            .sortedWith(
                compareByDescending<PowerSource> { it.outputKW }
                    .thenBy { it.name }
            )
    }

    /**
     * 2. The "Efficiency Filter" (Multiple Conditions)
     * Goal: Master complex filtering.
     * Task: Create a function that takes a minEfficiency: Double parameter. It should return only the SolarPanel objects that
     * are NOT under repair AND have an efficiencyRating higher than the parameter you passed in.
     * (Remember: You'll need to isolate the SolarPanels from the general list first!)
     */

    fun minEfficiency(parameter: Double): List<SolarPanel> {
        return powerSources.filterIsInstance<SolarPanel>()
            .filter { source -> !source.isUnderRepair && source.efficiencyRating > parameter }

    }

    /**
     * 3. The "City Statistics" (Grouping)
     * Goal: Learn how to categorize data (very common for dashboard screens).
     * Task: Create a function that counts how many devices are "Emergency" vs "Normal." It should return a formatted String like:
     * "System Status: 3 Emergency, 12 Normal".
     *
     * Challenge: Try to do this without creating manual counters (var count = 0). Use the collection tools to count based on the boolean state.
     */

    fun cityStats(): String {
        val (normal, emergency) = powerSources.partition { source -> source.isEmergencyMode }
        return ("Normal : ${normal.size}   Emergency : ${emergency.size}")
    }


    /** 4. The "Mass Maintenance" Update
     * Goal: Simulating a "Select All" or "Action" button.
     * Task: Create a function that "Services" the city. This function should find every Maintainable source and set its
     * lastServiceDays to 0 and isUnderRepair to false.
     *
     * Note: Since you are using data classes and val, you might need to think about how to update a MutableList
     * item or if you need to change your properties to var.
     */
    fun Services() {
        powerSources.filterIsInstance<Maintainable>()
            .filter { source -> source.isUnderRepair && source.lastServiceDays > 0 }
            .forEach { source -> source.isUnderRepair = false; source.lastServiceDays = 0 }
    }

    fun getServicedSources(): List<PowerSource> {

        return powerSources.map { source ->
            when (source) {
                is SolarPanel -> source.copy(isUnderRepair = false, lastServiceDays = 0)
                is NuclearPlant -> source.copy(isUnderRepair = false, lastServiceDays = 0)
                else -> source
            }
        }
    }

    /**
     * 5. The "Safety Override" (All/Any combo)
     * Goal: Critical system logic.
     * Task: Create a "Safety Check" function that returns a Boolean. It should return true only if:
     *
     * None of the NuclearPlant sources have a coreTemp over 1000.0.
     *
     * All SolarPanel sources have at least some efficiencyRating (greater than 0.0).
     *
     * At least one source in the entire city is currently producing power.
     */

    fun SafetyCheck(): Boolean {
       val one =  powerSources.filterIsInstance<NuclearPlant>().
               none { source -> source.coreTemp > 1000.0 }
        val two = powerSources.filterIsInstance<SolarPanel>().any{ it.efficiencyRating > 0.0}
        val three = powerSources.any { source -> source.outputKW > 0.0 }

        return  (one && two && three)
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