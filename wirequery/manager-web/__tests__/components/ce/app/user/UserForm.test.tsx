// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { UserForm } from '@components/ce/app/user/UserForm'
import {
  RegisterMutation,
  UpdateUserMutation,
  UserFormQuery,
  UserFormRolesQuery,
} from '@generated/graphql'
import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { Client, Provider } from 'urql'
import { fromValue } from 'wonka'

describe('UserForm', () => {
  const user = {
    id: '1',
    username: 'Some username',
    password: 'Some password',
    enabled: true,
    roles: 'ADMIN',
  }

  it('renders form containing the necessary fields', () => {
    const mockClient: Partial<Client> = {
      executeQuery: jest.fn(),
      executeMutation: jest.fn(),
      executeSubscription: jest.fn(),
    }
    render(
      <Provider value={mockClient as Client}>
        <UserForm onSave={jest.fn()} onCancel={jest.fn()} />
      </Provider>
    )
    expect(screen.queryByText('Username')).not.toBeNull()
    expect(screen.queryByText('Password')).not.toBeNull()
    expect(screen.queryByText('Enabled')).not.toBeNull()
    expect(screen.queryByText('System Role(s)')).not.toBeNull()
  })

  it('renders form containing existing data if id is passed', () => {
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue<{ data: UserFormQuery & UserFormRolesQuery }>({
        data: {
          user,
          roles: [
            {
              id: '1',
              name: 'ADMIN',
            },
          ],
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
        <UserForm id="1" onSave={jest.fn()} onCancel={jest.fn()} />
      </Provider>
    )
    const expectedFormValues = {
      ...user,
    }
    delete expectedFormValues.id
    delete expectedFormValues.password
    delete expectedFormValues.username
    expect(screen.getByRole('form')).toHaveFormValues(expectedFormValues)
  })

  it('calls a mutation if Save is clicked if there is an id', async () => {
    const executeQuery = jest.fn()
    const executeMutation = jest.fn()
    executeQuery.mockReturnValue(
      fromValue<{ data: UserFormQuery }>({
        data: { user },
      })
    )
    executeMutation.mockReturnValue(
      fromValue<{ data: RegisterMutation & UpdateUserMutation }>({
        data: {
          register: user,
          updateUser: user,
        },
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
        <UserForm id="1" onSave={saveFn} onCancel={jest.fn()} />
      </Provider>
    )
    fireEvent.click(screen.getByText('Save'))
    await waitFor(() => expect(mockClient.executeMutation).toHaveBeenCalled())
    await waitFor(() => expect(saveFn).toHaveBeenCalled())
  })

  it('calls a mutation if Save is clicked if there is no id', async () => {
    const executeMutation = jest.fn()
    executeMutation.mockReturnValue(
      fromValue<{ data: RegisterMutation & UpdateUserMutation }>({
        data: {
          register: user,
          updateUser: user,
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
        <UserForm onSave={saveFn} onCancel={jest.fn()} />
      </Provider>
    )
    fireEvent.change(screen.getByLabelText('Username'), {
      target: { value: 'wouter' },
    })
    fireEvent.change(screen.getByLabelText('Password'), {
      target: { value: 'wouter' },
    })
    fireEvent.click(screen.getByText('Save'))
    await waitFor(() => expect(mockClient.executeMutation).toHaveBeenCalled())
    await waitFor(() => expect(saveFn).toHaveBeenCalled())
  })

  it('calls no mutation if Save if validation failed', async () => {
    const executeMutation = jest.fn()
    const mockClient: Partial<Client> = {
      executeQuery: jest.fn(),
      executeMutation,
      executeSubscription: jest.fn(),
    }
    const saveFn = jest.fn()
    render(
      <Provider value={mockClient as Client}>
        <UserForm onSave={saveFn} onCancel={jest.fn()} />
      </Provider>
    )
    fireEvent.click(screen.getByText('Save'))
    expect(mockClient.executeMutation).not.toHaveBeenCalled()
    expect(saveFn).not.toHaveBeenCalled()
    expect(
      screen.getAllByText('Username must be at least 6 characters long').length
    ).toEqual(1)
    expect(
      screen.getAllByText('Password must be at least 6 characters long').length
    ).toEqual(1)
    expect(executeMutation).not.toHaveBeenCalled()
  })
})
