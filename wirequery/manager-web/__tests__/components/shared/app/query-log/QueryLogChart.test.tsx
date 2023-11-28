// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { QueryLogChart } from '@components/shared/app/query-log/QueryLogChart'
import { ColorSchemeProvider } from '@mantine/core'
import { render } from '@testing-library/react'

jest.mock('react-apexcharts', () => () => <div>ReactApexChart</div>)

// Most of the code in this component is visual, so we only check if no error occures when rendered.
// However, it may be nice at some point to also test that the start and end times overlap in the eventual chart.

describe('QueryLogChart', () => {
  it('calculates overlaps between different datapoints, adds them together and shows a chart', () => {
    render(
      <ColorSchemeProvider
        colorScheme={undefined as any}
        toggleColorScheme={undefined as any}
      >
        <QueryLogChart
          data={[
            {
              startTime: 1000,
              endTime: 2000
            },
            {
              startTime: 1500,
              endTime: 2500
            }
          ]} />
      </ColorSchemeProvider>
    )
  })
})
