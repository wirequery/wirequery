// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { EmptyList } from '@components/shared/EmptyList'
import { IconAdjustments } from '@tabler/icons-react'
import { fireEvent, render, screen } from '@testing-library/react'

describe('EmptyList', () => {
  it('renders with title, description and button if onClick is defined', () => {
    const onClickHandler = jest.fn()
    render(
      <EmptyList
        title="Title"
        description="Description"
        icon={IconAdjustments}
        buttonText="Button"
        onClick={onClickHandler}
      />
    )
    expect(screen.getByText('Title')).not.toBeNull()
    expect(screen.getByText('Description')).not.toBeNull()

    expect(screen.getByText('Button')).not.toBeNull()

    expect(onClickHandler).toBeCalledTimes(0)

    fireEvent.click(screen.getByText('Button'))

    expect(onClickHandler).toBeCalledTimes(1)
  })

  it('renders with title and description but not button if onClick is undefined, even if button text is set', () => {
    render(
      <EmptyList
        title="Title"
        description="Description"
        icon={IconAdjustments}
        buttonText="Button"
      />
    )
    expect(screen.getByText('Title')).not.toBeNull()
    expect(screen.getByText('Description')).not.toBeNull()

    expect(screen.queryByText('Button')).toBeNull()
  })
})
