// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { SummaryBar } from '@components/shared/SummaryBar'
import { render, screen } from '@testing-library/react'

describe('SummaryBar', () => {
  describe('badges', () => {
    it('renders items with undefined items stripped', () => {
      render(
        <SummaryBar
          items={['item 1', undefined, 'item 2', 'item 3']}
          type="badges"
        />
      )
      expect(screen.getByText('item 1')).not.toBeNull()
      expect(screen.getByText('item 2')).not.toBeNull()
      expect(screen.getByText('item 3')).not.toBeNull()
      expect(screen.queryAllByText('·')).toHaveLength(0)
    })
  })

  describe('dot-separated', () => {
    it('renders items with undefined items stripped', () => {
      render(
        <SummaryBar
          items={['item 1', undefined, 'item 2', 'item 3']}
          type="dot-separated"
        />
      )
      expect(screen.getByText('item 1')).not.toBeNull()
      expect(screen.getByText('item 2')).not.toBeNull()
      expect(screen.getByText('item 3')).not.toBeNull()
      expect(screen.queryAllByText('·')).toHaveLength(1)
    })
  })
})
