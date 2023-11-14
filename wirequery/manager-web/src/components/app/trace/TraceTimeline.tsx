// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { useMantineTheme } from '@mantine/core'
import { ApexOptions } from 'apexcharts'
import dynamic from 'next/dynamic'
import React from 'react'

const ReactApexChart = dynamic(() => import('react-apexcharts'), { ssr: false })

export interface TraceTimelineProps {
  series: {
    appName: string
    label: string
    startTimestamp: number
    endTimestamp: number
    id: number | string
  }[]
  onSelect: (i: number) => void
}

export function TraceTimeline(props: TraceTimelineProps) {
  const theme = useMantineTheme()
  const series = [
    {
      data:
        props.series?.map((s) => ({
          x: s.appName,
          y: [s.startTimestamp, s.endTimestamp],
          label: s.label,
        })) ?? [],
    },
  ]

  const state = {
    series,
    options: {
      tooltip: {
        enabled: false,
      },
      colors: [(theme.colors as any)?.purple?.[6]],
      chart: {
        zoom: {
          enabled: false,
        },
        toolbar: {
          show: false,
        },
        selection: {
          enabled: false,
        },
        type: 'rangeBar',
        animations: {
          enabled: false,
        },
        events: {
          dataPointSelection: (_e, _chart, options) => {
            props.onSelect(options.dataPointIndex)
          },
        },
      },
      dataLabels: {
        enabled: true,
        formatter: (_val, opt) => {
          return props.series[opt.dataPointIndex]?.label
        },
      },
      plotOptions: {
        bar: {
          horizontal: true,
          distributed: true,
        },
      },
      xaxis: {
        type: 'datetime',
        labels: {
          datetimeUTC: false,
        },
      },
    } as ApexOptions,
  }

  const seriesKeys: any = {}
  props.series.forEach((s) => {
    seriesKeys[s.appName] = true
  })
  const seriesKeysLen = Object.keys(seriesKeys).length

  return (
    <div id="chart">
      <ReactApexChart
        options={state.options}
        series={state.series}
        type="rangeBar"
        height={seriesKeysLen * 50 + 50}
      />
    </div>
  )
}
