// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { ErrorMessage } from '@components/shared/ErrorMessage'
import { LoadingScreen } from '@components/shared/LoadingScreen'
import { SummaryBar } from '@components/shared/SummaryBar'
import { Query } from '@generated/graphql'
import { createAuditItems } from '@lib/audit'
import { Code, Title } from '@mantine/core'
import { useMemo } from 'react'
import { gql, useQuery } from 'urql'
import { QueryLogList } from '../query-log/QueryLogList'
import { KEYWORDS } from '@lib/keywords'

export interface StoredQueryDetailsProps {
  sessionId?: string | number | null
  id: string | number
}

export function StoredQueryDetails(props: StoredQueryDetailsProps) {
  const [{ data, error }] = useQuery<Query>({
    query: gql`
      query storedQueryDetails($id: ID!) {
        storedQuery(id: $id) {
          id
          applicationId
          application {
            id
            name
          }
          name
          type
          query
          queryLimit
          endDate
          createdAt
          updatedAt
          createdBy
          updatedBy
        }
      }
    `,
    variables: { id: props.id },
    context: useMemo(
      () => ({
        additionalTypenames: ['StoredQuery'],
      }),
      []
    ),
  })

  if (error?.message) {
    return <ErrorMessage error={error} />
  }

  if (!data) {
    return <LoadingScreen />
  }

  const items = [
    data?.storedQuery?.application?.name,
    data?.storedQuery?.queryLimit && 'Max ' + data?.storedQuery?.queryLimit,
    data?.storedQuery?.type && KEYWORDS[data?.storedQuery?.type],
    data?.storedQuery?.endDate &&
      'Ends ' + new Date(data?.storedQuery?.endDate).toLocaleString(),
    ...createAuditItems(data?.storedQuery),
  ]
  return (
    <>
      <Title order={2}>{data?.storedQuery?.name}</Title>
      <SummaryBar items={items} />
      <p>
        <Code>{data?.storedQuery?.query}</Code>
      </p>

      <QueryLogList sessionId={props.sessionId} storedQueryId={'' + props.id} />
    </>
  )
}
