// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { QueryForm } from '@components/app/query/QueryForm'
import { ColorSchemeProvider } from '@mantine/core'
import { fireEvent, render, screen } from '@testing-library/react'
import { useState } from 'react'
import { act } from 'react-dom/test-utils'
import { Client, makeOperation, OperationContext, Provider } from 'urql'
import { fromArray } from 'wonka'

jest.mock('@components/app/query-log/QueryLogChart', () => ({
  QueryLogChart: () => <></>,
}))

// The useQueryParam is a bit tricky to test, so just replace it with useState for now.
jest.mock('use-query-params', () => ({
  useQueryParam: () => useState(''),
  withDefault: jest.fn(),
}))

describe('QueryForm', () => {
  beforeEach(() => {
    jest.useFakeTimers()
    jest.clearAllMocks()
  })

  it('renders', () => {
    const mockClient: Partial<Client> = {
      executeQuery: jest.fn(),
      executeMutation: jest.fn(),
      executeSubscription: jest.fn(),
    }
    render(
      <ColorSchemeProvider
        colorScheme={undefined as any}
        toggleColorScheme={undefined as any}
      >
        <Provider value={mockClient as Client}>
          <QueryForm />
        </Provider>
      </ColorSchemeProvider>
    )
    expect(screen.queryByText('Explore')).not.toBeNull()
  })

  it('calls subscription when enter is pressed', () => {
    const executeSubscription = jest.fn()
    const mockClient: Partial<Client> = {
      executeQuery: jest.fn(),
      executeMutation: jest.fn(),
      executeSubscription,
    }
    render(
      <ColorSchemeProvider
        colorScheme={undefined as any}
        toggleColorScheme={undefined as any}
      >
        <Provider value={mockClient as Client}>
          <QueryForm />
        </Provider>
      </ColorSchemeProvider>
    )
    const label = screen.getByLabelText('Search Query')
    label.click()
    fireEvent.change(label, { target: { value: 'some query' } })
    expect(executeSubscription).not.toBeCalled()
    act(() => {
      fireEvent.keyUp(label, { key: 'Enter', code: 'Enter', charCode: 13 })
      jest.runAllTimers()
    })
    expect(executeSubscription).toBeCalled()
  })

  it('does not call subscription when enter is pressed and query is empty', () => {
    const executeSubscription = jest.fn()
    const mockClient: Partial<Client> = {
      executeQuery: jest.fn(),
      executeMutation: jest.fn(),
      executeSubscription,
    }
    render(
      <ColorSchemeProvider
        colorScheme={undefined as any}
        toggleColorScheme={undefined as any}
      >
        <Provider value={mockClient as Client}>
          <QueryForm />
        </Provider>
      </ColorSchemeProvider>
    )
    const label = screen.getByLabelText('Search Query')
    label.click()
    act(() => {
      fireEvent.keyUp(label, { key: 'Enter', code: 'Enter', charCode: 13 })
      jest.runAllTimers()
    })
    expect(executeSubscription).not.toBeCalled()
  })

  it('renders result upon retrieving it', () => {
    const executeSubscription: any = jest.fn((query) => {
      return fromArray([
        {
          operation: makeOperation(
            'subscription',
            query,
            {} as OperationContext
          ),
          data: { query: { queryReport: { message: '{"result":"123"}' } } },
        },
      ])
    })
    const mockClient: Partial<Client> = {
      executeQuery: jest.fn(),
      executeMutation: jest.fn(),
      executeSubscription,
    }
    render(
      <ColorSchemeProvider
        colorScheme={undefined as any}
        toggleColorScheme={undefined as any}
      >
        <Provider value={mockClient as Client}>
          <QueryForm />
        </Provider>
      </ColorSchemeProvider>
    )
    const label = screen.getByLabelText('Search Query')
    label.click()
    fireEvent.change(label, { target: { value: 'some query' } })
    act(() => {
      fireEvent.keyUp(label, { key: 'Enter', code: 'Enter', charCode: 13 })
      jest.runAllTimers()
    })
    expect(executeSubscription).toBeCalled()
    expect(screen.getAllByText('"123"')).toHaveLength(1)
  })

  // Possible test improvements:
  // - Test results reset after pressing Enter
  // - No more results after error
  // - No more results after reaching limit
})
