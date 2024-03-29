// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { TemplateQueryForm } from '@components/shared/app/template-query/TemplateQueryForm'
import { Client, Provider } from 'urql'
import { fromValue } from 'wonka'
import { UpdateTemplateMutation } from '@generated/graphql'

describe('TemplateQueryForm', () => {
  it('renders form containing the necessary fields', () => {
    const mockClient: Partial<Client> = {
      executeQuery: jest.fn(),
      executeMutation: jest.fn(),
      executeSubscription: jest.fn(),
    }
    render(
      <Provider value={mockClient as Client}>
        <TemplateQueryForm
          templateId={1}
          onSave={jest.fn()}
          onCancel={jest.fn()}
        />
      </Provider>
    )
    expect(screen.queryByText('Name Template')).not.toBeNull()
    expect(screen.queryByText('Query Template')).not.toBeNull()
    expect(screen.queryByText('Query Limit')).not.toBeNull()
  })

  it('calls a mutation if Save is clicked if there is an id', async () => {
    const executeQuery = jest.fn()
    const executeMutation = jest.fn()
    executeMutation.mockReturnValue(
      fromValue<{ data: UpdateTemplateMutation }>({
        data: {
          updateTemplate: {
            id: '1',
          },
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
        <TemplateQueryForm
          templateId="1"
          onSave={saveFn}
          onCancel={jest.fn()}
        />
      </Provider>
    )
    fireEvent.click(screen.getByText('Save'))
    await waitFor(() => expect(mockClient.executeMutation).toHaveBeenCalled())
    await waitFor(() => expect(saveFn).toHaveBeenCalled())
  })
})
