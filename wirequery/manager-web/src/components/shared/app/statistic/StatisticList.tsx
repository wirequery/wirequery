// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { ListTable } from '@components/shared/ListTable'
import { Query } from '@generated/graphql'
import { useMemo } from 'react'
import { gql, useQuery } from 'urql'

export function StatisticList() {
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

  return (
    <ListTable>
      <thead>
        <tr>
          <th>Date</th>
          <th>Hour</th>
          <th>Type</th>
          <th>Metadata</th>
          <th>Count</th>
        </tr>
      </thead>
      <tbody>
        {data?.statistics?.map((row) => (
          <tr key={row.id}>
            <td>{row.moment}</td>
            <td>{row.hour}</td>
            <td>{row.type}</td>
            <td>{row.metadata}</td>
            <td>{row.amount}</td>
          </tr>
        ))}
      </tbody>
    </ListTable>
  )
}
