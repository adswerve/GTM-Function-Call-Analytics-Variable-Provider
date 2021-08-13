package YOUR_PACKAGE_HERE;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import YOUR_PACKAGE_HERE.BuildConfig;
import com.google.android.gms.tagmanager.CustomVariableProvider;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import androidx.annotation.Keep;

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
 * 1) Define a new constant for the name of the variable to be returned.
 *
 * 2) Implement a new case in the "fetch variable" switch statement below that returns
 * the desired value based on that name.
 *
 * 3) In GTM, configure a "Function Call" variable that references this class and passes two
 * key-value pairs:
 * a) "action" is "fetch_variable" (no quotes)
 * b) "variable_name" (no quotes) is the constant defined in Step #1
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
public class AnalyticsVariableProvider implements CustomVariableProvider {

    public static final String TAG = AnalyticsVariableProvider.class.getSimpleName();

    /* Constants passed by GTM as keys when using this class */
    private static class GtmKey {
        private static final String ACTION = "action";
        private static final String ACTION_FETCH_VARIABLE = "fetch_variable";
        private static final String ACTION_ENFORCE_DEFAULT = "enforce_default";
        private static final String VARIABLE_NAME = "variable_name";
        private static final String CURRENT_VALUE = "current_value";
        private static final String DEFAULT_VALUE = "default_value";
        private static final String DEFAULT_NONE = "default_none";
    }

    /* Constants passed by GTM as values when requesting a variable */
    private static class VariableName {
        private static final String ENVIRONMENT = "environment";
        private static final String TIMESTAMP = "timestamp";
        private static final String LANGUAGE_CODE = "language_code";
        private static final String TIMEZONE_OFFSET = "timezone_offset";
        private static final String VERSION_NAME = "version_name";
        private static final String LOGGED_IN = "logged_in";
        private static final String ANONYMIZE_IP = "anonymize_ip";
    }

    /* Constants that may be returned to GTM */
    private static class ReturnValue {
        private static final String TRUE = "true";
        private static final String FALSE = "false";
        private static final String TEST = "test";
        private static final String PRODUCTION = "production";
    }

    /**
     * Returns the requested value to Google Tag Manager.
     *
     * @param map - Map representing key-value pairs passed from Google Tag Manager
     * @return String representation of variable's value, or null if not defined
     */
    @Override
    public String getValue(Map<String, Object> map) {

        // determine the action requested: fetch_variable or enforce_default
        String action = (String) map.get(GtmKey.ACTION);
        if (null == action || TextUtils.isEmpty(action)) {
            return makeErrorValue(String.format("'%s' key not found", GtmKey.ACTION));
        }

        // determine default value in case there is no value available
        // if GTM doesn't pass a "default_value" key, or passes the key "default_none", default will be null
        // null will cause empty CDs to be excluded from GA hits
        String defaultValue = null;
        if (map.containsKey(GtmKey.DEFAULT_VALUE) && !map.containsKey(GtmKey.DEFAULT_NONE)) {
            defaultValue = (String) map.get(GtmKey.DEFAULT_VALUE);
        }

        switch (action) {

            case GtmKey.ACTION_FETCH_VARIABLE: {
                // USE CASE 1: FETCH A VARIABLE
                // This case is used to provide GTM with requested values

                // the value of "variable_name" should contain the name of the value requested
                String variableName = (String) map.get(GtmKey.VARIABLE_NAME);
                if (null == variableName || TextUtils.isEmpty(variableName)) {
                    return makeErrorValue(String.format("'%s' key not found", GtmKey.VARIABLE_NAME));
                }

                String value;

                switch (variableName) {

                    // add any additional variables below

                    case VariableName.ENVIRONMENT:
                        value = getEnvironment();
                        break;

                    case VariableName.TIMESTAMP:
                        value = getTimestamp();
                        break;

                    case VariableName.LANGUAGE_CODE:
                        value = getLanguageCode();
                        break;

                    case VariableName.TIMEZONE_OFFSET:
                        value = getTimezoneOffset();
                        break;

                    case VariableName.VERSION_NAME:
                        value = BuildConfig.VERSION_NAME;
                        break;

                    case VariableName.LOGGED_IN:
                        // TODO: Requires app-specific implementation below if used
                        value = getLoggedInStatus();
                        break;

                    case VariableName.ANONYMIZE_IP:
                        // TODO: Requires app-specific implementation below if used
                        value = getAnonymizeIp();
                        break;

                    default:
                        value = makeErrorValue(String.format("Unexpected '%s' value: %s", GtmKey.VARIABLE_NAME, variableName));

                }

                // return value (or default)
                return TextUtils.isEmpty(value) ? defaultValue : value;

            } // fetch_variable

            case GtmKey.ACTION_ENFORCE_DEFAULT: {
                // USE CASE 2: ENFORCE DEFAULTS
                // This case is used to work around a bug in GTM on iOS by enforcing a desired default
                // value when the underlying variable (user property) is not set
                // included in Android version of this class for cross-platform consistency

                // GTM should pass a "current_value" key, where the value is the current value of the GTM variable
                String currentValue = (String) map.get(GtmKey.CURRENT_VALUE);
                if (TextUtils.isEmpty(currentValue) || "undefined".equals(currentValue) || "null".equals(currentValue)) {
                    return defaultValue;
                } else {
                    return currentValue;
                }
            } // enforce default

            default:
                // unexpected "action" value
                return makeErrorValue(String.format("Unexpected '%s' value: %s", GtmKey.ACTION, action));

        } // action switch

    } // getValue


