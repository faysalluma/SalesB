# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Some transitive JVM-only dependencies reference the SLF4J binder, which is not
# packaged on Android and is safe to ignore in release builds.
-dontwarn org.slf4j.impl.StaticLoggerBinder

# Keep API request/response models intact. Several request bodies are plain
# Kotlin data classes serialized reflectively by Gson, so release obfuscation
# can otherwise change JSON keys while debug still works.
-keep class com.groupec.salesb.core.model.data.** { *; }
-keep class com.groupec.salesb.core.network.model.** { *; }

# Keep fields annotated for Gson response parsing.
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# iText relies on reflective/internal event classes that can break under R8 in
# release builds with errors such as "AbstractITextEvent is only for internal
# usage". Keep the PDF stack intact.
-keep class com.itextpdf.** { *; }
-dontwarn com.itextpdf.**
