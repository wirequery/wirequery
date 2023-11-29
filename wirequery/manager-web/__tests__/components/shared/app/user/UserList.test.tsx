// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { UserList } from '@components/shared/app/user/UserList'
import { Client, Provider } from 'urql'
import { act } from 'react-dom/test-utils'
import { fromValue } from 'wonka'
import { DeleteUserMutation, UsersQuery } from '@generated/graphql'

describe('UserList', () => {
  const user = {
    id: '1',
    username: 'Some username',
    enabled: true,
    roles: 'Some roles',
    createdAt: '1970-01-01T00:00:00Z'
  }

  it('forwards to user page on Show', () => {
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue<{ data: UsersQuery }>({
        data: {
          users: [user],
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
          <UserList />
        </Provider>
      )
    })
    expect(screen.getByText(user.username)).toHaveAttribute('href', '/users/1')
  })

  it('renders entries when data is fetched', () => {
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue<{ data: UsersQuery }>({
        data: {
          users: [user],
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
          <UserList />
        </Provider>
      )
    })
    expect(screen.getAllByText(user.username)).not.toHaveLength(0)
    expect(
      screen.getAllByText(user.enabled ? 'Enabled' : 'Disabled')
    ).not.toHaveLength(0)
    expect(screen.getAllByText(user.roles)).not.toHaveLength(0)
  })

  it('calls mutation when Delete is clicked and confirmed', async () => {
    jest.spyOn(window, 'confirm').mockReturnValueOnce(true)
    const executeQuery = jest.fn()
    const executeMutation = jest.fn()
    executeQuery.mockReturnValue(
      fromValue<{ data: UsersQuery }>({
        data: {
          users: [user],
        },
      })
    )
    executeMutation.mockReturnValue(
      fromValue<{ data: DeleteUserMutation }>({
        data: {
          deleteUser: true
        },
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
          <UserList />
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
    expect(mockClient.executeMutation).toHaveBeenCalled()
  })

  it('does not call mutation when Delete is clicked and not confirmed', async () => {
    jest.spyOn(window, 'confirm').mockReturnValueOnce(false)
    const executeQuery = jest.fn()
    const executeMutation = jest.fn()
    executeQuery.mockReturnValue(
      fromValue<{ data: UsersQuery }>({
        data: {
          users: [user],
        },
      })
    )
    executeMutation.mockReturnValue(
      fromValue<{ data: DeleteUserMutation }>({
        data: {
          deleteUser: true
        },
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
          <UserList />
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
    expect(mockClient.executeMutation).not.toHaveBeenCalled()
  })
})
