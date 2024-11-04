@file:OptIn(ExperimentalCompilerApi::class)

package com.rendox.evalplugin

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

@AutoService(CommandLineProcessor::class)
class EvalCommandLineProcessor : CommandLineProcessor {
    companion object {
        const val OPTION_DUMP_OUTPUT = "option-dump-output"

        val ARG_OUTPUT = CompilerConfigurationKey<String>(OPTION_DUMP_OUTPUT)
    }

    override val pluginId: String = BuildConfig.KOTLIN_PLUGIN_ID

    override val pluginOptions: Collection<CliOption> = listOf(
        CliOption(
            optionName = OPTION_DUMP_OUTPUT,
            valueDescription = "<string>",
            description = "path to the output file for IR dumping",
            required = false,
        ),
    )

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration,
    ) {
        return when (option.optionName) {
            OPTION_DUMP_OUTPUT -> configuration.put(ARG_OUTPUT, value)
            else -> throw IllegalArgumentException("Unexpected config option ${option.optionName}")
        }
    }
}
