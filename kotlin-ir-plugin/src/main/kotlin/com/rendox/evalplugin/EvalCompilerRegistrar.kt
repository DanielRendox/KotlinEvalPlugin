package com.rendox.evalplugin

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import java.io.File

@OptIn(ExperimentalCompilerApi::class)
@AutoService(CompilerPluginRegistrar::class)
class EvalCompilerRegistrar : CompilerPluginRegistrar() {
    override val supportsK2 = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val outputPath = configuration.get(EvalCommandLineProcessor.ARG_OUTPUT)
        IrGenerationExtension.registerExtension(
            EvalIrGenerationExtension(dumpOutputFile = outputPath?.let { File(it) }),
        )
    }
}
