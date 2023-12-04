// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.core.query

import dev.cel.common.CelOptions
import dev.cel.common.types.SimpleType
import dev.cel.compiler.CelCompilerFactory
import dev.cel.extensions.CelExtensions
import dev.cel.parser.CelMacro
import dev.cel.runtime.CelRuntime.Program
import dev.cel.runtime.CelRuntimeFactory

class ExpressionCompiler {
    fun compile(expression: String): Program {
        return CEL_RUNTIME.createProgram(CEL_COMPILER.compile(expression).ast)
    }

    private companion object {
        private val CEL_COMPILER =
            CelCompilerFactory
                .standardCelCompilerBuilder()
                .addMacros(CelMacro.STANDARD_MACROS)
                .addLibraries(CelExtensions.strings())
                .addLibraries(CelExtensions.bindings())
                .addLibraries(CelExtensions.encoders())
                .addLibraries(CelExtensions.math(CelOptions.DEFAULT))
                .addLibraries(CelExtensions.protos())
                .addVar("it", SimpleType.ANY)
                .addVar("context", SimpleType.ANY)
                .build()

        private val CEL_RUNTIME =
            CelRuntimeFactory
                .standardCelRuntimeBuilder()
                .addLibraries(CelExtensions.strings())
                .addLibraries(CelExtensions.encoders())
                .addLibraries(CelExtensions.math(CelOptions.DEFAULT))
                .build()

    }
}
