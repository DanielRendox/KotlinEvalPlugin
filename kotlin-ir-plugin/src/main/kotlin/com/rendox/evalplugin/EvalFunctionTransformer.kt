package com.rendox.evalplugin

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

class EvalFunctionTransformer(private val context: IrPluginContext) : IrElementTransformerVoid() {

    override fun visitCall(expression: IrCall): IrExpression {
        val transformedExpression = super.visitCall(expression)

        val function = expression.symbol.owner
        if (function.name.asString().startsWith("eval")) {
            val args = expression.valueArguments.map { it as? IrConst<*> }

            val firstArg = args[0]?.value as? Int ?: return transformedExpression
            val secondArg = args[1]?.value as? Int ?: return transformedExpression
            val operationName = getOperationName(function) ?: return transformedExpression

            val result = when (operationName) {
                "plus" -> firstArg + secondArg
                "minus" -> firstArg - secondArg
                "times" -> firstArg * secondArg
                "div" -> if (secondArg != 0) firstArg / secondArg else return transformedExpression
                "rem" -> if (secondArg != 0) firstArg % secondArg else return transformedExpression
                else -> return transformedExpression
            }

            return IrConstImpl.int(
                startOffset = expression.startOffset,
                endOffset = expression.endOffset,
                type = context.irBuiltIns.intType,
                value = result,
            )
        }

        return transformedExpression
    }

    private fun getOperationName(function: IrSimpleFunction): String? {
        val body = function.body ?: return null

        val returnExpression = (body as? IrBlockBody)?.statements?.firstOrNull() as? IrReturn
            ?: return null

        val binaryOperation = returnExpression.value as? IrCall ?: return null
        return binaryOperation.symbol.owner.name.asString()
    }
}
