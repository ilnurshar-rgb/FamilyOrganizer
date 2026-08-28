// Корневой build.gradle.kts — только объявление версий плагинов,
// сами плагины подключаются в app/build.gradle.kts.
plugins {
    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
    id("org.jetbrains.kotlin.kapt") version "2.0.21" apply false
    id("com.google.gms.google-services") version "4.5.0" apply false
}
