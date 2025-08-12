# Keep any classes annotated with @Keep
-keep @interface androidx.annotation.Keep
-keep @androidx.annotation.Keep class * { *; }

# If using Ktor, you often need to keep certain things related to OkHttp and Ktor:
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# If using Kotlin Serialization:
-keep class kotlinx.serialization.** { *; }
-dontwarn kotlinx.serialization.**

# If using @Parcelize:
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}
-dontwarn org.slf4j.impl.**

# Keep Fragment constructors
-keep public class * extends androidx.fragment.app.Fragment {
    public <init>();
}
