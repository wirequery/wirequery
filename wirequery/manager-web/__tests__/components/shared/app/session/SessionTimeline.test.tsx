// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { SessionTimeline } from '@components/shared/app/session/SessionTimeline'
import { ColorSchemeProvider } from '@mantine/core'
import { render } from '@testing-library/react'

jest.mock('react-apexcharts', () => () => <div>ReactApexChart</div>)

// Most of the code in this component is visual, so we only check if no error occures when rendered.
describe('SessionTimeline', () => {
  it('renders the mocked chart without throwing errors', () => {
    render(
      <ColorSchemeProvider
        colorScheme={undefined as any}
        toggleColorScheme={undefined as any}
      >
        <SessionTimeline currentTime={0} data={[]} onEventClick={jest.fn()} />
      </ColorSchemeProvider>
    )
  })
})
