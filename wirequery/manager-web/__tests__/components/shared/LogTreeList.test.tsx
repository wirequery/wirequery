// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { LogTreeList } from '@components/shared/LogTreeList'
import { ColorSchemeProvider } from '@mantine/core'
import { fireEvent, render, screen } from '@testing-library/react'

jest.mock('@components/shared/app/trace/TraceDetails', () => ({
  TraceDetails: (props: any) => (
    <>
      <div>Open Modal</div>
      <div>{props.storedQueryId}</div>
      <div>{props.traceId}</div>
    </>
  ),
}))

jest.mock('react-json-tree', () => ({
  JSONTree: (props: any) => <div>{props.data}</div>,
}))

describe('LogTreeList', () => {
  it('renders json trees containing results', () => {
    render(
      <ColorSchemeProvider
        colorScheme={undefined as any}
        toggleColorScheme={undefined as any}
      >
        <LogTreeList
          rows={[{ message: '{"result":"item"}' }] as any}
          storedQueryId={1}
          extendedTracing={false}
        />
      </ColorSchemeProvider>
    )
    expect(screen.queryByText('item')).not.toBeNull()
  })

  it('renders json trees containing errors', () => {
    render(
      <ColorSchemeProvider
        colorScheme={undefined as any}
        toggleColorScheme={undefined as any}
      >
        <LogTreeList
          rows={[{ message: '{"error":"item"}' }] as any}
          storedQueryId={1}
          extendedTracing={false}
        />
      </ColorSchemeProvider>
    )
    expect(screen.getByText('item')).not.toBeNull()
  })

  it('does not show Extended Tracing if it is false', () => {
    render(
      <ColorSchemeProvider
        colorScheme={undefined as any}
        toggleColorScheme={undefined as any}
      >
        <LogTreeList
          rows={[{ message: '{"result":"item"}' }] as any}
          storedQueryId={1}
          extendedTracing={false}
        />
      </ColorSchemeProvider>
    )
    expect(screen.queryByTitle('Extended Tracing')).toBeNull()
  })

  it('renders trace details on click on zoom icon', () => {
    render(
      <ColorSchemeProvider
        colorScheme={undefined as any}
        toggleColorScheme={undefined as any}
      >
        <LogTreeList
          rows={[{ message: '{"result":"item"}', traceId: 'abc123' }] as any}
          storedQueryId={1}
          extendedTracing={true}
        />
      </ColorSchemeProvider>
    )
    expect(screen.queryByTitle('Extended Tracing')).not.toBeNull()
    expect(screen.queryByText('Open Modal')).toBeNull()

    fireEvent.click(screen.getByTitle('Extended Tracing'))
    expect(screen.queryByText('Open Modal')).not.toBeNull()
  })
})
