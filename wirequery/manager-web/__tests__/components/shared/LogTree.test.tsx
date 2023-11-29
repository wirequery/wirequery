// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { LogTree } from '@components/shared/LogTree'
import { ColorSchemeProvider } from '@mantine/core'
import { render, screen } from '@testing-library/react'

jest.mock('react-json-tree', () => ({
  JSONTree: (props: any) => <div>{props.data}</div>,
}))

describe('LogTree', () => {
  it('renders json tree', () => {
    render(
      <ColorSchemeProvider
        colorScheme={undefined as any}
        toggleColorScheme={undefined as any}
      >
        <LogTree display="Something" />
      </ColorSchemeProvider>
    )
    expect(screen.getByText('Something')).not.toBeNull()
  })

  it('renders no times if not set', () => {
    render(
      <ColorSchemeProvider
        colorScheme={undefined as any}
        toggleColorScheme={undefined as any}
      >
        <LogTree display="Something" />
      </ColorSchemeProvider>
    )

    expect(screen.queryByText('1970', { exact: false })).toBeNull()
  })

  it('renders start time if set', () => {
    render(
      <ColorSchemeProvider
        colorScheme={undefined as any}
        toggleColorScheme={undefined as any}
      >
        <LogTree display="Something" startTime={1} />
      </ColorSchemeProvider>
    )

    expect(screen.getByText('1970', { exact: false })).not.toBeNull()
  })

  it('renders end time if set', () => {
    render(
      <ColorSchemeProvider
        colorScheme={undefined as any}
        toggleColorScheme={undefined as any}
      >
        <LogTree display="Something" startTime={0} endTime={100} />
      </ColorSchemeProvider>
    )

    expect(screen.getByText('100 ms', { exact: false })).not.toBeNull()
  })

  it('renders trace id', () => {
    render(
      <ColorSchemeProvider
        colorScheme={undefined as any}
        toggleColorScheme={undefined as any}
      >
        <LogTree display="Something" traceId="abc123" />
      </ColorSchemeProvider>
    )

    expect(screen.getByText('abc123')).not.toBeNull()
  })
})
