# ProGuard rules for EmergencyPower System
-keep class com.example.emergencypower.model.** { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
