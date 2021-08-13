package YOUR_PACKAGE_HERE

import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.annotation.Keep
import YOUR_PACKAGE_HERE.BuildConfig
import com.google.android.gms.tagmanager.CustomVariableProvider
import java.text.SimpleDateFormat
import java.util.*

/**
 * Returns custom values to Google Tag Manager.
 *
 * IMPORTANT: The fully-qualified name of this class needs to be entered into Google Tag Manager
 * in order for GTM's function call variables to work. For example, if your app's package name was
 * "com.mydomain.blah" and this file is placed in your primary package, the fully-qualified name for
 * this class would be "com.mydomain.blah.AnalyticsVariableProvider" and that value would need to be
 * entered as the "Class Path" for each function call variable in GTM. If you rename this class, or
 * put it into a different (sub)package, you will need to update GTM accordingly. If Adswerve is
 * doing the GTM configuration, you will need to provide this information to your Adswerve contact.
 *
 * When processing a tag that depends on "Function Call" variable, GTM will call out to
 * AnalyticsVariableProvider#getValue passing optional key-value pairs. The code here will use those
 * key-value pairs to return the desired value. GTM will then process the tag accordingly.
 *
 * This sample code can be used to perform either of two actions: 1) Fetch a variable value from
 * the app, or 2) Enforce a desired default value on an existing GTM variable.
 *
 * TO FETCH A VARIABLE VALUE BY NAME:
 *
 * 1) Define a new VariableName constant for the name of the variable to be returned.
 *
 * 2) Implement a new case in the "fetch variable" when statement below that returns
 * the desired value based on that name.
 *
 * 3) In GTM, configure a "Function Call" variable that references this class and passes two
 * key-value pairs:
 *    a) "action" is "fetch_variable" (no quotes)
 *    b) "variable_name" (no quotes) is the constant defined in Step #1
 *
 * 4) If you want to specify a default value for the variable, include an additional key-value pair
 * where "default_value" (no quotes) is the default value you want to be returned when the variable
 * is empty or unavailable. (If there is no "default_value" specified, null will be returned, which
 * will cause CDs to be excluded from GA hits -- typically the most desirable behavior.)
 *
 * TO ENFORCE A DEFAULT VALUE ON AN EXISTING GTM VARIABLE:
 *
 * In GTM, configure a "Function Call" variable that references this class and passes the
 * following key-value pairs:
 *
 * 1) "action" is "enforce_default" (no quotes)
 *
 * 2) "current_value" (no quotes) is the current value of the GTM variable
 *
 * 3) "default_value" (no quotes) is the default value you want returned if the current value is
 * empty, null, undefined, etc.
 *
 * 4) Optional: In lieu of (3), if you want don't want the variable to have any value at all (i.e.,
 * you want it to be null if there is no value available), include the key "default_none" (no quotes).
 *
 * Note:
 * If using R8 or ProGuard, make sure that the class name and methods are not dead stripped, renamed,
 * or obfuscated. Use the @Keep annotation to specify this.
 *
 * Based on the Google sample code at:
 * https://developers.google.com/tag-manager/android/v5/advanced-config
 *
 * This code is intended only to illustrate how the CustomVariableProvider interface
 * works. It is not ready for production use as-is.
 *
 * @author Chris Hubbard
 * @copyright Copyright (c) 2021 Adswerve. All rights reserved.
 */
@Keep
class AnalyticsVariableProvider : CustomVariableProvider {

    val TAG = AnalyticsVariableProvider::class.java.simpleName

    /* Constants passed by GTM as keys when using this class */
    private object GtmKey {
        const val ACTION = "action"
        const val ACTION_FETCH_VARIABLE = "fetch_variable"
        const val ACTION_ENFORCE_DEFAULT = "enforce_default"
        const val VARIABLE_NAME = "variable_name"
        const val CURRENT_VALUE = "current_value"
        const val DEFAULT_VALUE = "default_value"
        const val DEFAULT_NONE = "default_none"
    }

    /* Constants passed by GTM as values when requesting a variable */
    private object VariableName {
        const val ENVIRONMENT = "environment"
        const val TIMESTAMP = "timestamp"
        const val LANGUAGE_CODE = "language_code"
        const val TIMEZONE_OFFSET = "timezone_offset"
        const val VERSION_NAME = "version_name"
        const val LOGGED_IN = "logged_in"
        const val ANONYMIZE_IP = "anonymize_ip"
    }

    /* Constants that may be returned to GTM */
    private object ReturnValue {
        const val TRUE = "true"
        const val FALSE = "false"
        const val TEST = "test"
        const val PRODUCTION = "production"
    }

