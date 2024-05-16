// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { useMantineTheme } from '@mantine/core'
import { ApexOptions } from 'apexcharts'
import dynamic from 'next/dynamic'

const ReactApexChart = dynamic(() => import('react-apexcharts'), { ssr: false })

export interface QueryLogChartProps {
  data: {
    startTime: number
    endTime: number
  }[]
}

export const QueryLogChart = (props: QueryLogChartProps) => {
  const theme = useMantineTheme()
  const obj: any = {}
  props.data.forEach((e) => {
    obj[e.startTime] = (obj[e.startTime] ?? 0) + 1
    obj[e.endTime] = (obj[e.endTime] ?? 0) - 1
  })
  const objKeys = Object.keys(obj).map((x) =>
    typeof x === 'string' ? parseInt(x) : x
  )
  let cur = 0
  const data = objKeys.sort().map((x) => {
    cur += obj[x]
    return {
      x,
      y: cur,
    }
  })

  const state = {
    options: {
      tooltip: {
        enabled: false,
      },
      stroke: {
        curve: 'stepline',
        width: 2,
      },
      markers: {
        enabled: false,
      },
      chart: {
        zoom: {
          enabled: false,
        },
        toolbar: {
          show: false,
        },
        type: 'line',
        animations: {
          enabled: false,
        },
      },
      colors: [(theme.colors as any)?.blue?.[6]],
      xaxis: {
        type: 'datetime',
        labels: {
          datetimeUTC: false,
        },
      },
    } as ApexOptions,
  }

  return (
    <ReactApexChart
      options={state.options}
      series={[{ data }]}
      type="line"
      height={150}
    />
  )
}
