// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { Loader } from '@mantine/core'
import { useEffect, useState } from 'react'

export const LoadingScreen = () => {
  const [visible, setVisible] = useState(false)

  useEffect(() => {
    setTimeout(() => {
      setVisible(true)
    }, 500)
  })

  return visible ? <Loader variant="dots" /> : <></>
}
