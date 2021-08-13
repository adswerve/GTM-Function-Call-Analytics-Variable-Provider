/**
 * Returns custom values to Google Tag Manager.
 *
 * IMPORTANT: The name of this class needs to be entered into Google Tag Manager in order for
 * GTM's function call variables to work. For example, if the name of this class is
 * "AVPAnalyticsVariableProvider" the value "AVPAnalyticsVariableProvider" (no quotes) should be
 * entered as the "Class Name" for each function call variable. If you rename this class, you will
 * need to update GTM accordingly. If Adswerve is doing the GTM configuration, you will need to
 * provide this information to your Adswerve contact.
 *
 * When processing a tag that depends on "Function Call" variable, GTM will call out to the
 * executeWithParameters: method of this class passing optional key-value pairs. The code here will use
 * those key-value pairs to return the desired value. GTM will then process the tag accordingly.
 *
 * This sample code can be used to perform either of two actions: 1) Fetch a variable value from the app,
 * or 2) Enforce a desired default value on an existing GTM variable.
 *
 * TO FETCH A VARIABLE VALUE BY NAME:
 *
 * 1) Define a new constant for the name of the variable to be returned.
 *
 * 2) Implement a new IF clause in executeWithParameters: that returns the desired value
 * based on that name.
 *
 * 3) In GTM, configure a "Function Call" variable that references this class and passes two
 * key-value pairs:
 *   a) "action" is "fetch_variable" (no quotes)
 *   b) "variable_name" (no quotes) is the constant defined in Step #1
 *
 * 4) If you want to specify a default value for the variable, include an additional key-value pair
 * where "default_value" (no quotes) is the default value you want to be returned when the variable
 * is empty or unavailable. (If there is no "default_value" specified, nil will be returned, which
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
 * empty, nil, undefined, etc.
 *
 * 4) Optional: In lieu of (3), if you want don't want the variable to have any value at all (i.e.,
 * you want it to be nil if there is no value available), include the key "default_none" (no quotes).
 *
 * Note: If using a code shrinker or optimizer, make sure that the class name and methods are
 * not dead stripped, renamed, or obfuscated.
 *
 * Based on the Google sample code at:
 * https://developers.google.com/tag-manager/ios/v5/advanced-config
 *
 * This code is intended only to illustrate how the TAGCustomFunction protocol
 * works. It is not ready for production use as-is.
 *
 * @author Chris Hubbard
 * @copyright Copyright (c) 2021 Adswerve. All rights reserved.
 */

#import "AVPAnalyticsVariableProvider.h"

@implementation AVPAnalyticsVariableProvider

/** Constants passed by GTM as keys when using this class */
static NSString *const kAction = @"action";
static NSString *const kActionFetchVariable = @"fetch_variable";
static NSString *const kActionEnforceDefaults = @"enforce_default";
static NSString *const kVariableName = @"variable_name";
static NSString *const kCurrentValue = @"current_value";
static NSString *const kDefaultValue = @"default_value";
static NSString *const kDefaultNone = @"default_none";

/** Constants passed by GTM as values when requesting a variable. */
static NSString *const kEnvironment = @"environment";
static NSString *const kTimestamp = @"timestamp";
static NSString *const kLanguageCode = @"language_code";
static NSString *const kTimezoneOffset = @"timezone_offset";
static NSString *const kLoggedIn = @"logged_in";
static NSString *const kAnonymizeIp = @"anonymize_ip";

/** Constants that may be returned to GTM. */
static NSString *const kTrue = @"true";
static NSString *const kFalse = @"false";
static NSString *const kTest = @"test";
static NSString *const kProduction = @"production";

// MARK: - Public methods

/**
 * Returns the requested value to Google Tag Manager.
 *
 * @param parameters Key-value pairs passed from Google Tag Manager.
 * @return String representation of variable's value, or nil if not available.
 */
