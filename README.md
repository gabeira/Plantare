# Plantare App
[![CircleCI](https://circleci.com/gh/gabeira/Plantare.svg?style=svg)](https://circleci.com/gh/gabeira/Plantare)

This is an Android App to share Plants. 
Created as a Community Project, Open Source and non-profit.[Give a Like on Facebook](https://www.facebook.com/plantare.mobi)

## Motivation

Help to grow a green environment and make a healthier place to live.
And also practice/teach Android and Kotlin Development.

## Team

As a community project we are open to new developers, designers, testers and other who want to learn and help.
<BR>If you want to make this happen, feel free to submit a Pull Request and Join our WhatsApp [Group](https://chat.whatsapp.com/8q5vsvmz1A7K81FLBOQyxY)
<BR>Special thanks to all [Contributors](https://github.com/gabeira/Plantare/graphs/contributors)

## Installation

Available on [Google Play Store](https://play.google.com/store/apps/details?id=mobi.plantare)

## Contribute

This Project still under development, fell free to help and improve it. 

We used Kotlin for the project, check [how to Start](https://kotlinlang.org/docs/tutorials/kotlin-android.html)

To configure follow steps below:

- Download the code from this Repository via [Android Studio](https://youtu.be/Z98hXV9GmzY) or command line running:

      git clone https://github.com/gabeira/Plantare

- Finally, to Build the Project, you can use Android Studio or from command line just run:

      ./gradlew build

- (Optional) To install debug app from command line use:

      ./adb install /app/build/outputs/apk/debug/app-debug.apk

## External Libs Reference

- [Google Maps](https://developers.google.com/maps/android/)
- [Firebase](https://firebase.google.com/docs/android/setup)
- [Bumptech Glide](https://github.com/bumptech/glide)
- [Apache Commons Lang](https://commons.apache.org/proper/commons-lang/)
- [Facebook](https://developers.facebook.com/docs/android/getting-started)

## Tests

There is some small tests done, but essential for the functionalities, you can run on Android Studio or from the command line,
to run the Unit Tests just use:

    ./gradlew test

Also there is some Connected Android Tests, but this requires to have a device or emulator connected:

    ./gradlew connectedAndroidTest
