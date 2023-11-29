// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { RoleList } from '@components/shared/app/role/RoleList'
import { Client, Provider } from 'urql'
import { act } from 'react-dom/test-utils'
import { fromValue } from 'wonka'
import { DeleteRoleMutation, RoleListQuery } from '@generated/graphql'

describe('RoleList', () => {
  const role = {
    id: '1',
    name: 'Some name',
    authorisations: [{ name: 'SOME_AUTHORISATION', label: 'Some label', description: 'Some description' }],
    createdAt: '1970-01-01T00:00:00Z'
  }

  it('renders entries when data is fetched', () => {
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue<{ data: RoleListQuery }>({
        data: {
          roles: [role],
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
          <RoleList />
        </Provider>
      )
    })
    expect(screen.getAllByText(role.name)).not.toHaveLength(0)
    expect(screen.getAllByText(role.authorisations[0].label)).not.toHaveLength(
      0
    )
  })

  it('calls mutation when Delete is clicked and confirmed', async () => {
    jest.spyOn(window, 'confirm').mockReturnValueOnce(true)
    const executeQuery = jest.fn()
    const executeMutation = jest.fn()
    executeQuery.mockReturnValue(
      fromValue<{ data: RoleListQuery }>({
        data: {
          roles: [role],
        },
      })
    )
    executeMutation.mockReturnValue(
      fromValue<{ data: DeleteRoleMutation }>({
        data: {
          deleteRole: true
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
          <RoleList />
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
      fromValue<{ data: RoleListQuery }>({
        data: {
          roles: [role],
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
          <RoleList />
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
