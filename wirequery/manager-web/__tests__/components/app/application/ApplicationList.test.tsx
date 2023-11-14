// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { ApplicationList } from '@components/app/application/ApplicationList'
import { Client, Provider } from 'urql'
import { act } from 'react-dom/test-utils'
import { fromValue } from 'wonka'

describe('ApplicationList', () => {
  const application = {
    id: '1',
    name: 'Some name',
    description: 'Some description',
    inQuarantine: false,
  }

  it('forwards to application page on Show', () => {
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue({
        data: {
          applications: [application],
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
          <ApplicationList onCreateApplication={() => ({})} />
        </Provider>
      )
    })
    expect(screen.getByText(application.name)).toHaveAttribute(
      'href',
      '/applications/1'
    )
  })

  it('renders entries when data is fetched', () => {
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue({
        data: {
          applications: [application],
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
          <ApplicationList onCreateApplication={() => ({})} />
        </Provider>
      )
    })
    expect(screen.getAllByText(application.name)).not.toHaveLength(0)
    expect(screen.getAllByText(application.description)).not.toHaveLength(0)
    expect(screen.queryAllByText('In Quarantine')).toHaveLength(0)
  })

  it('renders empty screen when there are no applications', async () => {
    const onCreateApplicationMock = jest.fn()
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue({
        data: {
          applications: [],
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
          <ApplicationList onCreateApplication={onCreateApplicationMock} />
        </Provider>
      )
    })
    expect(screen.getAllByText('No applications found')).not.toHaveLength(0)
    expect(onCreateApplicationMock).not.toBeCalled()
    await waitFor(() => {
      act(() => {
        fireEvent(
          screen.getByText('Create application'),
          new MouseEvent('click', {
            bubbles: true,
            cancelable: true,
          })
        )
      })
    })
    expect(onCreateApplicationMock).toBeCalled()
  })

  it('shows in quarantine if an app is in quarantine', () => {
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue({
        data: {
          applications: [{ ...application, inQuarantine: true }],
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
          <ApplicationList onCreateApplication={() => ({})} />
        </Provider>
      )
    })
    expect(screen.getAllByText(application.name)).not.toHaveLength(0)
    expect(screen.getAllByText(application.description)).not.toHaveLength(0)
    expect(screen.getAllByText('In Quarantine')).not.toHaveLength(0)
  })

  it('calls mutation when Delete is clicked and confirmed', async () => {
    jest.spyOn(window, 'confirm').mockReturnValueOnce(true)
    const executeQuery = jest.fn()
    const executeMutation = jest.fn()
    executeQuery.mockReturnValue(
      fromValue({
        data: {
          applications: [application],
        },
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
    act(() => {
      render(
        <Provider value={mockClient as Client}>
          <ApplicationList onCreateApplication={() => ({})} />
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
    expect(mockClient.executeMutation).toBeCalled()
  })

  it('does not call mutation when Delete is clicked and not confirmed', async () => {
    jest.spyOn(window, 'confirm').mockReturnValueOnce(false)
    const executeQuery = jest.fn()
    const executeMutation = jest.fn()
    executeQuery.mockReturnValue(
      fromValue({
        data: {
          applications: [application],
        },
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
    act(() => {
      render(
        <Provider value={mockClient as Client}>
          <ApplicationList onCreateApplication={() => ({})} />
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
    expect(mockClient.executeMutation).not.toBeCalled()
  })
})
