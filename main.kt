package tasklist
import kotlinx.datetime.*
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File

object TaskList {
    var taskList = mutableListOf<MutableList<String>>()
    
    fun add() {                               // Collects task details (date, time, priority, description) 
        val task = mutableListOf<String>()    // and adds a new task to taskList
        val priority = priority()
        task.add("${date()} ${time()} $priority")
        task.add(newTask())
        taskList.add(task)
    }
    
    private fun newTask(): String { // Collects a multi-line task description from the user
        var str = ""
        println("Input a new task (enter a blank line to end):")
        while (true) {
            readln().trim().also { if (it == "") {
                if (str.isEmpty()) println("The task is blank").also { return str }
                else return str
            } else if (it.length <= 44) {
                str += it + "\n"
            } else {
                var x = 0
                for (y in 44 until it.length step 44) {
                    str += it.substring(x, y) + "\n"
                    x = y
                }
                str += it.substring(x, it.length) + "\n"
            }
            }
        }
    }
    private fun priority(): String { // Gets valid task priority (C, H, N, L) from the user
        var str = ""
        while (str.isEmpty()) {
            println("Input the task priority (C, H, N, L):")
            readln().uppercase().let { if ((it == "C") or (it == "H") or (it == "N") or (it == "L")) str = it }
        }
        return str
    }
    private fun date(): String { // Gets a valid date (YYYY-MM-DD) from the user 
        var list = listOf<Int>()
        while (list.size != 3) {
            println("Input the date (yyyy-mm-dd):")
            try { list = readln().split('-').map { it.toInt() } }
            catch (e: NumberFormatException) {
                println("The input date is invalid")
                continue
            }
            if (list.size == 3 && checkDateTime(list[0], list[1], list[2])) {
            } else list = listOf<Int>().also { println("The input date is invalid") }
        }
        return "${list[0]}-${format(list[1])}-${format(list[2])}"
    }
    private fun time(): String { // Gets a valid time (HH:MM) from the user 
        var list = listOf<Int>()
        while (list.size != 2) {
            println("Input the time (hh:mm):")
            try { list = readln().split(':').map { it.toInt() } }
            catch (e: NumberFormatException) {
                println("The input time is invalid")
                continue
            }
            if (list.size == 2 && checkDateTime(2000, 1, 1, list[0], list[1])) {
            } else list = listOf<Int>().also { println("The input time is invalid") }
        }
        return "${format(list[0])}:${format(list[1])}"
    }
    
    private fun format(n: Int): String { // Ensures two-digit formatting for date/time components (e.g., 09 instead of 9)
        var str = n.toString()
        if (str.length < 2) str = "0$str"
        return str
    }
    // Validates date/time components using the kotlinx.datetime library
    private fun checkDateTime(years: Int = 2000, months: Int = 1, days: Int = 1, hours: Int = 0, minutes: Int = 0): Boolean {
        return try {  
            LocalDateTime(years, months, days, hours, minutes)
            true
        }
        catch (e: IllegalArgumentException) {
            false
        }
    }

    fun printTaskList() { // Nicely formats and prints all tasks in the taskList
        val dev = "+----+------------+-------+---+---+--------------------------------------------+\n" // dividing line

        if (taskList.isNotEmpty()) {
            print(dev + "| N  |    Date    | Time  | P | D |                   Task                     |\n" + dev)
            for (x in 0 until taskList.size) {
                val (date, time, priority) = taskList[x][0].split(' ').map { it }
                val taskLines = taskList[x][1].split("\n")
                println("| " + (x+1).toString().padEnd(3) + "| " + date + " | " + time + " | " +
                color(priority) + " | " + color(deadline(date)) + " |" + taskLines[0].padEnd(44) + "|")
                for (y in 1 until taskLines.size-1) { // each line for 44 symbols
                    println("|    |            |       |   |   |" + taskLines[y].padEnd(44) + "|")
                }
                print(dev)
            }
        } else println("No tasks have been input")
    }
    
