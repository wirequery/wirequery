// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { StoredQueryList } from '@components/app/stored-query/StoredQueryList'
import { Client, Provider } from 'urql'
import { act } from 'react-dom/test-utils'
import { fromValue } from 'wonka'

describe('StoredQueryList', () => {
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
  }

  it('forwards to storedQuery page on Show', () => {
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue({
        data: {
          storedQuerys: [storedQuery],
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
        <Provider value={mockClient as Client}>
          <StoredQueryList onCreateStoredQuery={() => ({})} />
        </Provider>
      )
    })
    expect(screen.getByText(storedQuery.name)).toHaveAttribute(
      'href',
      '/stored-querys/1'
    )
  })

  it('renders entries when data is fetched', () => {
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue({
        data: {
          storedQuerys: [storedQuery],
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
        <Provider value={mockClient as Client}>
          <StoredQueryList onCreateStoredQuery={() => ({})} />
        </Provider>
      )
    })
    expect(screen.getAllByText(storedQuery.name)).not.toHaveLength(0)
    expect(screen.getAllByText(storedQuery.application.name)).not.toHaveLength(
      0
    )
  })

  it('shows empty message if there are no entries', async () => {
    const onCreateStoredQueryMock = jest.fn()
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue({
        data: {
          storedQuerys: [],
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
        <Provider value={mockClient as Client}>
          <StoredQueryList onCreateStoredQuery={onCreateStoredQueryMock} />
        </Provider>
      )
    })
    expect(screen.getAllByText('No queries found')).not.toHaveLength(0)
    expect(onCreateStoredQueryMock).not.toBeCalled()
    await waitFor(() => {
      act(() => {
        fireEvent(
          screen.getByText('Create new query'),
          new MouseEvent('click', {
            bubbles: true,
            cancelable: true,
          })
        )
      })
    })
    expect(onCreateStoredQueryMock).toBeCalled()
  })

  it('calls mutation when Delete is clicked and confirmed', async () => {
    jest.spyOn(window, 'confirm').mockReturnValueOnce(true)
    const executeQuery = jest.fn()
    const executeMutation = jest.fn()
    executeQuery.mockReturnValue(
      fromValue({
        data: {
          storedQuerys: [storedQuery],
        },
      })
    )
    executeMutation.mockReturnValue(
      fromValue({
        data: {},
      })
    )
    const mockClient: Partial<Client> = {
      executeQuery,
      executeMutation,
      executeSubscription: jest.fn(),
    }
    act(() => {
      render(
        <Provider value={mockClient as Client}>
          <StoredQueryList onCreateStoredQuery={() => ({})} />
        </Provider>
      )
    })
    await waitFor(() => {
      act(() => {
        fireEvent(
          screen.getByTitle('Delete'),
          new MouseEvent('click', {
            bubbles: true,
            cancelable: true,
          })
        )
      })
    })
    expect(mockClient.executeMutation).toBeCalled()
  })

  it('does not call mutation when Delete is clicked and not confirmed', async () => {
    jest.spyOn(window, 'confirm').mockReturnValueOnce(false)
    const executeQuery = jest.fn()
    const executeMutation = jest.fn()
    executeQuery.mockReturnValue(
      fromValue({
        data: {
          storedQuerys: [storedQuery],
        },
      })
    )
    executeMutation.mockReturnValue(
      fromValue({
        data: {},
      })
    )
    const mockClient: Partial<Client> = {
      executeQuery,
      executeMutation,
      executeSubscription: jest.fn(),
    }
    act(() => {
      render(
        <Provider value={mockClient as Client}>
          <StoredQueryList onCreateStoredQuery={() => ({})} />
        </Provider>
      )
    })
    await waitFor(() => {
      act(() => {
        fireEvent(
          screen.getByTitle('Delete'),
          new MouseEvent('click', {
            bubbles: true,
            cancelable: true,
          })
        )
      })
    })
    expect(mockClient.executeMutation).not.toBeCalled()
  })
})
