package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matchers
import org.hamcrest.core.Is.`is`
import org.junit.*
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.O_MR1])
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    // Subject under test
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    private lateinit var datasource: FakeDataSource


    @Before
    fun setupViewModel() {

        var reminders: MutableList<ReminderDTO>? = mutableListOf()
        datasource = FakeDataSource()
        reminders?.add(ReminderDTO("Reminder1", "Description1",null,null,null))
        reminders?.add(ReminderDTO("Reminder2", "Description3",null,null,null))

        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), datasource)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun addReminderNoLocation_setsErrorSelectLocation() {

        var reminderDataItem = ReminderDataItem("Title 5","Desc 5","", null, null, "")
        // Given a fresh TasksViewModel
        saveReminderViewModel.validateEnteredData(reminderDataItem)

        // Then the new task event is triggered
        val value = saveReminderViewModel.showSnackBarInt.getOrAwaitValue()
        Assert.assertThat(value, `is`(R.string.err_select_location))

    }


    @Test
    fun addReminderNoTitle_setsErrorEnterTitle() {

        var reminderDataItem = ReminderDataItem("","","", null, null, "")
        // Given a fresh TasksViewModel
        saveReminderViewModel.validateEnteredData(reminderDataItem)

        // Then the new task event is triggered
        val value = saveReminderViewModel.showSnackBarInt.getOrAwaitValue()
        Assert.assertThat(value, `is`(R.string.err_enter_title))

    }

    @Test
    fun saveReminderCorrect_setsReturnTrue() {

        var reminderDataItem = ReminderDataItem("Title 5","Desc 5","adasdasd", null, null, "")
        // Given a fresh TasksViewModel
        saveReminderViewModel.saveReminder(reminderDataItem)

        // Then the new task event is triggered
        val value = saveReminderViewModel.showToast.getOrAwaitValue()
        Assert.assertThat(value, `is`("Reminder Saved !"))
    }


    @Test
    fun addReminderCorrect_setsReturnsTrue() {

        var reminderDataItem = ReminderDataItem("Title 5","Desc 5","adasdasd", null, null, "")
        // Given a fresh TasksViewModel
        saveReminderViewModel.validateAndSaveReminder(reminderDataItem)

        // Then the new task event is triggered
        val value = saveReminderViewModel.showToast.getOrAwaitValue()
        Assert.assertThat(value, `is`("Reminder Saved !"))
    }


    @Test
    fun addReminderNoTitle_setsReturnsErrorMsg() {

        var reminderDataItem = ReminderDataItem("","Desc 5","adasdasd", null, null, "")
        // Given a fresh TasksViewModel
        saveReminderViewModel.validateAndSaveReminder(reminderDataItem)

        // Then the new task event is triggered
        val value = saveReminderViewModel.showSnackBarInt.getOrAwaitValue()
        Assert.assertThat(value, `is`(R.string.err_enter_title))
    }


}