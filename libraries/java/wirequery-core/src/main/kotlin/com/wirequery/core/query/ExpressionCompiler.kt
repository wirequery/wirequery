package com.wirequery.core.query

import org.projectnessie.cel.checker.Decls
import org.projectnessie.cel.tools.Script
import org.projectnessie.cel.tools.ScriptHost
import org.projectnessie.cel.types.jackson.JacksonRegistry

class ExpressionCompiler {
    private val scriptHost = ScriptHost.newBuilder()
        .registry(JacksonRegistry.newRegistry())
        .build()

    fun compile(expression: String): Script {
        return scriptHost
            .buildScript(expression)
            .withDeclarations(
                Decls.newVar("it", Decls.Any),
                Decls.newVar("context", Decls.Any),
            )
            .build()
    }
}
