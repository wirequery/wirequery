// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { render, screen } from '@testing-library/react'
import { SessionDetails } from '@components/shared/app/session/SessionDetails'
import { Client, Provider } from 'urql'
import { act } from 'react-dom/test-utils'
import { fromValue } from 'wonka'

jest.mock('@components/shared/app/session/SessionTimeline', () => ({
  SessionTimeline: () => <></>,
}))

jest.mock('@components/shared/app/recording/RecordingPlayer', () => ({
  RecordingPlayer: () => <></>,
}))

describe('SessionDetails', () => {
  const session = {
    id: '1',
    name: 'Some name',
    description: 'Some description',
    createdAt: '1970-01-01T00:00:00Z',
    createdBy: 'wouter',
    storedQuerys: [],
  }

  let mockDate: jest.SpyInstance

  beforeAll(() => {
    mockDate = jest
      .spyOn(Date.prototype, 'toLocaleString')
      .mockReturnValue('<datetime>')
  })

  afterAll(() => {
    mockDate.mockRestore()
  })

  it('renders details when data is fetched', () => {
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue({
        data: { session },
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
          <SessionDetails id="1" />
        </Provider>
      )
    })
    expect(screen.getAllByText(session.name)).not.toHaveLength(0)
    expect(screen.getAllByText(session.description)).not.toHaveLength(0)
    expect(
      screen.getAllByText('Created <datetime> by wouter')
    ).not.toHaveLength(0)
  })
})
