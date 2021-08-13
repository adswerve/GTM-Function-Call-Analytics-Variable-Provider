///
/// AnalyticsVariableProvider.swift
///
/// Returns custom values to Google Tag Manager.
///
/// IMPORTANT: The name of this class, prefixed with the module name, needs to be entered
/// into Google Tag Manager in order for GTM's function call variables to work. For example,
/// if the name of this class is "AnalyticsVariableProvider", and it's located in a module called
/// "MyModule", the value "MyModule.AnalyticsVariableProvider" (no quotes) should be entered
/// as the "Class Name" for each function call variable. If you rename this class, or rename the
/// module, you will need to update GTM accordingly. If Adswerve is doing the GTM configuration,
/// you will need to provide this information to your Adswerve contact.
///
/// When processing a tag that depends on a function call variable, GTM will call out to the
/// `execute(withParameters:)` method of this class passing various key-value pairs.
/// The code here will use those key-value pairs to return the desired value. GTM will then
/// process the tag accordingly.
///
/// This sample code can be used to perform either of two actions: 1) Fetch a variable value from the app,
/// or 2) Enforce a desired default value on an existing GTM variable.
///
/// # TO FETCH A VARIABLE VALUE BY NAME:
///
/// 1) Define a new `VariableName` constant for the name of the variable to be returned.
///
/// 2) Implement a new case in the "fetch variable" switch statement below that returns
/// the desired value based on that name.
///
/// 3) In GTM, configure a "Function Call" variable that references this class and passes two
/// key-value pairs:
///   a) "action" is "fetch_variable" (no quotes)
///   b) "variable_name" (no quotes) is the constant defined in Step #1
///
/// 4) If you want to specify a default value for the variable, include an additional key-value pair
/// where "default_value" (no quotes) is the default value you want to be returned when the variable
/// is empty or unavailable. (If there is no "default_value" specified, nil will be returned, which
/// will cause CDs to be excluded from GA hits -- typically the most desirable behavior.)
///
/// # TO ENFORCE A DEFAULT VALUE ON AN EXISTING GTM VARIABLE:
///
/// In GTM, configure a "Function Call" variable that references this class and passes the
/// following key-value pairs:
///
/// 1) "action" is "enforce_default" (no quotes)
///
/// 2) "current_value" (no quotes) is the current value of the GTM variable
///
/// 3) "default_value" (no quotes) is the default value you want returned if the current value is
/// empty, nil, undefined, etc.
///
/// 4) Optional: In lieu of (3), if you want don't want the variable to have any value at all (i.e.,
/// you want it to be nil if there is no value available), include the key "default_none" (no quotes).
///
/// Based on the Google sample code at:
/// https://developers.google.com/tag-manager/ios/v5/advanced-config
///
/// Because the GTM library was created in Objective C, you  will need to use a bridging header
/// in your Swift project that imports <GoogleTagManager/TAGCustomFunction.h>.
///
/// Note: If using a code shrinker or optimizer, make sure that the class name and methods are
/// not dead stripped, renamed, or obfuscated.
///
/// This code is intended only to illustrate how the TAGCustomFunction protocol
/// works. It is not ready for production use as-is.
///
/// @author Chris Hubbard
/// @copyright Copyright (c) 2021 Adswerve. All rights reserved.
///

import Firebase

class AnalyticsVariableProvider: NSObject, TAGCustomFunction {

    // MARK: - Constants (and constant types)

    /// Constants passed by GTM as keys when using this class.
    private struct GtmKey {
        static let action = "action"
        static let fetchVariable = "fetch_variable"
        static let enforceDefault = "enforce_default"
        static let variableName = "variable_name"
        static let currentValue = "current_value"
        static let defaultValue = "default_value"
        static let defaultNone = "default_none"
    }

    /// Constants passed by GTM as values when requesting a variable.
    private struct VariableName {
        static let environment = "environment"
        static let timestamp = "timestamp"
        static let languageCode = "language_code"
        static let timezoneOffset = "timezone_offset"
        static let loggedIn = "logged_in"
        static let anonymizeIp = "anonymize_ip"
    }

    /// Constants that may be returned to GTM.
    private struct ReturnValue {
        static let kTrue = "true"
        static let kFalse = "false"
        static let test = "test"
        static let production = "production"
    }

    // MARK: - Initializer

    required override init() {
        super.init()
    }

    // MARK: - Public methods

