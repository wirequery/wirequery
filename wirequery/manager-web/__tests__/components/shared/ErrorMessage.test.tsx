// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { ErrorMessage } from '@components/shared/ErrorMessage'
import { render, screen } from '@testing-library/react'

describe('ErrorMessage', () => {
  it('renders graphql errors', () => {
    render(
      <ErrorMessage
        error={{ graphQLErrors: [{ message: 'Some graphql error' }] } as any}
      />
    )
    expect(screen.queryByText('Error')).not.toBeNull()
    expect(screen.queryByText('Some graphql error')).not.toBeNull()
  })

  it('renders network errors', () => {
    render(<ErrorMessage error={{ networkError: Symbol() } as any} />)
    expect(screen.queryByText('Network Issue')).not.toBeNull()
    expect(
      screen.queryByText(
        'There were network issues while loading. Please check your internet connection and try again.'
      )
    ).not.toBeNull()
  })

  it('renders unknown error', () => {
    render(<ErrorMessage error={{} as any} />)
    expect(screen.queryByText('Unknown Error')).not.toBeNull()
    expect(screen.queryByText('An unknown error occurred.')).not.toBeNull()
  })
})
