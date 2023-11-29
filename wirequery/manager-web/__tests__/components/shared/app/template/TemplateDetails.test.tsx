// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { render, screen } from '@testing-library/react'
import { TemplateDetails } from '@components/shared/app/template/TemplateDetails'
import { Client, Provider } from 'urql'
import { act } from 'react-dom/test-utils'
import { fromValue } from 'wonka'
import { FieldType, TemplateDetailsQuery } from '@generated/graphql'

describe('TemplateDetails', () => {
  const template = {
    id: '1',
    name: 'Some name',
    description: 'Some description',
    fields: [{ key: 'someKey', label: 'Some label', type: FieldType.Text }],
    nameTemplate: 'Some nameTemplate',
    descriptionTemplate: 'Some descriptionTemplate',
    allowUserInitiation: true,
    createdAt: '1970-01-01T00:00:00Z',
    updatedAt: '1970-02-02T00:00:00Z',
  }

  it('renders details when data is fetched', () => {
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue<{ data: TemplateDetailsQuery }>({
        data: { template },
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
          <TemplateDetails id="1" />
        </Provider>
      )
    })
    expect(screen.getAllByText(template.name)).not.toHaveLength(0)
    expect(screen.getAllByText(template.description)).not.toHaveLength(0)
    expect(screen.getAllByText('Some label (someKey: TEXT)')).not.toHaveLength(
      0
    )
    expect(screen.getAllByText(template.nameTemplate)).not.toHaveLength(0)
    expect(screen.getAllByText(template.descriptionTemplate)).not.toHaveLength(
      0
    )
    expect(
      screen.getAllByText(template.allowUserInitiation ? 'Yes' : 'No')
    ).not.toHaveLength(0)
  })
})
