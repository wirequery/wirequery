// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

package com.wirequery.spring5

class Sleeper {
    fun sleep(millis: Long) {
        Thread.sleep(millis)
    }
}
