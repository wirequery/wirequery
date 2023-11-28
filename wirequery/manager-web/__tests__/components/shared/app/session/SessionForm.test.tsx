// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { SessionForm } from '@components/shared/app/session/SessionForm'
import { Client, Provider } from 'urql'
import { fromValue } from 'wonka'

describe('SessionForm', () => {
  it('renders the form', () => {
    const mockClient: Partial<Client> = {
      executeQuery: jest.fn(),
      executeMutation: jest.fn(),
      executeSubscription: jest.fn(),
    }
    render(
      <Provider value={mockClient as Client}>
        <SessionForm onSave={jest.fn()} onCancel={jest.fn()} />
      </Provider>
    )
    expect(screen.queryByText('Ends at')).not.toBeNull()
    expect(screen.queryByText('Template')).not.toBeNull()
  })

  it('renders form containing the fields in the template', () => {
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue({
        data: {
          templates: [
            {
              id: '1',
              name: 'Some template',
              fields: [
                {
                  type: 'TEXT',
                  key: 'textKey',
                  label: 'textLabel'
                }, {
                  type: 'TEXTAREA',
                  key: 'textAreaKey',
                  label: 'textAreaLabel'
                }, {
                  type: 'INTEGER',
                  key: 'integerKey',
                  label: 'integerLabel'
                }, {
                  type: 'FLOAT',
                  key: 'floatKey',
                  label: 'floatLabel'
                }, {
                  type: 'BOOLEAN',
                  key: 'booleanKey',
                  label: 'booleanLabel'
                }
              ],
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
        <SessionForm templateId={'1'} onSave={jest.fn()} onCancel={jest.fn()} />
      </Provider>
    )
    expect(screen.queryByText('Ends at')).not.toBeNull()

    expect(screen.queryByText('textLabel')).not.toBeNull()
    expect(screen.queryByText('textAreaLabel')).not.toBeNull()
    expect(screen.queryByText('integerLabel')).not.toBeNull()
    expect(screen.queryByText('floatLabel')).not.toBeNull()
    expect(screen.queryByText('booleanLabel')).not.toBeNull()
  })

  it('calls a mutation if Save is clicked', async () => {
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue({
        data: {
          templates: [
            {
              id: '1',
              name: 'Some template',
              fields: [],
            },
          ],
        },
      })
    )
    const executeMutation = jest.fn()
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
        <SessionForm templateId={'1'} onSave={saveFn} onCancel={jest.fn()} />
      </Provider>
    )
    fireEvent.click(screen.getByText('Save'))
    await waitFor(() => expect(mockClient.executeMutation).toBeCalled())
    await waitFor(() => expect(saveFn).toBeCalled())
  })
})
