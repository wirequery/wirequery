// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { TemplateList } from '@components/shared/app/template/TemplateList'
import { Client, Provider } from 'urql'
import { act } from 'react-dom/test-utils'
import { fromValue } from 'wonka'
import { DeleteTemplateMutation, TemplateListQuery } from '@generated/graphql'

describe('TemplateList', () => {
  const template = {
    id: '1',
    name: 'Some name',
    description: 'Some description',
    fields: [],
    nameTemplate: 'Some nameTemplate',
    descriptionTemplate: 'Some descriptionTemplate',
    allowUserInitiation: true,
    createdAt: '1970-01-01T00:00:00Z',
  }

  it('forwards to template page on Show', () => {
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue<{ data: TemplateListQuery }>({
        data: {
          templates: [template],
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
          <TemplateList />
        </Provider>
      )
    })
    expect(screen.getByText(template.name)).toHaveAttribute(
      'href',
      '/templates/1'
    )
  })

  it('renders entries when data is fetched', () => {
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue<{ data: TemplateListQuery }>({
        data: {
          templates: [template],
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
          <TemplateList />
        </Provider>
      )
    })
    expect(screen.getAllByText(template.name)).not.toHaveLength(0)
    expect(screen.getAllByText(template.description)).not.toHaveLength(0)
  })

  it('calls mutation when Delete is clicked and confirmed', async () => {
    jest.spyOn(window, 'confirm').mockReturnValueOnce(true)
    const executeQuery = jest.fn()
    const executeMutation = jest.fn()
    executeQuery.mockReturnValue(
      fromValue<{ data: TemplateListQuery }>({
        data: {
          templates: [template],
        },
      })
    )
    executeMutation.mockReturnValue(
      fromValue<{ data: DeleteTemplateMutation }>({
        data: {
          deleteTemplate: true,
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
          <TemplateList />
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
      fromValue<{ data: TemplateListQuery }>({
        data: {
          templates: [template],
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
          <TemplateList />
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
