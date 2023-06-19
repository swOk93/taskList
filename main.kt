package tasklist
import kotlinx.datetime.*

object TaskList {
    private val taskList = mutableListOf<MutableList<String>>()
    fun add() {
        val task = mutableListOf<String>()
        val priority = priority()
        val date = date()
        val time = time()
        task.add("${date[0]}-${format(date[1])}-${format(date[2])} ${format(time[0])}:${format(time[1])} $priority")
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
            } else str = str + "   $it\n" }
        }
    }
    private fun priority(): String {
        var str = ""
        while (str.isEmpty()) {
            println("Input the task priority (C, H, N, L):")
            readln().uppercase().let { if ((it == "C") or (it == "H") or (it == "N") or (it == "L")) str = it }
        }
        return str
    }
    private fun date(): List<Int> {
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
        return list
    }
    private fun time(): List<Int> {
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
        return list
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

    fun printTaskList() {
        if (taskList.isNotEmpty()) {
            for (x in 0 until taskList.size) {
                taskList[x][0] = taskList[x][0] + " " + deadline(taskList[x][0].substringBefore(' '))
                for (y in 0 until taskList[x].size) {
                    println((if (y == 0) (x+1).toString().padEnd(3) else "") + taskList[x][y])
                }
//                println()
            }
        } else println("No tasks have been input")
    }

    private fun deadline(s: String): String {
        val date = s.split('-').map { it.toInt() }
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+0")).date
        val numberOfDays = currentDate.daysUntil(LocalDate(date[0], date[1], date[2]))
        return if (numberOfDays > 0) "I" else if (numberOfDays < 0) "O" else "T"
    }

    fun change(type: String) {
        if (taskList.isNotEmpty()) {
            var loop = true
            while (loop) {
                println("Input the task number (1-${taskList.size}):")
                readln().toInt().let { if (it in 1 until taskList.size) {
                    if (type == "delete") {
                        taskList.removeAt(it).also { println("The task is deleted") }.also { loop = false }
                    } else edit(it).also { loop = false }
                } else println("Invalid task number") }
            }
        }
        printTaskList()
    }

    private fun edit(num: Int) {
        var loop = true
        while (loop) {
            println("Input a field to edit (priority, date, time, task):")
            when (readln().lowercase()) {
                "priority" -> taskList[num][0] = "${taskList[num][0]} ${taskList[num][1]} ${priority()}".also { loop = false }
                "date" -> taskList[num][0] = "${date().let { (a, b, c) -> "$a-$b-$c" }} ${taskList[num][1]} ${taskList[num][2]}".also { loop = false }
                "time" -> taskList[num][0] = "${taskList[num][0]} ${time().let { (a, b) -> "$a:$b" }}  ${taskList[num][2]}".also { loop = false }
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
