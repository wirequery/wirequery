// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import React from 'react'

export const AuthorisationsContext = React.createContext<{
  [key: string]: boolean
}>({})
