@file:OptIn(ExperimentalCompilerApi::class)

package com.rendox.evalplugin

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.PluginOption
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test
import java.io.File
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

const val DEFAULT_OUTPUT_FILE_NAME = "eval-dump.txt"

class IrPluginTest {
    @Test
    fun testAdditionInEvalFunction() {
        val result = compile(
            sourceFile = SourceFile.kotlin(
                name = "main.kt",
                """
                    fun main() {
                        // evalAdd(1, 2) must be evaluated as 3
                        println(evalAdd(1, 2))
                    }
                    
                    fun evalAdd(a: Int, b: Int) = a + b
                """.trimIndent(),
            ),
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        val irDumpContent = getDumpFilePath(result).readText()
        assertContains(irDumpContent, "CONST Int type=kotlin.Int value=3")
        assertFalse(irDumpContent.contains("CONST Int type=kotlin.Int value=1"))
        assertFalse(irDumpContent.contains("CONST Int type=kotlin.Int value=2"))
    }

    @Test
    fun testSubtractionInEvalFunction() {
        val result = compile(
            sourceFile = SourceFile.kotlin(
                name = "main.kt",
                """
                fun main() {
                    // evalMinus(10, 4) must be evaluated as 6
                    println(evalMinus(10, 4))
                }
                
                fun evalMinus(a: Int, b: Int) = a - b
                """.trimIndent(),
            ),
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        val irDumpContent = getDumpFilePath(result).readText()
        assertContains(irDumpContent, "CONST Int type=kotlin.Int value=6")
        assertFalse(irDumpContent.contains("CONST Int type=kotlin.Int value=10"))
        assertFalse(irDumpContent.contains("CONST Int type=kotlin.Int value=4"))
    }

    @Test
    fun testMultiplicationInEvalFunction() {
        val result = compile(
            sourceFile = SourceFile.kotlin(
                name = "main.kt",
                """
                fun main() {
                    // evalTimes(3, 5) must be evaluated as 15
                    println(evalTimes(3, 5))
                }
                
                fun evalTimes(a: Int, b: Int) = a * b
                """.trimIndent(),
            ),
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        val irDumpContent = getDumpFilePath(result).readText()
        assertContains(irDumpContent, "CONST Int type=kotlin.Int value=15")
        assertFalse(irDumpContent.contains("CONST Int type=kotlin.Int value=3"))
        assertFalse(irDumpContent.contains("CONST Int type=kotlin.Int value=5"))
    }

    @Test
    fun testDivisionInEvalFunction() {
        val result = compile(
            sourceFile = SourceFile.kotlin(
                name = "main.kt",
                """
                fun main() {
                    // evalDiv(20, 4) must be evaluated as 5
                    println(evalDiv(20, 4))
                }
                
                fun evalDiv(a: Int, b: Int) = a / b
                """.trimIndent(),
            ),
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        val irDumpContent = getDumpFilePath(result).readText()
        assertContains(irDumpContent, "CONST Int type=kotlin.Int value=5")
        assertFalse(irDumpContent.contains("CONST Int type=kotlin.Int value=20"))
        assertFalse(irDumpContent.contains("CONST Int type=kotlin.Int value=4"))
    }

    @Test
    fun testRemainderInEvalFunction() {
        val result = compile(
            sourceFile = SourceFile.kotlin(
                name = "main.kt",
                """
                fun main() {
                    // evalRem(23, 5) must be evaluated as 3
                    println(evalRem(23, 5))
                }
                
                fun evalRem(a: Int, b: Int) = a % b
                """.trimIndent(),
            ),
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        val irDumpContent = getDumpFilePath(result).readText()
        assertContains(irDumpContent, "CONST Int type=kotlin.Int value=3")
        assertFalse(irDumpContent.contains("CONST Int type=kotlin.Int value=23"))
        assertFalse(irDumpContent.contains("CONST Int type=kotlin.Int value=5"))
    }

    private fun getDumpFilePath(result: JvmCompilationResult): File {
        val parentDir = result.outputDirectory.parentFile
        val irDumpFilePath = "${parentDir.absolutePath}${File.separator}eval-dump.txt"
        val irDumpFile = File(irDumpFilePath)
        assertTrue(irDumpFile.exists(), "IR dump file does not exist")
        return irDumpFile
    }
}

fun compile(
    sourceFiles: List<SourceFile>,
    plugin: CompilerPluginRegistrar = EvalCompilerRegistrar(),
    outputFilePath: String? = null,
): JvmCompilationResult {
    return KotlinCompilation().apply {
        sources = sourceFiles
        compilerPluginRegistrars = listOf(plugin)
        inheritClassPath = true

        compilerPluginRegistrars = listOf(EvalCompilerRegistrar())
        commandLineProcessors = listOf(EvalCommandLineProcessor())

        val defaultOutputFilePath = "${workingDir.absolutePath}${File.separator}${DEFAULT_OUTPUT_FILE_NAME}"
        pluginOptions = listOf(
            PluginOption(
                pluginId = BuildConfig.KOTLIN_PLUGIN_ID,
                optionName = EvalCommandLineProcessor.OPTION_DUMP_OUTPUT,
                optionValue = outputFilePath ?: defaultOutputFilePath,
            ),
        )
    }.compile()
}

fun compile(
    sourceFile: SourceFile,
    plugin: CompilerPluginRegistrar = EvalCompilerRegistrar(),
    outputFilePath: String? = null,
): JvmCompilationResult {
    return compile(
        sourceFiles = listOf(sourceFile),
        plugin = plugin,
        outputFilePath = outputFilePath,
    )
}