     /// Returns the requested value to Google Tag Manager.
     ///
     /// - Parameter parameters: Key-value pairs passed from Google Tag Manager.
     /// - Returns: String representation of variable's value, or nil if not available.
    func execute(withParameters parameters: [AnyHashable : Any]!)  -> NSObject! {

        // determine the action requested: fetch_variable or enforce_default
        let action = parameters[GtmKey.action] as? String
        if (action == nil || action!.count == 0) {
            return makeErrorValue("'\(GtmKey.action)' key not found") as NSObject?
        }

        // determine default value in case there is no value available
        // if GTM doesn't pass a "default_value" key, or passes the key "default_none", default will be nil
        // nil will cause empty CDs to be excluded from GA hits
        var defaultValue : String? = nil
        if (parameters[GtmKey.defaultValue] != nil && parameters[GtmKey.defaultNone] == nil) {
            defaultValue = parameters[GtmKey.defaultValue] as? String
        }

        switch (action) {

        case GtmKey.fetchVariable:
            // USE CASE 1: FETCH A VARIABLE
            // This case is used to provide GTM with app-specific values

            // the value of "variable_name" should contain the name of the value requested
            let variableName = parameters[GtmKey.variableName] as? String
            if (variableName == nil || variableName!.count == 0) {
                return makeErrorValue("'\(GtmKey.variableName)' key not found") as NSObject?
            }

            var value : String? = nil

            switch (variableName) {

            // add any additional variables below

            case VariableName.environment:
                value = environment

            case VariableName.timestamp:
                value = timestamp

            case VariableName.languageCode:
                value = languageCode

            case VariableName.timezoneOffset:
                value = timezoneOffset

            case VariableName.loggedIn:
                value = loggedInStatus  // TODO: Requires app-specific implementation below if used

            case VariableName.anonymizeIp:
                value = anonymizeIp  // TODO: Requires app-specific implementation below if used

            default:
                value = makeErrorValue("Unexpected '\(GtmKey.variableName)' value (\(variableName!))")

            }

            // return value (or default)
            return ((value != nil && value != "") ? value : defaultValue) as NSObject?

        case GtmKey.enforceDefault:
            // USE CASE 2: ENFORCE DEFAULT
            // This case is used to work around a bug in GTM for iOS by enforcing the desired default
            // value when the underlying variable (user property) is not set

            // GTM should pass a "current_value" key, where the value is the current value of the GTM variable
            let currentValue = parameters[GtmKey.currentValue] as? String
            if (currentValue == nil || currentValue == "" || currentValue == "undefined") {
                return defaultValue as NSObject?
            } else {
                return currentValue as NSObject?
            }

        default:
            // unexpected "action" value
            return makeErrorValue("Unexpected '\(GtmKey.action)' value (\(action!))") as NSObject?

        } // switch action

    } // execute

    // MARK: - Private convenience methods & properties

    /// Indicates which GA property hits should be sent to.
    private var environment: String {
        // Current implementation determines environment based on build type, but
        // criteria can be made more sophisticated as needed. End result should be
        // that this returns "test" when hits are to be sent to the test property
        // and "production" when hits are to be sent to the production property
        #if DEBUG
        return ReturnValue.test
        #else
        return ReturnValue.production
        #endif
    }

    /// String representation of current timestamp.
    private var timestamp: String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss.SSS 'GMT'Z '('z')'"  // 2020-02-11 11:26:02.868 GMT-0800 (PST)
        return dateFormatter.string(from: Date())
    }

    /// 2-letter language code for device's preferred language (e.g., "en").
    private var languageCode: String {
        let preferredLanguage = Locale.preferredLanguages.first
        let parts = preferredLanguage?.components(separatedBy: "-")
        let languageCode = parts?.first ??  ""
        return languageCode
    }

    /// Current timezone offset in hours vs GMT (e.g., -8.0, -7.0, 2.0, 1.0).
    private var timezoneOffset: String {
        let offsetInHours = Double(TimeZone.current.secondsFromGMT()) / 3600.0
        return String(format: "%1.1f", offsetInHours)
    }

    /// Indicates whether user is logged in.
    private var loggedInStatus: String? {
        // TODO: THIS IS ONLY A PLACEHOLDER AND REQUIRES APP-SPECIFIC IMPLEMENTATION IF USED
        // return "true" to signal user is logged in, "false" otherwise
        return makeErrorValue("Need to implement loggedInStatus")
    }

    /// Indicates whether IP address should be anonymized by GA.
    private var anonymizeIp: String? {
        // TODO: THIS IS ONLY A PLACEHOLDER AND REQUIRES APP-SPECIFIC IMPLEMENTATION IF USED
        // return "true" to anonomize IP addresses in GA, "false" otherwise
        return makeErrorValue("Need to implement anonymizeIp")
    }

    /// Returns error message for test hits, or nil in production.
    ///
    /// - Parameter description: Description of the error.
    /// - Returns: String description, or nil.
    private func makeErrorValue(_ description: String) -> String? {
        if environment == ReturnValue.test {
            return "Analytics Variable Provider Error: \(description)"
        } else {
            return nil
        }
    }

}
