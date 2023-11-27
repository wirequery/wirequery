// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { Item } from '@components/shared/Item'
import { fireEvent, render, screen } from '@testing-library/react'

describe('Item', () => {
  it('renders the label with an href if set', () => {
    render(<Item label="Label" href="http://test/" />)
    expect((screen.queryByText('Label') as any)['href']).toBe('http://test/')
  })

  it('renders the label with an onClick handler if set', () => {
    const onClickHandler = jest.fn()
    render(<Item label="Label" onClick={onClickHandler} />)
    expect(screen.queryByText('Label')).not.toBeNull()
    expect(onClickHandler).toHaveBeenCalledTimes(0)

    fireEvent.click(screen.getByText('Label'))
    expect(onClickHandler).toHaveBeenCalledTimes(1)
  })

  it('renders the label if neither href nor onClick are set', () => {
    render(<Item label="Label" />)
    expect(screen.queryByText('Label')).not.toBeNull()
  })

  it('renders the description if set', () => {
    render(<Item label="Label" description="Description" />)
    expect(screen.queryByText('Description')).not.toBeNull()
  })

  it('renders the items with dots in between', () => {
    render(<Item label="Label" items={['item 1', 'item 2', 'item 3']} />)
    expect(screen.queryByText('item 1')).not.toBeNull()
    expect(screen.queryByText('item 2')).not.toBeNull()
    expect(screen.queryByText('item 3')).not.toBeNull()
    expect(screen.queryAllByText('·')).toHaveLength(2)
  })
})
