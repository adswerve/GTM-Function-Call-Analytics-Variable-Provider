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
 * This class can be used to perform either of two actions: 1) Fetch a variable value from the app,
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

#ifndef AVPAnalyticsVariableProvider_h
#define AVPAnalyticsVariableProvider_h

#import <GoogleTagManager/TAGCustomFunction.h>
//#import <Foundation/Foundation.h>
@import Foundation;

@class AVPAnalyticsVariableProvider;

@interface AVPAnalyticsVariableProvider : NSObject <TAGCustomFunction>

/**
 * Returns the requested value to Google Tag Manager.
 *
 * @param parameters Key-value pairs passed from Google Tag Manager.
 * @return String representation of variable's value, or nil if not available.
 */
- (NSObject*)executeWithParameters:(NSDictionary*)parameters;

@end

#endif // AVPAnalyticsVariableProvider_h
