// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { DetailsTable } from '@components/shared/DetailsTable'
import { Query } from '@generated/graphql'
import { Flex, SimpleGrid, useMantineTheme } from '@mantine/core'
import { DateTimePicker } from '@mantine/dates'
import dynamic from 'next/dynamic'
import { useMemo, useState } from 'react'
import { gql, useQuery } from 'urql'

const ReactApexChart = dynamic(() => import('react-apexcharts'), { ssr: false })

export function StatisticList() {
  const theme = useMantineTheme()
  const [earliest, setEarliest] = useState(undefined)
  const [latest, setLatest] = useState(undefined)

  const [{ data, error, fetching }] = useQuery<Query>({
    query: gql`
      query statistics {
        statistics {
          id
          moment
          hour
          type
          metadata
          amount
        }
      }
    `,
    context: useMemo(
      () => ({
        additionalTypenames: ['Statistic'],
      }),
      []
    ),
  })

  if (fetching) {
    return <div>Loading...</div>
  }

  if (error?.message) {
    return <div>{error?.message}</div>
  }

  const getMomentAsDateTime = (s: any) =>
    new Date(s.moment + 'T' + s.hour + ':00:00')

  const statisticsInRange = (data?.statistics ?? [])
    .filter((s) => !earliest || getMomentAsDateTime(s) >= (earliest as any))
    .filter((s) => !latest || getMomentAsDateTime(s) <= (latest as any))

  const getStatistics = (type: string) =>
    statisticsInRange
      .filter((s) => s.type === type)
      .map((s) => ({ y: s.amount, x: getMomentAsDateTime(s) })) ?? []

  const uniqueLogins: any = {}
  statisticsInRange
    .filter((s) => s.type === 'LOGIN')
    .forEach((s) => {
      uniqueLogins[JSON.parse(s.metadata).username] = true
    })
  const uniqueLoginsCount = Object.keys(uniqueLogins).length

  return (
    <>
      <Flex gap={'xs'} m={'xs'}>
        <DateTimePicker
          w={200}
          dropdownType="modal"
          label="Earliest"
          clearable
          placeholder="Earliest"
          value={earliest}
          onChange={(e) => setEarliest(e as any)}
        />

        <DateTimePicker
          w={200}
          dropdownType="modal"
          label="Latest"
          clearable
          placeholder="Latest"
          value={latest}
          onChange={(e) => setLatest(e as any)}
        />
      </Flex>

      <SimpleGrid cols={2}>
        <ReactApexChart
          series={[
            { name: 'Query Log Events', data: getStatistics('QUERY_LOG') },
          ]}
          height={250}
          options={{
            title: {
              text: 'Query Log Events',
            },
            subtitle: {
              text: 'Total number of Query Log Events',
            },
            colors: [(theme.colors as any)?.purple?.[6]],
            xaxis: {
              type: 'datetime',
            },
            chart: {
              type: 'line',
              toolbar: {
                show: false,
              },
              zoom: {
                enabled: false,
              },
            },
          }}
        />

        <ReactApexChart
          series={[
            {
              name: 'Query Log Chunks',
              data: getStatistics('QUERY_LOG_CHUNKS'),
            },
          ]}
          height={250}
          options={{
            title: {
              text: 'Query Log Chunks',
            },
            subtitle: {
              text: 'Counted as chunks of 4KB.',
            },
            colors: [(theme.colors as any)?.purple?.[6]],
            xaxis: {
              type: 'datetime',
            },
            chart: {
              type: 'line',
              toolbar: {
                show: false,
              },
              zoom: {
                enabled: false,
              },
            },
          }}
        />
        <ReactApexChart
          series={[{ name: 'Logins', data: getStatistics('LOGIN') }]}
          height={250}
          options={{
            title: {
              text: 'Logins',
            },
            subtitle: {
              text: 'Total number of Login events',
            },
            colors: [(theme.colors as any)?.purple?.[6]],
            xaxis: {
              type: 'datetime',
            },
            chart: {
              type: 'line',
              toolbar: {
                show: false,
              },
              zoom: {
                enabled: false,
              },
            },
          }}
        />

        <ReactApexChart
          series={[{ name: 'Recordings', data: getStatistics('RECORDING') }]}
          height={250}
          options={{
            title: {
              text: 'Recordings',
            },
            subtitle: {
              text: 'Total number of Started Recordings',
            },
            colors: [(theme.colors as any)?.purple?.[6]],
            xaxis: {
              type: 'datetime',
            },
            chart: {
              type: 'line',
              toolbar: {
                show: false,
              },
              zoom: {
                enabled: false,
              },
            },
          }}
        />
      </SimpleGrid>

      <DetailsTable w={350} striped withBorder>
        <thead>
          <tr>
            <th></th>
            <th>Count</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>Unique logins</td>
            <td>{uniqueLoginsCount}</td>
          </tr>
        </tbody>
      </DetailsTable>
    </>
  )
}
