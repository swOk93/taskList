package tasklist
import kotlinx.datetime.*

object TaskList {
    private val taskList = mutableListOf<MutableList<String>>()
    fun add() {
        val task = mutableListOf<String>()
        var priority = ""
        var date = listOf<Int>()
        var time = listOf<Int>()
        while (priority.isEmpty()) {
            println("Input the task priority (C, H, N, L):")
            readln().uppercase().let { if ((it == "C") or (it == "H") or (it == "N") or (it == "L")) priority = it }
        }
        println("Input the date (yyyy-mm-dd):")
        while (date.size != 3) {
            println("Input the date (yyyy-mm-dd):")
            try { date = readln().split('-').map { it.toInt() } }
            catch (e: NumberFormatException) {
                println("The input date is invalid")
                continue
            }
            if (date.size == 3 && checkDateTime(date[0], date[1], date[2])) {
            } else date = listOf<Int>().also { println("The input date is invalid") }
        }
        while (time.size != 2) {
            println("Input the time (hh:mm):")
            try { time = readln().split(':').map { it.toInt() } }
            catch (e: NumberFormatException) {
                println("The input time is invalid")
                continue
            }
            if (time.size == 2 && checkDateTime(date[0], date[1], date[2], time[0], time[1])) {
            } else time = listOf<Int>().also { println("The input time is invalid") }
        }
        task.add("${date[0]}-${format(date[1])}-${format(date[2])} ${format(time[0])}:${format(time[1])} $priority")
        println("Input a new task (enter a blank line to end):")
        while (true) {
            readln().trim().also { if (it == "") {
                if (task.size < 2) println("The task is blank").also { return }
                else taskList.add(task).also { return }
            } else task.add(it) }
        }
    }
    private fun format(n: Int): String {
        var str = n.toString()
        if (str.length < 2) str = "0" + str
        return str
    }
    private fun checkDateTime(years: Int = 2000, months: Int = 1, days: Int = 1, hours: Int = 0, minutes: Int = 0): Boolean {
        return try {
            LocalDateTime(years, months, days, hours, minutes)
            true
        }
        catch (e: IllegalArgumentException) {
            false
        }
    }

    fun print() {
        if (taskList.isNotEmpty()) {
            for (x in 0 until taskList.size) {
                taskList[x][0] = taskList[x][0] + deadline(taskList[x][0].substringBefore(' '))
                for (y in 0 until taskList[x].size) {
                    println((if (y == 0) (x+1).toString().padEnd(3) else "   ") + taskList[x][y])
                }
                println()
            }
        } else println("No tasks have been input")
    }

    private fun deadline(s: String): String {
        val date = s.split('-').map { it.toInt() }
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+0")).date
        val numberOfDays = currentDate.daysUntil(LocalDate(date[0], date[1], date[2]))
        return if (numberOfDays > 0) "I" else if (numberOfDays < 0) "O" else "T"
    }

    fun delete() {
        TODO("Not yet implemented")
    }

    fun edit() {
        TODO("Not yet implemented")
    }

}

fun main() {
    while (true) {
        println("Input an action (add, print, edit, delete, end):")
        when (readln().lowercase()) {
            "add" -> TaskList.add()
            "print" -> TaskList.print()
            "delete" -> TaskList.delete()
            "edit" -> TaskList.edit()
            "end" -> print("Tasklist exiting!").also { return }
            else -> println("The input action is invalid")
        }
    }
}


