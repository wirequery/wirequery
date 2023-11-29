// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { QueryLogList } from '@components/shared/app/query-log/QueryLogList'
import { QueryLogListQuery } from '@generated/graphql'
import { ColorSchemeProvider } from '@mantine/core'
import { render, screen } from '@testing-library/react'
import { act } from 'react-dom/test-utils'
import { Client, Provider } from 'urql'
import { fromValue } from 'wonka'

jest.mock('@components/shared/app/query-log/QueryLogChart', () => ({
  QueryLogChart: () => <></>,
}))

describe('QueryLogList', () => {
  const queryLog = {
    id: '1',
    storedQueryId: 1,
    message: '{"result": {"message": "Some message"}}',
    startTime: 0,
    endTime: 100,
  }

  it('renders entries when data is fetched', () => {
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue<{ data: QueryLogListQuery }>({
        data: {
          queryLogs: [queryLog],
        },
      })
    )
    const mockClient: Partial<Client> = {
      executeQuery,
      executeMutation: jest.fn(),
      executeSubscription: jest.fn(),
    }
    act(() => {
      render(
        <ColorSchemeProvider
          colorScheme={undefined as any}
          toggleColorScheme={undefined as any}
        >
          <Provider value={mockClient as Client}>
            <QueryLogList />
          </Provider>
        </ColorSchemeProvider>
      )
    })
    expect(screen.getAllByText('"Some message"')).not.toHaveLength(0)
  })
})
