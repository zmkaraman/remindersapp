package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.runBlocking
import java.util.LinkedHashMap

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders: MutableList<ReminderDTO>? = mutableListOf() )  : ReminderDataSource {

    private var shouldReturnError = false

    var reminderServiceData: LinkedHashMap<String, ReminderDTO> = LinkedHashMap()

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) {
            return Result.Error("Test exception", -1)
        }
        reminders?.let { return Result.Success(ArrayList(it)) }
        return Result.Error("Tasks not found", 101)
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        reminders?.let { return Result.Success(it[id.toInt()]) }
        return Result.Error("Task not found", 105)
    }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }


    fun addReminders(vararg reminders: ReminderDTO) {
        for (reminder in reminders) {
            reminderServiceData[reminder.id] = reminder
        }
        runBlocking { getReminders() }
    }

}