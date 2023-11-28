// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { UserDetails } from '@components/ce/app/user/UserDetails'
import { UserDetailsQuery } from '@generated/graphql'
import { render, screen } from '@testing-library/react'
import { act } from 'react-dom/test-utils'
import { Client, Provider } from 'urql'
import { fromValue } from 'wonka'

describe('UserDetails', () => {
  const user = {
    id: '1',
    username: 'Some username',
    enabled: true,
    roles: 'Some roles',
    createdAt: '1970-01-01T00:00:00Z',
    updatedAt: '1970-02-02T00:00:00Z',
  }

  it('renders details when data is fetched', () => {
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue<{ data: UserDetailsQuery }>({
        data: { user },
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
          <UserDetails id="1" />
        </Provider>
      )
    })
    expect(screen.getAllByText(user.username)).not.toHaveLength(0)
    expect(
      screen.getAllByText(user.enabled ? 'Enabled' : 'Disabled')
    ).not.toHaveLength(0)
    expect(screen.getAllByText(user.roles)).not.toHaveLength(0)
  })
})
