// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { LogTreeList } from '@components/shared/LogTreeList'
import { EmptyList } from '@components/shared/EmptyList'
import { ErrorMessage } from '@components/shared/ErrorMessage'
import { LoadingScreen } from '@components/shared/LoadingScreen'
import { Query } from '@generated/graphql'
import { ActionIcon, Divider, Group } from '@mantine/core'
import { IconCopy, IconListSearch, IconRefresh } from '@tabler/icons-react'
import { useMemo } from 'react'
import { gql, useQuery } from 'urql'
import { QueryLogChart } from './QueryLogChart'

export interface QueryLogListProps {
  sessionId?: string | number | null
  storedQueryId: string
  extendedTracing: boolean
}

export function QueryLogList(props: QueryLogListProps) {
  const [{ data, error }, reexecuteQuery] = useQuery<Query>({
    query: gql`
      query queryLogList($filter: QueryLogFilter!) {
        queryLogs(filter: $filter) {
          storedQueryId
          message
          startTime
          endTime
          traceId
        }
      }
    `,
    variables: {
      filter: {
        storedQueryId:
          props.storedQueryId !== undefined
            ? parseInt(props.storedQueryId)
            : undefined,
      },
    },
    context: useMemo(
      () => ({
        additionalTypenames: ['QueryLog'],
      }),
      []
    ),
  })

  if (!data) {
    return <LoadingScreen />
  }

  if (error?.message) {
    return <ErrorMessage error={error} />
  }

  const actionBar = (
    <>
      <QueryLogChart
        data={
          data?.queryLogs?.map((q) => ({
            startTime: q.startTime,
            endTime: q.endTime,
          })) ?? []
        }
      />
      <Divider pb={'lg'} />
      <Group spacing="xs" my={5}>
        <ActionIcon variant="default" onClick={reexecuteQuery}>
          <IconRefresh size="1rem" />
        </ActionIcon>
        <ActionIcon
          variant="default"
          onClick={() => {
            // TODO untested
            const json = data?.queryLogs?.map((row) => {
              const message = JSON.parse(row?.message)
              return message?.result ?? message?.error
            })
            navigator.clipboard.writeText(JSON.stringify(json))
          }}
        >
          <IconCopy size={16} />
        </ActionIcon>
      </Group>
    </>
  )

  if (data?.queryLogs?.length === 0) {
    return (
      <>
        {actionBar}
        <EmptyList
          icon={IconListSearch}
          title="No results yet"
          description="Click the refresh button to update the list."
        />
      </>
    )
  }

  return (
    <>
      {actionBar}
      <LogTreeList
        sessionId={props.sessionId}
        storedQueryId={props.storedQueryId}
        rows={data?.queryLogs}
        extendedTracing={props.extendedTracing}
      />
    </>
  )
}
