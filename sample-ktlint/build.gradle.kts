plugins {
    kotlin("jvm") version libs.versions.kotlin
}

dependencies {
    ktlint(libs.ktlint, ::configureKtlint)
    ktlint(project(":$RELEASE_ARTIFACT-ktlint"))
}