    /**************** Private helper methods ****************/


    /**
     * Indicates which GA property hits should be sent to.
     *
     * @return String "test" to send hits to test property, otherwise "production".
     */
    private String getEnvironment() {
        // Current implementation determines environment based on build type, but
        // criteria can be made more sophisticated as needed. End result should be
        // that this returns "test" when hits are to be sent to the test property
        // and "production" when hits are to be sent to the production property
        if (BuildConfig.DEBUG) {
            return ReturnValue.TEST;
        } else {
            return ReturnValue.PRODUCTION;
        }
    }

    /**
     * Returns the current timestamp.
     *
     * @return String representation of current timestamp.
     */
    @SuppressLint("SimpleDateFormat")
    private String getTimestamp() {
        String format = "yyyy-MM-dd HH:mm:ss.SSS 'GMT'Z '('z')'";  // 2020-02-11 11:26:02.868 GMT-0800 (PST)
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(new Date(System.currentTimeMillis()));
    }

    /**
     * Returns 2-letter language code for device's preferred language (e.g., "en").
     *
     * @return Language code.
     */
    private String getLanguageCode() {
        return Locale.getDefault().getLanguage(); // 2-letter language code (en)
    }

    /**
     * Returns the device's current timezone offset in hours vs GMT (e.g., -8.0, -7.0, 2.0, 1.0).
     *
     * @return String representation of current timezone offset.
     */
    @SuppressLint("DefaultLocale")
    private String getTimezoneOffset() {
        double offsetInHours = TimeZone.getDefault().getOffset(System.currentTimeMillis()) / (1000.0 * 60 * 60);
        return String.format("%1.1f", offsetInHours);
    }

    /**
     * Indicates whether user is logged in.
     *
     * @return String "true" if user is currently logged in.
     */
    private String getLoggedInStatus() {
        // TODO: THIS IS ONLY A PLACEHOLDER AND REQUIRES APP-SPECIFIC IMPLEMENTATION IF USED
        return makeErrorValue("Need to implement getLoggedInStatus()");
    }

    /**
     * Indicates whether IP address should be anonymized by GA.
     *
     * This function is optional and only really needs to be considered if there is a need to
     * anonymize the user's IP address for privacy reasons.
     *
     * @return String "true" if GA should anonymize IP address, otherwise "false".
     */
    private String getAnonymizeIp() {
        // TODO: THIS IS ONLY A PLACEHOLDER AND REQUIRES APP-SPECIFIC IMPLEMENTATION IF USED
        return makeErrorValue("Need to implement getAnonymizeIp()");
    }

    /**
     * Returns error message for test hits, or null in production.
     *
     * @param description - Description of the error.
     * @return String description, or null.
     */
    private String makeErrorValue(String description) {
        if (ReturnValue.TEST.equals(getEnvironment())) {
            return "Analytics Variable Provider Error: " + description;
        } else {
            return null;
        }
    }

}