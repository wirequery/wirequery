// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { render, screen } from '@testing-library/react'
import { StoredQueryDetails } from '@components/shared/app/stored-query/StoredQueryDetails'
import { Client, Provider } from 'urql'
import { act } from 'react-dom/test-utils'
import { fromValue } from 'wonka'
import { ColorSchemeProvider } from '@mantine/core'
import { StoredQueryDetailsQuery, StoredQueryType } from '@generated/graphql'

jest.mock('@components/shared/app/query-log/QueryLogChart', () => ({
  QueryLogChart: () => <></>,
}))

describe('StoredQueryDetails', () => {
  const storedQuery = {
    id: '1',
    applicationId: 1,
    application: {
      id: '2',
      name: 'Some application name',
    },
    name: 'Some name',
    type: StoredQueryType.Query,
    query: 'Some query',
    queryLimit: 10,
    endDate: '2000-01-01',
    createdAt: '1970-01-01T00:00:00Z',
    updatedAt: '1970-02-02T00:00:00Z',
  }

  it('renders details when data is fetched', () => {
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue<{ data: StoredQueryDetailsQuery }>({
        data: { storedQuery },
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
            <StoredQueryDetails id="1" />
          </Provider>
        </ColorSchemeProvider>
      )
    })
    expect(screen.getAllByText(storedQuery.application.name)).not.toHaveLength(
      0
    )
    expect(screen.getAllByText(storedQuery.name)).not.toHaveLength(0)
    expect(screen.getAllByText(storedQuery.query)).not.toHaveLength(0)
    expect(
      screen.getAllByText('Max ' + storedQuery.queryLimit)
    ).not.toHaveLength(0)
    expect(
      screen.getAllByText(
        'Ends ' + new Date(storedQuery.endDate).toLocaleString()
      )
    ).not.toHaveLength(0)
    expect(
      screen.getAllByText(
        'Created ' + new Date(storedQuery.createdAt).toLocaleString()
      )
    ).not.toHaveLength(0)
  })
})
