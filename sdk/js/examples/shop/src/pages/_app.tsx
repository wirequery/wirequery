// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

import { MantineProvider } from '@mantine/core'
import type { AppProps } from 'next/app'
import '@mantine/core/styles.css';
import { SWRConfig } from 'swr';

const fetchWithAccountId = (path: string, fetchArgs: any = {}): Promise<Response> => {
  const result = fetch(path, {
    ...fetchArgs,
    headers: { accountId: "123" },
  });
  return result.then((res) => res.json());
};

export default function MyApp({ Component, pageProps }: AppProps) {
  return <MantineProvider>
    <SWRConfig value={{
      fetcher: fetchWithAccountId,
      revalidateOnFocus: false,
      revalidateOnMount: true,
    }}>
      <Component {...pageProps} />
    </SWRConfig>
  </MantineProvider >
}