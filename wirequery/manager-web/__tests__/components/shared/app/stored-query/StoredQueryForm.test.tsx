// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { StoredQueryForm } from '@components/shared/app/stored-query/StoredQueryForm'
import { Client, Provider } from 'urql'
import { fromValue } from 'wonka'
import { CreateStoredQueryMutation } from '@generated/graphql'

describe('StoredQueryForm', () => {
  it('renders form containing the necessary fields', () => {
    const mockClient: Partial<Client> = {
      executeQuery: jest.fn(),
      executeMutation: jest.fn(),
      executeSubscription: jest.fn(),
    }
    render(
      <Provider value={mockClient as Client}>
        <StoredQueryForm onSave={jest.fn()} onCancel={jest.fn()} />
      </Provider>
    )
    expect(screen.queryByText('Name')).not.toBeNull()
    expect(screen.queryAllByText('Query')).toHaveLength(2)
    expect(screen.queryByText('Query Limit')).not.toBeNull()
    expect(screen.queryByText('Ends at')).not.toBeNull()
  })

  it('calls a mutation if Save is clicked', async () => {
    const executeMutation = jest.fn()
    executeMutation.mockReturnValue(
      fromValue<{ data: CreateStoredQueryMutation }>({
        data: {
          createStoredQuery: {
            id: '1'
          }
        },
      })
    )
    const mockClient: Partial<Client> = {
      executeQuery: jest.fn(),
      executeMutation,
      executeSubscription: jest.fn(),
    }
    const saveFn = jest.fn()
    render(
      <Provider value={mockClient as Client}>
        <StoredQueryForm onSave={saveFn} onCancel={jest.fn()} />
      </Provider>
    )
    fireEvent.change(screen.getByLabelText('Name', { exact: false }), {
      target: { value: 'Some Query' },
    })
    fireEvent.click(screen.getByText('Save'))
    await waitFor(() => expect(mockClient.executeMutation).toHaveBeenCalled())
    await waitFor(() => expect(saveFn).toHaveBeenCalled())
  })

  it('calls no mutation when validation fails', async () => {
    const executeMutation = jest.fn()
    const mockClient: Partial<Client> = {
      executeQuery: jest.fn(),
      executeMutation,
      executeSubscription: jest.fn(),
    }
    const saveFn = jest.fn()
    render(
      <Provider value={mockClient as Client}>
        <StoredQueryForm onSave={saveFn} onCancel={jest.fn()} />
      </Provider>
    )
    fireEvent.click(screen.getByText('Save'))
    expect(mockClient.executeMutation).not.toHaveBeenCalled()
    expect(saveFn).not.toHaveBeenCalled()
    expect(executeMutation).not.toHaveBeenCalled()
  })
})