    /**
     * Returns the requested value to Google Tag Manager.
     *
     * @param map - Map representing key-value pairs passed from Google Tag Manager
     * @return String representation of variable's value, or null if not defined
     */
    override fun getValue(map: Map<String, Any>): String? {

        // determine the action requested: fetch_variable or enforce_default
        val action = map[GtmKey.ACTION] as String?
        if (null == action || TextUtils.isEmpty(action)) {
            return makeErrorValue(String.format("'%s' key not found", GtmKey.ACTION))
        }

        // determine default value in case there is no value available
        // if GTM doesn't pass a "default_value" key, or passes the key "default_none", default will be null
        // null will cause empty CDs to be excluded from GA hits
        var defaultValue: String? = null
        if (map.containsKey(GtmKey.DEFAULT_VALUE) && !map.containsKey(GtmKey.DEFAULT_NONE)) {
            defaultValue = map[GtmKey.DEFAULT_VALUE] as String?
        }

        when (action) {

            GtmKey.ACTION_FETCH_VARIABLE -> {
                // USE CASE 1: FETCH A VARIABLE
                // This case is used to provide GTM with requested values

                // the value of "variable_name" should contain the name of the value requested
                val variableName = map[GtmKey.VARIABLE_NAME] as String?
                if (null == variableName || TextUtils.isEmpty(variableName)) {
                    return makeErrorValue(String.format("'%s' key not found", GtmKey.VARIABLE_NAME))
                }

                val value = when (variableName) {

                    // add any additional variables below

                    VariableName.ENVIRONMENT -> environment
                    VariableName.TIMESTAMP -> timestamp
                    VariableName.LANGUAGE_CODE -> languageCode
                    VariableName.TIMEZONE_OFFSET -> timezoneOffset
                    VariableName.VERSION_NAME -> BuildConfig.VERSION_NAME
                    VariableName.LOGGED_IN -> loggedInStatus
                    VariableName.ANONYMIZE_IP -> anonymizeIp
                    else -> {
                        makeErrorValue(String.format("Unexpected '%s' value: %s", GtmKey.VARIABLE_NAME, variableName))
                    }
                }

                // return value (or default)
                return if (TextUtils.isEmpty(value)) defaultValue else value

            } // fetch_variable

            GtmKey.ACTION_ENFORCE_DEFAULT -> {
                // USE CASE 2: ENFORCE DEFAULTS
                // This case is used to work around a bug in GTM on iOS by enforcing a desired default
                // value when the underlying variable (user property) is not set
                // included in Android version of this class for cross-platform consistency

                // GTM should pass a "current_value" key, where the value is the current value of the GTM variable
                val currentValue = map[GtmKey.CURRENT_VALUE] as String?

                if (TextUtils.isEmpty(currentValue) || "undefined" == currentValue || "null" == currentValue) {
                    return defaultValue
                } else {
                    return currentValue
                }

            } // enforce default

            // if "action" is any other value return null
            else -> return makeErrorValue(String.format("Unexpected '%s' value: %s", GtmKey.ACTION, action))

        } // when

    } // getValue


    /**************** Private helper properties ****************/


    /**
     * Indicates which GA property hits should be sent to.
     *
     * @return String "test" to send hits to test property, otherwise "production"
     */
    private val environment: String
        get() {
            // Current implementation determines environment based on build type, but
            // criteria can be made more sophisticated as needed. End result should be
            // that this returns "test" when hits are to be sent to the test property
            // and "production" when hits are to be sent to the production property
            return if (BuildConfig.DEBUG) {
                ReturnValue.TEST
            } else {
                ReturnValue.PRODUCTION
            }
        }

    /**
     * String representation of current timestamp.
     */
    @get:SuppressLint("SimpleDateFormat")
    private val timestamp: String
        get() {
            val format = "yyyy-MM-dd HH:mm:ss.SSS 'GMT'Z '('z')'"  // 2020-02-11 11:26:02.868 GMT-0800 (PST)
            val formatter = SimpleDateFormat(format)
            formatter.timeZone = TimeZone.getDefault()
            return formatter.format(Date(System.currentTimeMillis()))
        }

    /**
     * 2-letter language code for device's preferred language (e.g., "en").
     */
    private val languageCode: String
        get() = Locale.getDefault().language // 2-letter language code (en)

    /**
     * Device's current timezone offset in hours vs GMT (e.g., -8.0, -7.0, 2.0, 1.0)
     */
    @get:SuppressLint("DefaultLocale")
    private val timezoneOffset: String
        get() {
            val offsetInHours = TimeZone.getDefault().getOffset(System.currentTimeMillis()) / (1000.0 * 60 * 60)
            return String.format("%1.1f", offsetInHours)
        }

    /**
     * Indicates whether user is logged in.
     *
     * Should return the String "true" if user is currently logged in.
     */
    private val loggedInStatus: String?
        // TODO: THIS IS ONLY A PLACEHOLDER AND REQUIRES APP-SPECIFIC IMPLEMENTATION IF USED
        get() = makeErrorValue("Need to implement loggedInStatus")

    /**
     * Indicates whether IP address should be anonymized by GA.
     *
     * This function is optional and only really needs to be considered
     * if there is a need to anonymize the user's IP address for privacy
     * reasons. Should return the String "true" if GA should anonymize
     * IP address, otherwise "false".
     */
    private val anonymizeIp: String?
        // TODO: THIS IS ONLY A PLACEHOLDER AND REQUIRES APP-SPECIFIC IMPLEMENTATION IF USED
        get() = makeErrorValue("Need to implement anonymizeIp")


    /**************** Other Helpers ****************/


    /**
     * Returns error message for test hits, or null in production.
     *
     * @param description - Description of the error.
     * @return String description, or null.
     */
    private fun makeErrorValue(description: String): String? {
        return if (ReturnValue.TEST == environment) {
            "Analytics Variable Provider Error: $description"
        } else {
            null
        }
    }
}
