import { QueryLogList } from '@components/app/query-log/QueryLogList'
import { ColorSchemeProvider } from '@mantine/core'
import { render, screen } from '@testing-library/react'
import { act } from 'react-dom/test-utils'
import { Client, Provider } from 'urql'
import { fromValue } from 'wonka'

jest.mock('@components/app/query-log/QueryLogChart', () => ({
  QueryLogChart: () => <></>,
}))

describe('QueryLogList', () => {
  const queryLog = {
    id: '1',
    storedQueryId: 1,
    message: '{"result": {"message": "Some message"}}',
  }

  it('renders entries when data is fetched', () => {
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue({
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
