package tasklist
import kotlinx.datetime.*
import java.time.format.DateTimeParseException

fun checkDateTime(date: String): Boolean {
    return try {
        val check = Instant.parse(date)
        true
    }
    catch (e: IllegalArgumentException) {
//        println("$ is not a leap year")
        false
    }
}
object TaskList {
    private val taskList = mutableListOf<MutableList<String>>()
    fun add() {
        val task = mutableListOf<String>()
        var priority = ""
        var date = ""
        var time = ""
        while (priority.isEmpty()) {
            println("Input the task priority (C, H, N, L):")
            readln().uppercase().let { if ((it == "C") or (it == "H") or (it == "N") or (it == "L")) priority = it }
        }
        println("Input the date (yyyy-mm-dd):")
        while (date.isEmpty()) {
            println("Input the date (yyyy-mm-dd):")
            readln().let { if (checkDateTime(it + "T00:00Z")) date = it else println("The input time is invalid") }
        }
        while (time.isEmpty()) {
            println("Input the time (hh:mm):")
            readln().let { if (checkDateTime(date + "T" + it + "Z")) time = it else println("The input time is invalid") }
        }
        task.add(date + time + priority)
        println("Input a new task (enter a blank line to end):")
        while (true) {
            readln().trim().also { if (it == "") {
                if (task.isEmpty()) println("The task is blank").also { return }
                else taskList.add(task).also { return }
            } else task.add(it) }
        }
    }

    fun print() {
        if (taskList.isNotEmpty()) {
            for (x in 0 until taskList.size) {
                for (y in 0 until taskList[x].size) {
                    println((if (y == 0) (x+1).toString().padEnd(3) else "   ") + taskList[x][y])
                }
                println()
            }
        } else println("No tasks have been input")
    }

}

fun main() {
    while (true) {
        println("Input an action (add, print, end):")
        when (readln().lowercase()) {
            "add" -> TaskList.add()
            "print" -> TaskList.print()
            "end" -> print("Tasklist exiting!").also { return }
            else -> println("The input action is invalid")
        }
    }
}


