// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { TraceTimeline } from '@components/shared/app/trace/TraceTimeline'
import { ColorSchemeProvider } from '@mantine/core'
import { render } from '@testing-library/react'

jest.mock('react-apexcharts', () => () => <div>ReactApexChart</div>)

// Most of the code in this component is visual, so we only check if no error occures when rendered.
describe('TraceTimeline', () => {
  it('renders the mocked chart without throwing errors', () => {
    render(
      <ColorSchemeProvider
        colorScheme={undefined as any}
        toggleColorScheme={undefined as any}
      >
        <TraceTimeline
          onSelect={() => { }}
          series={[
            {
              appName: '',
              label: '',
              startTimestamp: 0,
              endTimestamp: 1000,
              id: 1
            }
          ]} />
      </ColorSchemeProvider>
    )
  })
})
