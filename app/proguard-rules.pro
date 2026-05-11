# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/hotaro/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep rules here:

# Hilt rules
-keep class dagger.hilt.android.internal.managers.** { *; }
-keep class * implements dagger.hilt.internal.GeneratedComponent { *; }
-keep class * implements dagger.hilt.internal.ComponentEntryPoint { *; }
-keep class * implements dagger.hilt.android.internal.builders.GeneratedComponentBuilder { *; }
