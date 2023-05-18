package com.example.friendlyfire

import com.example.friendlyfire.UI.RegisterActivity
import org.junit.Assert.*
import org.junit.Test

class FundDistributionTest {


    @Test
    fun example() {
        val isTrue: Boolean = true
        assertTrue(isTrue)
    }


    @Test
    fun checkMoneyCalculation() {

        //This is the theoretical calculation for our money distribution. We use
        //filler values since our real method uses Firebase and we want to avoid expensive
        //calls with 3rd party services
        var losingSideBets: Int = 30
        var numberOfWinners: Int = 5
        var yourOwnBet: Int = 5


        val splitWinnings: Int = losingSideBets / numberOfWinners + yourOwnBet

        assertEquals(splitWinnings, 11)

    }
}

    class PasswordTest{


        @Test
        fun isValidPasswordTest(){

            val goodPassword: String? = "Password12!"
            val passwordGoodTest: Boolean = RegisterActivity.isValidPassword(goodPassword)

            val badPassword: String? = "hello"
            val passwordBadTest: Boolean = RegisterActivity.isValidPassword(badPassword)

            assertEquals(passwordGoodTest, true)
            assertEquals(passwordBadTest, false)

        }
    }

class EmailTest {

    @Test
    fun isValidEmail(){

        val goodEmail: String? = "djohnson29@gmail.com"
        val emailGoodTest: Boolean = RegisterActivity.isEmailValid(goodEmail)
        println(emailGoodTest)

       val badEmail: String? = "hello"
        val emailBadTest: Boolean = RegisterActivity.isEmailValid(badEmail)

        assertEquals(emailGoodTest, true)
        assertEquals(emailBadTest, false)
    }

}



