package tasklist
import kotlinx.datetime.*

object TaskList {
    private val taskList = mutableListOf<MutableList<String>>()
    fun add() {
        val task = mutableListOf<String>()
        val priority = priority()
        task.add("${date()} ${time()} $priority")
        task.add(newTask())
        taskList.add(task)
    }
    private fun newTask(): String {
        var str = ""
        println("Input a new task (enter a blank line to end):")
        while (true) {
            readln().trim().also { if (it == "") {
                if (str.isEmpty()) println("The task is blank").also { return str }
                else return str
            } else str += "   $it\n"
            }
        }
    }
    private fun priority(): String {
        var str = ""
        while (str.isEmpty()) {
            println("Input the task priority (C, H, N, L):")
            readln().uppercase().let {
                when (it) {
                    "C" -> str = "\u001B[101m \u001B[0m"
                    "H" -> str = "\u001B[103m \u001B[0m"
                    "N" -> str = "\u001B[102m \u001B[0m"
                    "L" -> str = "\u001B[104m \u001B[0m"
                }
            }
        }
        return str
    }
    private fun date(): String {
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
    private fun time(): String {
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
    private fun format(n: Int): String {
        var str = n.toString()
        if (str.length < 2) str = "0$str"
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

    fun printTaskList() {
        val sep = "+----+------------+-------+---+---+--------------------------------------------+\n" // separate line

        if (taskList.isNotEmpty()) {
            println(sep + "| N  |    Date    | Time  | P | D |                   Task                     |\n" + sep)
            for (x in 0 until taskList.size) {
                val (date, time, priority) = taskList[x][0].split(' ').map { it }
                println("| " + (x+1).toString().padEnd(3) + "| " + date + " | " + time + " | " +
                color(priority) + " | " + color(deadline(date)) + " | " ) // without a newline character, because we must continue this
//                println((x+1).toString().padEnd(3) + taskList[x][0] + " " + deadline(taskList[x][0].substringBefore(' ')))
                for (y in 1 until taskList[x][1].length / 44) { // each line for 44 symbols
                    println(taskList[x][2])
                }
//                println()
            }
        } else println("No tasks have been input")
    }
    private fun color(color: String): String {
        return when (color) {
            "C", "O" -> "\u001B[101m \u001B[0m" // if priority = "C" or due tag = "O"
            "H", "T" -> "\u001B[103m \u001B[0m" // if priority = "H" or due tag = "T"
            "N", "I" -> "\u001B[102m \u001B[0m" // if priority = "N" or due tag = "I"
            "L" -> "\u001B[104m \u001B[0m" // if priority = "L"
            else -> "error"
        }
    }

    private fun deadline(s: String): String {
        val date = s.split('-').map { it.toInt() } // get year, month and day
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+0")).date
        val numberOfDays = currentDate.daysUntil(LocalDate(date[0], date[1], date[2])) // get the number of days until our date
        return if (numberOfDays > 0) "I" else if (numberOfDays < 0) "O" else "T"
    }

    fun change(type: String) {
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

    private fun edit(num: Int) {
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

fun main() {
    while (true) {
        println("Input an action (add, print, edit, delete, end):")
        when (readln().lowercase()) {
            "add" -> TaskList.add()
            "print" -> TaskList.printTaskList()
            "delete" -> TaskList.change("delete")
            "edit" -> TaskList.change("edit")
            "end" -> print("Tasklist exiting!").also { return }
            else -> println("The input action is invalid")
        }
    }
}
