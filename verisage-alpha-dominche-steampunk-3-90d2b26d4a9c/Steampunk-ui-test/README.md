# The UI Test Project

Note that running the ui-test script will delete your current version of the app.

## Setup

* run `cd <path_to_alphadominche_repo>/Steampunk-ui-test`

* run `export PATH="$PATH:<path_to_your_android_sdks>/platform-tools/"` Your android sdks are most likely in ~/android-sdks. Remember, environment variables don't persist between sessions, so if you don't want to have to run the above command every time you open a new terminal session, add it to your .bashrc or .bash_profile file (or to your .zshrc file if you use the one true shell).

* run `<path_to_your_android_sdks>/tools/android create uitest-project -n Steampunk-ui-test -t android-18 -p ./'` in order to tell the ant build where your android sdks are.

* go into the device settings->security->screen lock and choose None. Otherwise, the tests will fail unless the device is always unlocked.

Import the Steampunk-ui-test project into Eclipse if you want to edit the tests there. However, because they use an ant build to compile, Eclipse isn't strictly required.