- (NSObject *)executeWithParameters:(NSDictionary*)parameters {
    
    // determine the action requested: fetch_variable or enforce_default
    NSString *action = [parameters objectForKey:(kAction)];
    if (0 == [action length]) {
        return [self makeErrorValue:[NSString stringWithFormat:@"'%@' key not found", kAction]];
    }
    
    // determine default value in case there is no value available
    // if GTM doesn't pass a "default_value" key, or passes the key "default_none", default will be nil
    // nil will cause empty CDs to be excluded from GA hits
    NSString *defaultValue = nil;
    if (nil != [parameters objectForKey:(kDefaultValue)] && nil == [parameters objectForKey:(kDefaultNone)]) {
        defaultValue = [parameters objectForKey:(kDefaultValue)];
    }
    
    if ([kActionFetchVariable isEqualToString:action]) {
        // USE CASE 1: FETCH A VARIABLE
        // This case is used to provide GTM with app-specific values
        
        // the value of "variable_name" should contain the name of the value requested
        NSString *variableName = [parameters objectForKey:(kVariableName)];
        if (0 == [variableName length]) {
            return [self makeErrorValue:[NSString stringWithFormat:@"'%@' key not found", kVariableName]];
        }
        
        NSString *value = nil;
        
        // add any additional variables below
        
        if ([variableName isEqualToString:kEnvironment]) {
            value = [self getEnvironment];
        } else if ([variableName isEqualToString:kTimestamp]) {
            value = [self getTimestamp];
        } else if ([variableName isEqualToString:kLanguageCode]) {
            value = [self getLanguageCode];
        } else if ([variableName isEqualToString:kTimezoneOffset]) {
            value = [self getTimezoneOffset];
        } else if ([variableName isEqualToString:kLoggedIn]) {
            // TODO: Requires app-specific implementation below if used
            value = [self getLoggedInStatus];
        } else if ([variableName isEqualToString:kAnonymizeIp]) {
            // TODO: Requires app-specific implementation below if used
            value = [self getAnonymizeIp];
        } else {
            value = [self makeErrorValue:[NSString stringWithFormat:@"Unexpected '%@' value (%@)", kVariableName, variableName]];
        }
        
        // return value (or default)
        return [value length] > 0 ? value : defaultValue;
        
    } else if ([kActionEnforceDefaults isEqualToString:action]) {
        // USE CASE 2: ENFORCE DEFAULTS
        // This case is used to work around a bug in GTM for iOS by enforcing the desired default
        // value when the underlying variable (user property) is not set
        
        // GTM should pass a "current_value" key, where the value is the current value of the GTM variable
        NSString *currentValue = [parameters objectForKey:(kCurrentValue)];
        if (0 == [currentValue length] || [currentValue isEqualToString:@"undefined"]) {
            return defaultValue;
        } else {
            return currentValue;
        }
        
    } else {
        // unexpected "action" value
        return [self makeErrorValue:[NSString stringWithFormat:@"Unexpected '%@' value (%@)", kAction, action]];
    }
    
} //executeWithParameters

// MARK: - Private helper methods

/**
 * Indicates which GA property hits should be sent to.
 *
 * @return String "test" to send hits to test property, otherwise "production"
 */
- (NSString *)getEnvironment {
    // Current implementation determines environment based on build type, but
    // criteria can be made more sophisticated as needed. End result should be
    // that this returns "test" when hits are to be sent to the test property
    // and "production" when hits are to be sent to the production property
    #if DEBUG
    return kTest;
    #else
    return kProduction;
    #endif
}

/**
 * Returns the current timestamp.
 *
 * @return String representation of current timestamp
 */
- (NSString *)getTimestamp {
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss.SSS 'GMT'Z '('z')'"];  // 2020-02-11 11:26:02.868 GMT-0800 (PST)
    NSString *formattedTimestamp = [dateFormatter stringFromDate:[NSDate date]];
    return formattedTimestamp;
}

/**
 * Returns the device's current timezone offset in hours vs GMT (e.g., -8.0, -7.0, 2.0, 1.0).
 *
 * @return String representation of current timezone offset.
 */
- (NSString *)getTimezoneOffset {
    double offsetInHours = [[NSTimeZone localTimeZone] secondsFromGMT] / 3600.0;
    return [NSString stringWithFormat: @"%1.1f", offsetInHours];
}

/**
 * Returns 2-letter language code for device's preferred language (e.g., "en").
 *
 * @return Language code.
 */
- (NSString *)getLanguageCode {
    NSString *preferredLanguage = [[NSLocale preferredLanguages] firstObject];  // en-US, zh-Hans-HK
    NSArray *parts = [preferredLanguage componentsSeparatedByString:@"-"];     // { en, US }, { zh, Hans, HK }
    NSString *languageCode = [parts firstObject];  // en, zh
    return languageCode;
}

/**
 * Indicates whether user is logged in.
 *
 * @return String "true" if user is currently logged in.
 */
- (NSString *)getLoggedInStatus {
    // TODO: THIS IS ONLY A PLACEHOLDER AND REQUIRES APP-SPECIFIC IMPLEMENTATION IF USED
    return [self makeErrorValue:@"Need to implement getLoggedInStatus"];
}

/**
 * Indicates whether IP address should be anonymized by GA.
 *
 * This function is optional and only really needs to be considered if there is a need to
 * anonymize the user's IP address for privacy reasons.
 *
 * @return String "true" if GA should anonymize IP address, otherwise "false".
 */
- (NSString *)getAnonymizeIp {
    // TODO: THIS IS ONLY A PLACEHOLDER AND REQUIRES APP-SPECIFIC IMPLEMENTATION IF USED
    return [self makeErrorValue:@"Need to implement getAnonymizeIp"];
}

/**
 * Returns error message for test hits, or null in production.
 *
 * @param description Description of the error.
 * @return String description, or null.
 */
- (NSString *)makeErrorValue:(NSString *)description {
    if ([kTest isEqualToString:[self getEnvironment]]) {
        return [@"Analytics Variable Provider Error: " stringByAppendingString:description];
    } else {
        return nil;
    }
}

@end
