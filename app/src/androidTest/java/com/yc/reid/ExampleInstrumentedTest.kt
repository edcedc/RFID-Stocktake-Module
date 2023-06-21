package com.yc.reid

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        val a = StringUtils.equalsIgnoreCase("ABCD", "abcD")
        val b = StringUtils.equalsIgnoreCase("ABCD", "123")
        val c = StringUtils.equalsIgnoreCase("123", "123")
        val d = StringUtils.equalsIgnoreCase("123abcd", "123ABcd")
        val f = StringUtils.equalsIgnoreCase("123ab333cd", "123AB333cd")
        val e = StringUtils.equalsIgnoreCase("123ab333cd", "123AB33cd")

        LogUtils.e(a, b, c, d, f, e)

    }
}
