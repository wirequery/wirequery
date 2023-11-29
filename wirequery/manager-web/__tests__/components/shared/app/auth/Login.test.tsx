// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { Login } from '@components/shared/app/auth/Login'
import { Client, Provider } from 'urql'
import { fromValue } from 'wonka'
import { LoginMutation } from '@generated/graphql'

describe('Login', () => {
  it('renders form containing the necessary fields', () => {
    const mockClient: Partial<Client> = {
      executeQuery: jest.fn(),
      executeMutation: jest.fn(),
      executeSubscription: jest.fn(),
    }
    render(
      <Provider value={mockClient as Client}>
        <Login onLogin={jest.fn()} />
      </Provider>
    )
    expect(screen.queryByText('Username')).not.toBeNull()
    expect(screen.queryByText('Password')).not.toBeNull()
    expect(screen.queryByText('Sign in')).not.toBeNull()
  })

  it('calls a mutation if sign in is clicked', async () => {
    const executeMutation = jest.fn()
    executeMutation.mockReturnValue(
      fromValue<{ data: LoginMutation }>({
        data: {
          login: {
            id: '1',
            username: 'wnederhof'
          }
        },
      })
    )
    const mockClient: Partial<Client> = {
      executeQuery: jest.fn(),
      executeMutation,
      executeSubscription: jest.fn(),
    }
    const loginFn = jest.fn()
    render(
      <Provider value={mockClient as Client}>
        <Login onLogin={loginFn} />
      </Provider>
    )
    fireEvent.click(screen.getByText('Sign in'))
    await waitFor(() => expect(mockClient.executeMutation).toHaveBeenCalled())
    await waitFor(() => expect(loginFn).toHaveBeenCalled())
  })
})
