package tasklist
import kotlinx.datetime.*

fun checkDateTime(): Boolean {

}
object TaskList {
    private val taskList = mutableListOf<MutableList<String>>()
    fun add() {
        val task = mutableListOf<String>()
        var priority = ""
        var date: String
        var time: String
        while (priority.isEmpty()) {
            println("Input the task priority (C, H, N, L):")
            readln().uppercase().let { if ((it == "C") or (it == "H") or (it == "N") or (it == "L")) priority = it }
        }
        println("Input the date (yyyy-mm-dd):")
        while (checkDateTime())
        println("Input the time (hh:mm):")
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


