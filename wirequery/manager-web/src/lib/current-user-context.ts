// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { User } from '@generated/graphql'
import React from 'react'

export const CurrentUserContext = React.createContext<User>(undefined as any)
