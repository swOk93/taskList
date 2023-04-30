package tasklist

object TaskList {
    val taskList = mutableListOf<MutableList<String>>()
    fun add() {
        val task = mutableListOf<String>()
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
