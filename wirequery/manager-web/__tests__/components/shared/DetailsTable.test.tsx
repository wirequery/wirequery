// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { DetailsTable } from '@components/shared/DetailsTable'
import { render, screen } from '@testing-library/react'

describe('DetailsTable', () => {
  it('renders with children and props', () => {
    render(
      <DetailsTable data-testid="element">
        <tbody>
          <tr>
            <th>Test</th>
          </tr>
        </tbody>
      </DetailsTable>
    )
    expect(screen.getByText('Test')).not.toBeNull()
    expect(screen.getByTestId('element')).not.toBeNull()
  })
})
