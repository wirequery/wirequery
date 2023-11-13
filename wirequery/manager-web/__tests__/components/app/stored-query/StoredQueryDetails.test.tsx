import { render, screen } from '@testing-library/react'
import { StoredQueryDetails } from '@components/app/stored-query/StoredQueryDetails'
import { Client, Provider } from 'urql'
import { act } from 'react-dom/test-utils'
import { fromValue } from 'wonka'
import { ColorSchemeProvider } from '@mantine/core'

jest.mock('@components/app/query-log/QueryLogChart', () => ({
  QueryLogChart: () => <></>,
}))

describe('StoredQueryDetails', () => {
  const storedQuery = {
    id: '1',
    applicationId: 1,
    application: {
      name: 'Some application name',
    },
    name: 'Some name',
    type: 'Some type',
    query: 'Some query',
    queryLimit: 10,
    endDate: '2000-01-01',
    createdAt: '1970-01-01T00:00:00Z',
    updatedAt: '1970-02-02T00:00:00Z',
  }

  it('renders details when data is fetched', () => {
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue({
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
