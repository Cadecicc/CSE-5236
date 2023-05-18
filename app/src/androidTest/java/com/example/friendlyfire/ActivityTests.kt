package com.example.friendlyfire

import android.support.test.espresso.ViewInteraction;
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.friendlyfire.UI.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

//@RunWith(AndroidJUnit4::class)
//@LargeTest
//class MainMenuActivityTest {
//
//    private lateinit var mainMenu : ActivityScenario<MainMenuActivity>
//
//    @Before
//    fun setup(){
//        mainMenu = launchActivity()
//        mainMenu.moveToState(Lifecycle.State.STARTED)
//    }
//
//    @Test
//    fun testMakeGroup(){
//        onView(withId(R.id.makeGroupButton).perform(click()))
//    }
//
//    @get:Rule
//    val activityRule = ActivityScenarioRule(MainMenuActivity::class.java)
//
//    @Test
//    fun generatedCodeRegex() {
//        onView(withText("Hello world!")).check(matches(isDisplayed()))
//    }
//}
//
//@RunWith(AndroidJUnit4::class)
//@LargeTest
//class GroupLobbyActivityTest {
//
//    @get:Rule
//    val activityRule = ActivityScenarioRule(GroupLobbyActivity::class.java)
//
//    @Test
//    fun generatedCodeRegex() {
//        onView(withText("Hello world!")).check(matches(isDisplayed()))
//    }
//}

@RunWith(AndroidJUnit4::class)
@LargeTest
class LogoScreenActivityTest {

    private lateinit var logo : ActivityScenario<LogoScreenActivity>
    private val homeScreenText = "Where your friends become your enemies for the night..."

    @Before
    fun setup(){
        logo = launchActivity()
        logo.moveToState(Lifecycle.State.STARTED)
    }

    @Test
    fun testHomeScreenNav(){
        onView(withId(R.id.beginButton)).perform(click())
        onView(withText(homeScreenText)).check(matches(isDisplayed()))
    }

}

@RunWith(AndroidJUnit4::class)
@LargeTest
class HomeScreenActivityTest {

    private lateinit var homeScreen : ActivityScenario<HomeScreenActivity>

    @Before
    fun setup(){
        homeScreen = launchActivity()
        homeScreen.moveToState(Lifecycle.State.STARTED)
    }

    @Test
    fun testSignInNav(){
        onView(withId(R.id.SignIn)).perform(click())
        onView(withText("Sign In")).check(matches(isDisplayed()))
    }

    @Test
    fun testRegisterNav(){
        onView(withId(R.id.Register)).perform(click())
        onView(withText("Register")).check(matches(isDisplayed()))
    }
}