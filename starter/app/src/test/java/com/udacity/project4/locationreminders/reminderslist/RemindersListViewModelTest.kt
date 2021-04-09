package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
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

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

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


    @ExperimentalCoroutinesApi
    @Test
    fun loadRemindersDataExist_showLoadingTrue() = mainCoroutineRule.runBlockingTest {

        mainCoroutineRule.pauseDispatcher()
        datasource = FakeDataSource()
        datasource.addReminders(ReminderDTO("Reminder1", "Description1","12321312",null,null))

        reminderListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), datasource)

        // Given a fresh TasksViewModel
        reminderListViewModel.loadReminders()

        // Then the new task event is triggered
        val valueTrue = reminderListViewModel.showLoading.getOrAwaitValue()
        Assert.assertThat(valueTrue, Is.`is`(true))

        mainCoroutineRule.resumeDispatcher()

        // Then the new task event is triggered
        val valueFalse = reminderListViewModel.showLoading.getOrAwaitValue()
        Assert.assertThat(valueFalse, Is.`is`(false))


        // Then the new task event is triggered
        val value = reminderListViewModel.showNoData.getOrAwaitValue()
        // Then the new task event is triggered
        Assert.assertThat(value, Is.`is`(true))
    }

    @Test
    fun loadRemindersError_showTestException() {

        datasource = FakeDataSource()
        datasource.setReturnError(true)
        reminderListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), datasource)

        // Given a fresh TasksViewModel
        reminderListViewModel.loadReminders()

        // Then the new task event is triggered
        val value = reminderListViewModel.showSnackBar.getOrAwaitValue()
        // Then the new task event is triggered
        Assert.assertThat(value, Is.`is`("Test exception"))
    }

}