// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { StatisticList } from '@components/shared/app/statistic/StatisticList'
import { render } from '@testing-library/react'
import { act } from 'react-dom/test-utils'
import { Client, Provider } from 'urql'
import { fromValue } from 'wonka'

jest.mock('react-apexcharts', () => () => <div>ReactApexChart</div>)

describe('StatisticList', () => {
  const statistic = {
    id: '1',
    moment: '2000-01-01',
    hour: 10,
    type: 'Some type',
    metadata: 'Some metadata',
    amount: 10,
  }

  // Most of the code in this component is visual, so we only check if no error occures when rendered.

  it('renders', () => {
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue({
        data: {
          statistics: [statistic],
        },
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
          <StatisticList />
        </Provider>
      )
    })
  })
})
