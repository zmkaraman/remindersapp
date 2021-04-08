package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.Is
import org.junit.*
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    // Subject under test
    private lateinit var reminderListViewModel: RemindersListViewModel

    private lateinit var datasource: FakeDataSource


    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun loadRemindersNoData_showNoDataTrue() {

        datasource = FakeDataSource()
        reminderListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), datasource)

        // Given a fresh TasksViewModel
        reminderListViewModel.loadReminders()

        // Then the new task event is triggered
        val value = reminderListViewModel.showNoData.getOrAwaitValue()

        // Then the new task event is triggered
        Assert.assertThat(value, Is.`is`(true))
    }


    @Test
    fun loadRemindersDataExist_showNoDataFalse() {

        // We initialise the tasks to 3, with one active and two completed
        var reminders: MutableList<ReminderDTO>? = mutableListOf()
        reminders?.add(ReminderDTO("Reminder1", "Description1",null,null,null))
        reminders?.add(ReminderDTO("Reminder2", "Description3",null,null,null))
        datasource = FakeDataSource(reminders)

        reminderListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), datasource)

        // Given a fresh TasksViewModel
        reminderListViewModel.loadReminders()

        // Then the new task event is triggered
        val value = reminderListViewModel.showNoData.getOrAwaitValue()

        // Then the new task event is triggered
        Assert.assertThat(value, Is.`is`(false))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun loadRemindersDataExist_showLoadingFalse() = runBlockingTest {

        // We initialise the tasks to 3, with one active and two completed
        var reminders: MutableList<ReminderDTO>? = mutableListOf()
        reminders?.add(ReminderDTO("Reminder1", "Description1",null,null,null))
        reminders?.add(ReminderDTO("Reminder2", "Description3",null,null,null))
        datasource = FakeDataSource()
        //not sosure
        datasource.addReminders(ReminderDTO("Reminder1", "Description1",null,null,null))

        reminderListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), datasource)

        // Given a fresh TasksViewModel
        reminderListViewModel.loadReminders()

        // Then the new task event is triggered
        val value = reminderListViewModel.showLoading.getOrAwaitValue()
        //val valueS = reminderListViewModel.showSnackBar.getOrAwaitValue()


        // Then the new task event is triggered
        Assert.assertThat(value, Is.`is`(false))
        //Assert.assertThat(valueS, Is.`is`(""))
    }


}