// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import React from 'react'

// This context provides the resetUrqlClient method, which can be used
// to invalidate the urql cache after e.g. logging in or signing out.
//
// Example usage:
//
// <ResetUrqlClientContext.Consumer>
//   {(resetUrqlClient) => (
//     ... onClick={() => doLogout(resetUrqlClient)}
//   )}
// </ResetUrqlClientContext.Consumer>

export const ResetUrqlClientContext = React.createContext<
  (() => void) | undefined
>(undefined)
