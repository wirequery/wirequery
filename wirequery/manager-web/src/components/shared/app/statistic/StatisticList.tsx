// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { DetailsTable } from '@components/shared/DetailsTable'
import { Query } from '@generated/graphql'
import { incrementHourValues, zeroesForEachHour } from '@lib/chart-helpers'
import {
  startOfMonth,
  startOfNextMonth,
  startOfLastMonth,
  toDate,
  toDay,
} from '@lib/date-helpers'
import { Button, Flex, SimpleGrid, Title, useMantineTheme } from '@mantine/core'
import { DatePickerInput } from '@mantine/dates'
import dynamic from 'next/dynamic'
import { useMemo, useState } from 'react'
import { gql, useQuery } from 'urql'

const ReactApexChart = dynamic(() => import('react-apexcharts'), { ssr: false })

export function StatisticList() {
  const theme = useMantineTheme()

  const defaultEarliest = useMemo(
    () =>
      toDate({
        day: startOfMonth(toDay(new Date())),
        hour: 0,
        value: 0,
      }),
    []
  )

  const defaultLatest = useMemo(
    () =>
      toDate({
        day: startOfNextMonth(toDay(new Date())),
        hour: 0,
        value: 0,
      }),
    []
  )

  const [earliest, setEarliest] = useState(defaultEarliest)
  const [latest, setLatest] = useState(defaultLatest)

  const setLastMonth = () => {
    setEarliest(
      toDate({
        day: startOfLastMonth(toDay(new Date())),
        hour: 0,
        value: 0,
      })
    )
    setLatest(
      toDate({
        day: startOfMonth(toDay(new Date())),
        hour: 0,
        value: 0,
      })
    )
  }

  const setThisMonth = () => {
    setEarliest(
      toDate({
        day: startOfMonth(toDay(new Date())),
        hour: 0,
        value: 0,
      })
    )
    setLatest(
      toDate({
        day: startOfNextMonth(toDay(new Date())),
        hour: 0,
        value: 0,
      })
    )
  }

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

  const getMomentAsDateTime = (s: any) => {
    if (s.hour < 10) {
      return new Date(s.moment + 'T0' + s.hour + ':00:00')
    }
    return new Date(s.moment + 'T' + s.hour + ':00:00')
  }

  const statisticsInRange = (data?.statistics ?? [])
    .filter((s) => !earliest || getMomentAsDateTime(s) >= (earliest as any))
    .filter((s) => !latest || getMomentAsDateTime(s) < (latest as any))

  const getStatistics = (type: string) => {
    const statsInHourValueFormat = (data?.statistics ?? [])
      .filter((s) => s.type === type)
      .map((s) => {
        return {
          day: toDay(new Date(s.moment)),
          hour: s.hour,
          value: s.amount,
        }
      })

    return incrementHourValues(
      zeroesForEachHour(toDay(earliest), toDay(latest)),
      statsInHourValueFormat
    ).map((hourValue) => ({
      x: toDate(hourValue),
      y: hourValue.value,
    }))
  }

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
        <Button onClick={() => setLastMonth()}>Last Month</Button>
        <Button onClick={() => setThisMonth()}>This Month</Button>
      </Flex>

      <Flex gap={'xs'} m={'xs'}>
        <DatePickerInput
          w={200}
          dropdownType="modal"
          label="From"
          placeholder="Earliest"
          value={earliest}
          onChange={(e) => setEarliest(e as any)}
        />

        <DatePickerInput
          w={200}
          dropdownType="modal"
          label="To, exclusive"
          placeholder="Latest"
          value={latest}
          onChange={(e) => setLatest(e as any)}
        />
      </Flex>

      <Title my="xl" m="xs" order={2}>
        Instant Queries
      </Title>

      <SimpleGrid cols={2}>
        <ReactApexChart
          series={[
            { name: 'Instant Queries', data: getStatistics('INSTANT_QUERY') },
          ]}
          height={250}
          options={{
            title: {
              text: 'Instant Queries',
            },
            subtitle: {
              text: 'Total number of Instant Queries',
            },
            colors: [(theme.colors as any)?.purple?.[6]],
            xaxis: {
              type: 'datetime',
            },
            chart: {
              type: 'scatter',
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
              name: 'Instant Query Log Events',
              data: getStatistics('INSTANT_QUERY_LOG'),
            },
          ]}
          height={250}
          options={{
            title: {
              text: 'Instant Query Log Events',
            },
            subtitle: {
              text: 'Total number of Instant Query Log Events',
            },
            colors: [(theme.colors as any)?.purple?.[6]],
            xaxis: {
              type: 'datetime',
            },
            chart: {
              type: 'scatter',
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
              name: 'Instant Query Log Chunks',
              data: getStatistics('INSTANT_QUERY_LOG_CHUNKS'),
            },
          ]}
          height={250}
          options={{
            title: {
              text: 'Instant Query Log Chunks',
            },
            subtitle: {
              text: 'Counted as chunks of 4KB.',
            },
            colors: [(theme.colors as any)?.purple?.[6]],
            xaxis: {
              type: 'datetime',
            },
            chart: {
              type: 'scatter',
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

      <Title my="xl" m="xs" order={2}>
        Query Logs
      </Title>

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
              type: 'scatter',
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
              type: 'scatter',
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

      <Title my="xl" m="xs" order={2}>
        Recordings
      </Title>

      <SimpleGrid cols={2}>
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
              type: 'scatter',
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

      <Title my="xl" m="xs" order={2}>
        Counters
      </Title>

      <DetailsTable m="xs" w={350} striped withBorder>
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
