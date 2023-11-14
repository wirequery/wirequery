// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

import { MantineProvider } from '@mantine/core'
import type { AppProps } from 'next/app'
import '@mantine/core/styles.css';

export default function MyApp({ Component, pageProps }: AppProps) {
  return <MantineProvider>
    <Component {...pageProps} />
  </MantineProvider>
}