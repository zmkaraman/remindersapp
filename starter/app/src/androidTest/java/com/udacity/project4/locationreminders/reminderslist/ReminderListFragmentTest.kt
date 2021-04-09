package com.udacity.project4.locationreminders.reminderslist

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.IsNot.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {

    private lateinit var repository: ReminderDataSource

    @Before
    fun initRepository() {

        stopKoin()

        /**
         * use Koin Library as a service locator
         */
        val myModule = module {
            //Declare a ViewModel - be later inject into Fragment with dedicated injector using by viewModel()
            viewModel {
                RemindersListViewModel(
                    get(),
                    get() as ReminderDataSource
                )
            }
            //Declare singleton definitions to be later injected using by inject()
            single {
                //This view model is declared singleton to be used across multiple fragments
                SaveReminderViewModel(
                    get(),
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(getApplicationContext()) }
        }

        startKoin {
            androidContext(getApplicationContext())
            modules(listOf(myModule))
        }

        repository = GlobalContext.get().koin.get()

        runBlocking {
            repository.deleteAllReminders()
        }
    }


    @Test
    fun clickTask_navigateToDetailFragmentOne() = runBlockingTest {

        // GIVEN - On the home screen
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        val navController = mock(NavController::class.java)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // WHEN - Click on the first list item
        onView(withId(R.id.addReminderFAB))
            .perform(click())

        // THEN - Verify that we navigate to the first detail screen
        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )

    }

    @Test
    fun activeReminders_DisplayedInUi() = runBlockingTest {

        // GIVEN - Add active (incomplete) task to the DB
        val activeReminder =
            ReminderDTO("Reminder1", "Description1", "Location1", 37.422160, -122.084270)

        runBlocking {
            repository.saveReminder(activeReminder)
        }

        // WHEN - Details fragment launched to display task
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        // make sure that the title/description are both shown and correct
        onView(withId(R.id.title)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.title)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    "Reminder1"
                )
            )
        )
        onView(withId(R.id.description)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.description)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    "Description1"
                )
            )
        )
        onView(withId(R.id.location)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.location)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    "Location1"
                )
            )
        )
        onView(withId(R.id.noDataTextView)).check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))

    }

    @Test
    fun activeNoReminders_ErrMsgDisplayedInUi() = runBlockingTest {

        // WHEN - Details fragment launched to display task
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        // make sure that the title/description are both shown and correct
        onView(withId(R.id.noDataTextView)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.noDataTextView)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    "No Data"
                )
            )
        )
    }
}