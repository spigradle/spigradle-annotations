/*
 * Copyright (c) 2020 Spigradle contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.entree.spigradle.annotations

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Created by JunHyung Im on 2020-08-26
 */
internal fun File.createDirectories(): File = apply {
    parentFile.mkdirs()
    createNewFile()
}

internal fun File.writeGroovy(@Language("Groovy") contents: String): File = apply { writeText(contents) }

internal fun File.writeJava(@Language("Java") contents: String): File = apply { writeText(contents) }

class AnnotationProcessorTest {
    @TempDir
    lateinit var dir: File
    lateinit var buildFile: File
    lateinit var settingsFile: File
    lateinit var subBuildFile: File
    lateinit var subSettingsFile: File
    lateinit var javaFile: File
    lateinit var subJavaFile: File
    val jarPath = System.getProperty("spigradle.annotations.jar")

    private fun createGradleRunner() = GradleRunner.create()
            .withProjectDir(dir)
            .withPluginClasspath(File("build/libs").listFiles()?.toList() ?: emptyList())

    @BeforeTest
    fun setup() {
        buildFile = dir.resolve("build.gradle").createDirectories()
        settingsFile = dir.resolve("settings.gradle").createDirectories().writeGroovy("""
            rootProject.name = 'main'
            include('sub')
        """.trimIndent())
        subBuildFile = dir.resolve("sub/build.gradle").createDirectories()
        subSettingsFile = dir.resolve("sub/settings.gradle").createDirectories()
        javaFile = dir.resolve("src/main/java/Main.java").createDirectories()
        subJavaFile = dir.resolve("sub/src/main/java/Main.java").createDirectories()
    }

    @Test
    fun `test plugin annotation processor`() {
        val spigotJavaFile = dir.resolve("src/main/java/MySpigotPlugin.java").createDirectories().apply {
            writeJava("""
                import kr.entree.spigradle.annotations.SpigotPlugin;
                @SpigotPlugin public class MySpigotPlugin {}
            """.trimIndent())
        }
        dir.resolve("src/main/java/MyBungeePlugin.java").createDirectories().apply {
            writeJava("""
                import kr.entree.spigradle.annotations.BungeePlugin;
                @BungeePlugin public class MyBungeePlugin {}
        """.trimIndent())
        }
        dir.resolve("src/main/java/MyNukkitPlugin.java").createDirectories().apply {
            writeJava("""
                import kr.entree.spigradle.annotations.NukkitPlugin;
                @NukkitPlugin public class MyNukkitPlugin {}
        """.trimIndent())
        }
        val generalJavaFile = dir.resolve("src/main/java/MyGeneralPlugin.java").createDirectories().apply {
            writeJava("""
                import kr.entree.spigradle.annotations.PluginMain;
                @PluginMain public class MyGeneralPlugin {}
        """.trimIndent())
        }
        val spigotPath = dir.resolve(PluginType.SPIGOT.defaultPath)
        val bungeePath = dir.resolve(PluginType.BUNGEE.defaultPath)
        val nukkitPath = dir.resolve(PluginType.NUKKIT.defaultPath)
        val generalPath = dir.resolve(PluginType.GENERAL.defaultPath)
        val pathArgs = listOf(
                PluginType.SPIGOT to spigotPath,
                PluginType.BUNGEE to bungeePath,
                PluginType.NUKKIT to nukkitPath,
                PluginType.GENERAL to generalPath
        ).map { (type, file) -> "-A${type.pathKey}=${file.absolutePath.replace("\\", "/")}" }
        buildFile.writeGroovy("""
            plugins {
                id 'java'
            }
            
            dependencies {
                compileOnly files('$jarPath')
                annotationProcessor files('$jarPath')
            }
            
            compileJava.options.compilerArgs += [${pathArgs.joinToString { "'$it'" }}]
        """.trimIndent())
        val result = createGradleRunner().withArguments("compileJava", "-s").build()
        assertEquals(TaskOutcome.SUCCESS, result.task(":compileJava")?.outcome)
        assertEquals("MySpigotPlugin", spigotPath.readText())
        assertEquals("MyBungeePlugin", bungeePath.readText())
        assertEquals("MyNukkitPlugin", nukkitPath.readText())
        assertEquals("MyGeneralPlugin", generalPath.readText())
        spigotJavaFile.writeJava("""
                import kr.entree.spigradle.annotations.PluginMain;
                @PluginMain public class MySpigotPlugin {}
        """.trimIndent())
        generalJavaFile.writeJava("")
        // Incremental
        val resultB = createGradleRunner().withArguments("compileJava", "-s").build()
        assertEquals(TaskOutcome.SUCCESS, resultB.task(":compileJava")?.outcome)
        assertEquals("MySpigotPlugin", spigotPath.readText())
        assertEquals("MyBungeePlugin", bungeePath.readText())
        assertEquals("MyNukkitPlugin", nukkitPath.readText())
        assertEquals("MySpigotPlugin", generalPath.readText())
    }
}