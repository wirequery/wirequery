// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { TraceDetails } from '@components/app/trace/TraceDetails'
import { render, screen } from '@testing-library/react'
import { act } from 'react-dom/test-utils'
import { Client, Provider } from 'urql'
import { fromValue } from 'wonka'

jest.mock('@components/app/trace/TraceTimeline', () => ({
  TraceTimeline: () => <div>TraceTimeline</div>,
}))

describe('TraceDetails', () => {
  const storedQuery = {
    id: '1',
    name: 'Some stored query',
  }

  const queryLogByTrace = [
    {
      id: '1',
      name: 'Some stored query',
      storedQueryId: 1,
      message: '{"a": "part of a tree"}',
      appName: 'My App',
      traceId: 'mytrace',
      startTime: 0,
      endTime: 0,
    },
  ]

  it('renders details when data is fetched', () => {
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue({
        data: { storedQuery, queryLogByTrace },
      })
    )
    const mockClient: Partial<Client> = {
      executeQuery,
      executeMutation: jest.fn(),
      executeSubscription: jest.fn(),
    }
    act(() => {
      render(
        <Provider value={mockClient as Client}>
          <TraceDetails storedQueryId="1" traceId="mytrace" />
        </Provider>
      )
    })
    expect(
      screen.getAllByText('Trace of ' + storedQuery.name)
    ).not.toHaveLength(0)

    expect(screen.getAllByText('mytrace')).not.toHaveLength(0)
  })
})
