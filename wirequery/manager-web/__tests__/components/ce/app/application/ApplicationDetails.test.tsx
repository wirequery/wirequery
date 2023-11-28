// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { fireEvent, render, screen } from '@testing-library/react'
import { ApplicationDetails } from '@components/ce/app/application/ApplicationDetails'
import { Client, Provider } from 'urql'
import { act } from 'react-dom/test-utils'
import { fromValue } from 'wonka'
import { ApplicationDetailsQuery } from '@generated/graphql'

describe('ApplicationDetails', () => {
  const application = {
    id: '1',
    name: 'Some name',
    inQuarantine: false,
    quarantineReason: 'some quarantine reason',
    quarantineRule: 'some quarantine rule',
    description: 'Some description',
    createdAt: '1970-01-01T00:00:00Z',
    updatedAt: '1970-02-02T00:00:00Z',
    createdBy: 'wouter',
    updatedBy: 'wouter',
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
      fromValue<{ data: ApplicationDetailsQuery }>({ data: { application } })
    )

    const mockClient: Partial<Client> = {
      executeQuery,
      executeMutation: jest.fn(),
      executeSubscription: jest.fn(),
    }
    act(() => {
      render(
        <Provider value={mockClient as Client}>
          <ApplicationDetails id="1" />
        </Provider>
      )
    })
    expect(screen.getAllByText(application.name)).not.toHaveLength(0)
    expect(screen.getAllByText(application.description)).not.toHaveLength(0)
    expect(screen.queryAllByText('In Quarantine')).toHaveLength(0)
    expect(screen.queryAllByText(application.quarantineReason)).toHaveLength(0)
    expect(screen.queryAllByText(application.quarantineRule)).toHaveLength(0)
    expect(
      screen.getAllByText('Created <datetime> by wouter')
    ).not.toHaveLength(0)
    expect(
      screen.getAllByText('Updated <datetime> by wouter')
    ).not.toHaveLength(0)
  })

  it('when an app is in quarantine, the reason is shown', () => {
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue<{ data: ApplicationDetailsQuery }>({
        data: { application: { ...application, inQuarantine: true } },
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
          <ApplicationDetails id="1" />
        </Provider>
      )
    })
    expect(screen.getAllByText('In Quarantine')).not.toHaveLength(0)
    expect(screen.getAllByText('Unquarantine')).not.toHaveLength(0)
    expect(screen.getAllByText(application.quarantineReason)).not.toHaveLength(
      0
    )
    expect(screen.getAllByText(application.quarantineRule)).not.toHaveLength(0)
  })

  it('when an app is in quarantine, it can be unquarantined', () => {
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue<{ data: ApplicationDetailsQuery }>({
        data: { application: { ...application, inQuarantine: true } },
      })
    )
    const executeMutation = jest.fn().mockReturnValue(
      fromValue<{ data: ApplicationDetailsQuery }>({
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
          <ApplicationDetails id="1" />
        </Provider>
      )
    })
    window.prompt = jest.fn(() => 'some reason')
    expect(mockClient.executeMutation).not.toBeCalled()
    expect(screen.getAllByText('Unquarantine')).not.toHaveLength(0)
    fireEvent.click(screen.getByText('Unquarantine'))
    expect(mockClient.executeMutation).toBeCalled()
  })

  it('when an app is in quarantine, it is not quarantined if the prompt is not filled', () => {
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue<{ data: ApplicationDetailsQuery }>({
        data: { application: { ...application, inQuarantine: true } },
      })
    )
    const executeMutation = jest.fn().mockReturnValue(
      fromValue<{ data: ApplicationDetailsQuery }>({
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
          <ApplicationDetails id="1" />
        </Provider>
      )
    })
    window.prompt = jest.fn(() => undefined)
    expect(mockClient.executeMutation).not.toBeCalled()
    expect(screen.getAllByText('Unquarantine')).not.toHaveLength(0)
    fireEvent.click(screen.getByText('Unquarantine'))
    expect(mockClient.executeMutation).not.toBeCalled()
  })
})
