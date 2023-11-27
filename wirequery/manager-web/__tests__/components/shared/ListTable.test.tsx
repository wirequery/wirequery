// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { ListTable } from '@components/shared/ListTable'
import { render, screen } from '@testing-library/react'

describe('ListTable', () => {
  it('renders with children and props', () => {
    render(
      <ListTable data-testid="element">
        <tbody>
          <tr>
            <th>Test</th>
          </tr>
        </tbody>
      </ListTable>
    )
    expect(screen.getByText('Test')).not.toBeNull()
    expect(screen.getByTestId('element')).not.toBeNull()
  })
})
