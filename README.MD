# Google Tag Manager Function Call Analytics Variable Provider
by [Adswerve](https://www.adswerve.com)

Sample implementation of an "AnalyticsVariableProvider" class that returns requested values to Google Tag Manager (v5) via Function Call variables. For use in native Android and iOS apps that utilize the Firebase Analytics SDK and the Google Tag Manager (v5) library to send analytics data to Google Analytics "Universal Analytics" properties.

## Implementation

#### Android
 Copy the **AnalyticsVariableProvider.java** file or **AnalyticsVariableProvider.kt** file from the repo folder to a convenient location in your Android Studio project. _The class name you will need for GTM configuration is the fully-qualified name of this class (e.g., com.mydomain.myapp.AnalyticsVariableProvider)._

#### iOS
For Swift apps, copy the **AnalyticsVariableProvider.swift** and **Analytics-Bridging-Header.h** files to a convenient location in your Xcode project. In the Build Settings for your app's target(s), add an entry for the bridging header in the **Swift Compiler - General** section (see [Apple's documentation](https://developer.apple.com/documentation/swift/imported_c_and_objective-c_apis/importing_objective-c_into_swift)). _The class name you will need for GTM configuration is the class name prefixed by the name of the Swift module in which the class resides (e.g., MyModule.AnalyticsVariableProvider)._

For Objective-C apps, copy the **AVPAnalyticsVariableProvider.h** and **AVPAnalyticsVariableProvider.m** files to a convenient location in your Xcode project. _The class name you will need for GTM configuration is simply the name of the class (e.g., AVPAnalyticsVariableProvider)._

#### Google Tag Manager
In the GTM (v5) web UI, create a constant variable called **AnalyticsVariableProviderClass** and enter the class name indicated above based on the language you are using.

Create a new "Function Call" variable in GTM, using **AnalyticsVariableProviderClass** as the value for "Class Path" (Android) or "Class Name" (iOS). Include any key-value pairs needed to cause the AnalyticsVariableProviderCode to return the desired value. (See examples below.)

Sample Android and iOS containers with some preconfigured function call variables are included in the "Sample Containers" folder. Import the JSON file into an empty test container to experiment.

## Usage
The sample code included in this repo returns a handful of illustrative values, including environment, timestamp, timezone offset, logged-in status, etc. for Java, Kotlin, Swift, and Objective-C. This code is only an example, but it's functional and will give you a good foundation for adding additional custom values based on the needs in your apps.

Each function call variable can pass a custom set of parameters (key-value pairs) from GTM to the AnalyticsVariableProvider code, and the code can then use those parameters to determine what value to return. In the sample code provided, we’ve created logic that depends on the parameter keys “action”, “variable_name”, and “default_value”. (And since it’s usually best practice to not send empty custom dimensions to Universal Analytics, we’re also defined a “default_none” key that tells the code to return null/nil if no value is available.)

**Keep in mind that all of these keys (and values) are completely customizable.** In the sample code we’ve defined two “actions” for use cases we commonly need to address, but your “actions” could be something else entirely. Or you might not even need to use an “action” parameter: In the simplest implementation, you might not pass any values to the code. _But the point is that you have the flexibility to supply a map (dictionary) of dynamic values from GTM to the app (sourced from other GTM variables), and your code can then use those values as needed, ultimately returning some desired value to GTM._

You can also use function call variables to perform custom functions beyond just returning a value: For example, you could use the function call structure to trigger functions in the app that send data to another server, update the state of the app, etc. based on values known to GTM.

## Examples

#### timestamp
* To create a GTM variable that returns a human-readable timestamp, create a new function call variable in GTM called "FC - timestamp" and enter the `{{AnalyticsVariableProviderClass}}` variable as the path/name of the class. In the key-value section pass: `action` = `fetch_variable`, `variable_name` = `timestamp`, `default_value` = `not available`. (Or, in lieu of passing the `default_value` key pass the key `default_none` with no value to have the code return null/nil if the desired value is unavailable, which will then cause any custom dimension mapped to the variable to be dropped from the GA hit. Not a concern for the timestamp example, but can be useful in cases where it's possible no value exists.)

#### logged_in
* To create a GTM variable that returns the user's current logged-in status, create a new function call variable in GTM called "FC - logged_in" and enter the `AnalyticsVariableProviderClass` variable as the path/name of the class. In the key-value section pass: `action` = `fetch_variable`, `variable_name` = `logged_in`, `default_value` = `false`. Then, in the AnalyticsVariableProvider code, find the function that returns the logged-in status and implement whatever app-specific code is needed to return the current status for your app.

## Limitations
Function call variables are a feature of Google Tag Manager (v5) for Android and iOS apps. The sample code provided here will not work with earlier versions of GTM.

## License
Created by Adswerve, Inc. and distributed under the BSD 3-Clause license. See `LICENSE` for more information.

## Contact
Ping us on [#measure](https://measure.slack.com/) or email us at analytics-help@adswerve.com if you have questions.

## Release History

* 0.1.0
  * Initial version
