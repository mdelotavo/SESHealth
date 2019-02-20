# SESHealth consultation app

## Requirements
* IDE:
  * [Android Studio](https://developer.android.com/studio/)
  * [IntelliJ](https://www.jetbrains.com/help/idea/getting-started-with-android-development.html)
* Java Run Time Environment (JRE)
* Java Development Kit (JDK)
* An emulator with API version >= 20 OR a physical Android device


## Setting up the Application Locally
In your terminal, navigate to the directory you want to clone the repository to and run:


`git clone https://gitlab.com/mdelotavo/seshealth`


Open up Android Studio and select `Open an existing Android Studio project`. Search for the directory that you cloned and then expand it. Select SESHealth and click `Open`.
Wait for Gradle to sync all the files
Once the files are all synced, select `Run` from the toolbar and select the device.
The application will now start up and can be used.

## Developing for this Application
Requires the completion of the steps from `Setting up the Application Locally`.


During the development of this application, you should follow [Git Workflow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow) as the branching strategy and developing features.


When preparing for a release, a pull request from `develop` to `master` should be set up and following a successful release, this should then be merged.


The commit for the release should then be tagged according to the [Semantic Versioning Convention](https://semver.org/)
