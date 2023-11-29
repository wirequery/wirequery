// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { TemplateQueryList } from '@components/shared/app/template-query/TemplateQueryList'
import { Client, Provider } from 'urql'
import { act } from 'react-dom/test-utils'
import { fromValue } from 'wonka'
import { DeleteTemplateMutation, TemplateQueryListQuery, TemplateQueryType } from '@generated/graphql'

describe('TemplateQueryList', () => {
  const templateQuery = {
    id: '1',
    templateId: 1,
    applicationId: 1,
    nameTemplate: 'Some nameTemplate',
    type: TemplateQueryType.Query,
    queryTemplate: 'Some queryTemplate',
    queryLimit: 10,
    template: {
      id: '1',
      name: 'Some template',
    },
    application: {
      id: '1',
      name: 'Some application',
    },
  }

  it('renders entries when data is fetched', () => {
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue<{ data: TemplateQueryListQuery }>({
        data: {
          templateQuerys: [templateQuery],
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
          <TemplateQueryList />
        </Provider>
      )
    })
    expect(screen.getAllByText(templateQuery.nameTemplate)).not.toHaveLength(0)
    expect(screen.getAllByText(templateQuery.queryTemplate)).not.toHaveLength(0)
    expect(
      screen.getAllByText('Max ' + templateQuery.queryLimit)
    ).not.toHaveLength(0)
    expect(screen.getAllByText(templateQuery.template.name)).not.toHaveLength(0)
    expect(
      screen.getAllByText(templateQuery.application.name)
    ).not.toHaveLength(0)
  })

  it('calls mutation when Delete is clicked and confirmed', async () => {
    jest.spyOn(window, 'confirm').mockReturnValueOnce(true)
    const executeQuery = jest.fn()
    const executeMutation = jest.fn()
    executeQuery.mockReturnValue(
      fromValue<{ data: TemplateQueryListQuery }>({
        data: {
          templateQuerys: [templateQuery],
        },
      })
    )
    executeMutation.mockReturnValue(
      fromValue<{ data: DeleteTemplateMutation }>({
        data: {
          deleteTemplate: true
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
          <TemplateQueryList />
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
      fromValue<{ data: TemplateQueryListQuery }>({
        data: {
          templateQuerys: [templateQuery],
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
          <TemplateQueryList />
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
