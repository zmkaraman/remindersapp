package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    private lateinit var database: RemindersDatabase

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertReminderAndGetById() = runBlockingTest {
        // GIVEN - Insert a reminder.
        val reminder = ReminderDTO("title", "description","lcoation",null,null)
        database.reminderDao().saveReminder(reminder)

        // WHEN - Get the task by id from the database.
        val loaded = database.reminderDao().getReminderById(reminder.id)

        // THEN - The loaded data contains the expected values.
        assertThat<ReminderDTO>(loaded as ReminderDTO, notNullValue())
        assertThat(loaded.id, `is`(reminder.id))
        assertThat(loaded.title, `is`(reminder.title))
        assertThat(loaded.description, `is`(reminder.description))
    }

    @Test
    fun deleteRemindersAndGetByIdisNull() = runBlockingTest {
        // GIVEN - Insert a reminder.
        val reminder = ReminderDTO("title", "description","location",null,null)
        database.reminderDao().saveReminder(reminder)

        // WHEN - Get the task by id from the database.
        val loaded = database.reminderDao().getReminderById(reminder.id)

        // THEN - The loaded data contains the expected values.
        assertThat<ReminderDTO>(loaded as ReminderDTO, notNullValue())
        assertThat(loaded.id, `is`(reminder.id))
        assertThat(loaded.title, `is`(reminder.title))
        assertThat(loaded.description, `is`(reminder.description))

        database.reminderDao().deleteAllReminders()

        // WHEN - Get the task by id from the database.
        val reloaded = database.reminderDao().getReminderById(reminder.id)

        // THEN - The loaded data contains the expected values.
        assertThat(reloaded, nullValue())
    }

    @Test
    fun saveRemindersAndGetByIdisNull() = runBlockingTest {
        // GIVEN - Insert a reminder.
        val reminder = ReminderDTO("title", "description","location",null,null)
        database.reminderDao().saveReminder(reminder)
        val reminder2 = ReminderDTO("title2", "description2","location2",null,null)
        database.reminderDao().saveReminder(reminder2)

        // WHEN - Get the task by id from the database.
        val reminders = database.reminderDao().getReminders()

        val loaded = reminders[0]
        // THEN - The loaded data contains the expected values.
        assertThat<ReminderDTO>(loaded, notNullValue())
        assertThat(loaded.id, `is`(reminder.id))
        assertThat(loaded.title, `is`(reminder.title))
        assertThat(loaded.description, `is`(reminder.description))


        val loaded2 = reminders[1]
        // THEN - The loaded data contains the expected values.
        assertThat<ReminderDTO>(loaded2, notNullValue())
        assertThat(loaded2.id, `is`(reminder2.id))
        assertThat(loaded2.title, `is`(reminder2.title))
        assertThat(loaded2.description, `is`(reminder2.description))

    }


}