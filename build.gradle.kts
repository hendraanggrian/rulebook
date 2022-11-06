import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.MavenPublishBasePlugin
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.maven.publish) apply false
}

allprojects {
    group = RELEASE_GROUP
    version = RELEASE_VERSION
}

subprojects {
    plugins.withType<KotlinPluginWrapper> {
        kotlinExtension.jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get()))
        }
    }
    plugins.withType<MavenPublishBasePlugin> {
        configure<MavenPublishBaseExtension> {
            publishToMavenCentral(SonatypeHost.S01)
            signAllPublications()
            pom(::pom)
            configure(KotlinJvm(JavadocJar.Dokka("dokkaJavadoc")))
        }
    }
}

tasks {
    register(LifecycleBasePlugin.CLEAN_TASK_NAME) {
        delete(buildDir)
    }
    dokkaHtmlMultiModule {
        outputDirectory.set(buildDir.resolve("dokka/dokka"))
    }
}
