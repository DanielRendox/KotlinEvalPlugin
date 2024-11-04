package com.rendox.evalplugin

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.util.dump
import java.io.File

class EvalIrGenerationExtension(private val dumpOutputFile: File? = null) : IrGenerationExtension {

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        moduleFragment.transform(EvalFunctionTransformer(pluginContext), null)
        dumpOutputFile?.writeText(moduleFragment.dump())
    }
}