    private fun color(color: String): String { // Adds color codes for priority and due date tags (visual clarity)
        return when (color) {
            "C", "O" -> "\u001B[101m \u001B[0m" // if priority = "C" or due tag = "O"
            "H", "T" -> "\u001B[103m \u001B[0m" // if priority = "H" or due tag = "T"
            "N", "I" -> "\u001B[102m \u001B[0m" // if priority = "N" or due tag = "I"
            "L" -> "\u001B[104m \u001B[0m" // if priority = "L"
            else -> "error"
        }
    }
    // Calculates a due date tag ('I' - In due, 'O' - Overdue, 'T' - Today)
    fun deadline(s: String): String {
        val date = LocalDate.parse(s)    
        val today = LocalDate.now()    
        return when {        
            date.isBefore(today) -> "O"        
            date.isEqual(today) -> "T"        
            else -> "I"    
        }
    }
    // private fun deadline(s: String): String { // Calculates a due date tag ('I' - In due, 'O' - Overdue, 'T' - Today)
    //     val date = s.split('-').map { it.toInt() } // get year, month and day
    //     val currentDate = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+0")).date
    //     val numberOfDays = currentDate.daysUntil(LocalDate(date[0], date[1], date[2])) // get the number of days until our date
    //     return if (numberOfDays > 0) "I" else if (numberOfDays < 0) "O" else "T"
    // }

    fun change(type: String) { // Allows deleting or editing a task based on the 'type' parameter
        printTaskList()
        if (taskList.isNotEmpty()) {
            var loop = true
            while (loop) {
                println("Input the task number (1-${taskList.size}):")
                val num = readln().let { if (Regex("^\\d{1,4}$").matches(it)) it.toInt() else 0 }
                num.let { if (it in 1..taskList.size) {
                    if (type == "delete") {
                        taskList.removeAt(it-1).also { println("The task is deleted") }.also { loop = false }
                    } else edit(it-1).also { loop = false }
                } else println("Invalid task number") }
            }
        }
    }

    private fun edit(num: Int) { // Handles editing specific fields of a task (priority, date, time, task) 
        var loop = true
        while (loop) {
            println("Input a field to edit (priority, date, time, task):")
            val (date, time, priority) = taskList[num][0].split(" ")
            when (readln().lowercase()) {
                "priority" -> taskList[num][0] = "$date $time ${priority()}".also { loop = false }
                "date" -> taskList[num][0] = "${date()} $time $priority".also { loop = false }
                "time" -> taskList[num][0] = "$date ${time()} $priority".also { loop = false }
                "task" -> taskList[num][1] = newTask().also { loop = false }
                else -> println("Invalid field")
            }
        }
        println("The task is changed")
    }
}

class Array2D(val data: MutableList<MutableList<String>>) {
    operator fun component1() = data
}
object SaveLoad { // Creates initial JSON representation
    val jsonFile = File("tasklist.json")
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    val type = Types.newParameterizedType(MutableList::class.java, Types.newParameterizedType(MutableList::class.java, String::class.java))
    val adapter = moshi.adapter<MutableList<MutableList<String>>>(type)
    val json = adapter.toJson(TaskList.taskList)
}
fun load() { // Loads task list from "tasklist.json" if the file exists
    if (SaveLoad.jsonFile.exists()) { // just get back 2Dlist from json file. I found only this way to do it.
        TaskList.taskList = SaveLoad.adapter.fromJson(SaveLoad.jsonFile.readText())!!
    }
}
fun save() { // Saves the current task list to "tasklist.json"
    val json = SaveLoad.adapter.toJson(TaskList.taskList)
    SaveLoad.jsonFile.writeText(json) // write the data to file "tasklist.json"
}
fun main() {
    load() // Load saved tasks on program start
    while (true) { // Prompts the user for an action, executes the relevant task management functions
        println("Input an action (add, print, edit, delete, end):")
        when (readln().lowercase()) {
            "add" -> TaskList.add()
            "print" -> TaskList.printTaskList()
            "delete" -> TaskList.change("delete")
            "edit" -> TaskList.change("edit")
            "end" -> println("Tasklist exiting!").also { save().also { return } } // Ends the program and saves data when the user enters "end"
            else -> println("The input action is invalid")
        }
    }
}
