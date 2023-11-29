// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { CurrentUserSettings } from '@components/shared/app/user/CurrentUserSettings'
import { UpdateCurrentUserMutation } from '@generated/graphql'
import { showErrorAlertForMessage } from '@lib/alert'
import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { Client, Provider } from 'urql'
import { fromValue } from 'wonka'

jest.mock('@lib/alert', () => ({
  showErrorAlert: jest.fn(() => true),
  showErrorAlertForMessage: jest.fn(() => true),
  showInfoAlert: jest.fn(() => true),
}))

describe('CurrentUserSettings', () => {
  it('renders form containing the necessary fields', () => {
    const mockClient: Partial<Client> = {
      executeQuery: jest.fn(),
      executeMutation: jest.fn(),
      executeSubscription: jest.fn(),
    }
    render(
      <Provider value={mockClient as Client}>
        <CurrentUserSettings onSave={jest.fn()} />
      </Provider>
    )
    expect(screen.queryByText('New Password')).not.toBeNull()
    expect(screen.queryByText('Confirm Password')).not.toBeNull()
  })

  it('calls a mutation if Save is clicked and password not set', async () => {
    const executeQuery = jest.fn()
    const executeMutation = jest.fn()
    executeMutation.mockReturnValue(
      fromValue<{ data: UpdateCurrentUserMutation }>({
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
        <CurrentUserSettings onSave={saveFn} />
      </Provider>
    )
    fireEvent.click(screen.getByText('Save'))
    await waitFor(() => expect(mockClient.executeMutation).toHaveBeenCalled())
    await waitFor(() => expect(saveFn).toHaveBeenCalled())
  })

  it('shows a prompt that needs to be the same as the password when changing password', async () => {
    const executeQuery = jest.fn()
    const executeMutation = jest.fn()
    executeMutation.mockReturnValue(
      fromValue<{ data: UpdateCurrentUserMutation }>({
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
        <CurrentUserSettings onSave={saveFn} />
      </Provider>
    )
    const labelNewPassword = screen.getByLabelText('New Password')
    fireEvent.change(labelNewPassword, { target: { value: 'Some password' } })
    const labelConfirmPassword = screen.getByLabelText('Confirm Password')
    fireEvent.change(labelConfirmPassword, {
      target: { value: 'Some password' },
    })
    fireEvent.click(screen.getByText('Save'))
    await waitFor(() => expect(saveFn).toHaveBeenCalled())
    await waitFor(() => expect(mockClient.executeMutation).toHaveBeenCalled())
  })

  it('does not call mutation when passwords differ', async () => {
    const executeQuery = jest.fn()
    const executeMutation = jest.fn()
    const mockClient: Partial<Client> = {
      executeQuery,
      executeMutation,
      executeSubscription: jest.fn(),
    }
    const saveFn = jest.fn()
    render(
      <Provider value={mockClient as Client}>
        <CurrentUserSettings onSave={saveFn} onCancel={jest.fn()} />
      </Provider>
    )
    const labelNewPassword = screen.getByLabelText('New Password')
    fireEvent.change(labelNewPassword, { target: { value: 'Some password' } })
    const labelConfirmPassword = screen.getByLabelText('Confirm Password')
    fireEvent.change(labelConfirmPassword, {
      target: { value: 'Some other password' },
    })
    fireEvent.click(screen.getByText('Save'))
    await waitFor(() => {
      expect(mockClient.executeMutation).not.toHaveBeenCalled()
      expect(showErrorAlertForMessage).toHaveBeenCalledWith(
        'Passwords did not match. Please try again.'
      )
    })
    expect(executeMutation).not.toHaveBeenCalled()
  })
})
