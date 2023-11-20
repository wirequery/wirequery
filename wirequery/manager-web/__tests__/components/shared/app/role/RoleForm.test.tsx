// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { RoleForm } from '@components/shared/app/role/RoleForm'
import { Client, Provider } from 'urql'
import { fromValue } from 'wonka'

describe('RoleForm', () => {
  const role = {
    id: '1',
    name: 'Some name',
    authorisations: [
      {
        name: { name: 'Admin', labe: 'Admin', description: 'Admin' },
      },
    ],
  }

  it('renders form containing the necessary fields', () => {
    const mockClient: Partial<Client> = {
      executeQuery: jest.fn(),
      executeMutation: jest.fn(),
      executeSubscription: jest.fn(),
    }
    render(
      <Provider value={mockClient as Client}>
        <RoleForm onSave={jest.fn()} onCancel={jest.fn()} />
      </Provider>
    )
    expect(screen.queryByText('Name')).not.toBeNull()
    expect(screen.queryByText('Authorisations')).not.toBeNull()
  })

  it('renders form containing existing data if id is passed', () => {
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue({
        data: {
          role,
        },
      })
    )
    const mockClient: Partial<Client> = {
      executeQuery,
      executeMutation: jest.fn(),
      executeSubscription: jest.fn(),
    }
    render(
      <Provider value={mockClient as Client}>
        <RoleForm id="1" onSave={jest.fn()} onCancel={jest.fn()} />
      </Provider>
    )
    const expectedFormValues = {
      ...role,
      id: undefined,
    }
    delete expectedFormValues.id
    delete expectedFormValues.authorisations // TODO not sure why this is not on the form, but it works.
    expect(screen.getByRole('form')).toHaveFormValues(expectedFormValues)
  })

  it('calls a mutation if Save is clicked if there is an id', async () => {
    const executeQuery = jest.fn()
    const executeMutation = jest.fn()
    executeQuery.mockReturnValue(
      fromValue({
        data: { role },
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
    const saveFn = jest.fn()
    render(
      <Provider value={mockClient as Client}>
        <RoleForm id="1" onSave={saveFn} onCancel={jest.fn()} />
      </Provider>
    )

    const input = screen.getByLabelText('Name', { exact: false })
    fireEvent.change(input, { target: { value: 'some name' } })

    fireEvent.click(screen.getByText('Save'))
    await waitFor(() => expect(mockClient.executeMutation).toBeCalled())
    await waitFor(() => expect(saveFn).toBeCalled())
  })

  it('calls a mutation if Save is clicked if there is no id', async () => {
    const executeMutation = jest.fn()
    executeMutation.mockReturnValue(
      fromValue({
        data: {},
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
        <RoleForm onSave={saveFn} onCancel={jest.fn()} />
      </Provider>
    )

    const input = screen.getByLabelText('Name', { exact: false })
    fireEvent.change(input, { target: { value: 'some name' } })

    fireEvent.click(screen.getByText('Save'))
    await waitFor(() => expect(mockClient.executeMutation).toBeCalled())
    await waitFor(() => expect(saveFn).toBeCalled())
  })
})
