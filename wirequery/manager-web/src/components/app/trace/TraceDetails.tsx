import { ErrorMessage } from '@components/shared/ErrorMessage'
import { LoadingScreen } from '@components/shared/LoadingScreen'
import { SummaryBar } from '@components/shared/SummaryBar'
import { Query } from '@generated/graphql'
import { Modal, Title, UnstyledButton } from '@mantine/core'
import { IconCopy } from '@tabler/icons-react'
import React, { useMemo, useState } from 'react'
import { gql, useQuery } from 'urql'
import { LogTree } from '../../shared/LogTree'
import { TraceTimeline } from './TraceTimeline'

export interface TraceDetailsProps {
  storedQueryId: string
  traceId: string
}

export const TraceDetails = (props: TraceDetailsProps) => {
  const [selectedIndex, setSelectedId] = useState<number | undefined>(undefined)
  const [{ data, error }] = useQuery<Query>({
    query: gql`
      query queryLogByTrace($filter: TraceFilterInput!, $id: ID!) {
        storedQuery(id: $id) {
          id
          name
        }
        queryLogByTrace(filter: $filter) {
          storedQueryId
          message
          appName
          traceId
          startTime
          endTime
        }
      }
    `,
    variables: {
      filter: {
        traceId: props.traceId,
        storedQueryId:
          props.storedQueryId !== undefined
            ? parseInt(props.storedQueryId)
            : undefined,
      },
      id: props.storedQueryId,
    },
    context: useMemo(
      () => ({
        additionalTypenames: ['QueryLog'],
      }),
      []
    ),
  })

  const traceQueryLogs = [...(data?.queryLogByTrace ?? [])].sort(
    (x, y) => x.startTime - y.startTime
  )

  const jsonByIndex = useMemo(() => {
    const result: any = {}
    traceQueryLogs.forEach((queryLog, i) => {
      result[i] = JSON.parse(queryLog.message)
    })
    return result
  }, [data])

  if (error?.message) {
    return <ErrorMessage error={error} />
  }

  if (!data) {
    return <LoadingScreen />
  }

  if (traceQueryLogs.length === 0) {
    return (
      <>
        No trace logs found. Did you use <code>@trace</code>?
      </>
    )
  }

  const start = traceQueryLogs?.[0]?.startTime ?? 0

  return (
    <>
      <Title order={2}>Trace of {data?.storedQuery?.name}</Title>
      <SummaryBar items={[props.traceId]} />
      <TraceTimeline
        series={traceQueryLogs.map((queryLog, i) => ({
          appName: queryLog.appName,
          label:
            jsonByIndex[i]?.error ??
            jsonByIndex[i]?.result?.method +
              ' ' +
              jsonByIndex[i]?.result?.path +
              ' ' +
              jsonByIndex[i]?.result?.statusCode,
          startTimestamp: queryLog.startTime - start,
          endTimestamp: queryLog.endTime - start,
          id: i,
        }))}
        onSelect={(i) => setSelectedId(i)}
      />

      <Modal
        title="Selected Query Log"
        opened={selectedIndex !== undefined}
        onClose={() => setSelectedId(undefined)}
        size="xl"
      >
        {selectedIndex !== undefined && (
          <>
            <div style={{ float: 'right' }}>
              <UnstyledButton
                onClick={() =>
                  navigator.clipboard.writeText(
                    JSON.stringify(jsonByIndex[selectedIndex]?.result)
                  )
                }
              >
                <IconCopy size={16} />
              </UnstyledButton>
            </div>
            <LogTree
              display={jsonByIndex[selectedIndex]?.result}
              startTime={traceQueryLogs[selectedIndex]?.startTime}
              endTime={traceQueryLogs[selectedIndex]?.endTime}
              traceId={traceQueryLogs[selectedIndex]?.traceId}
            />
          </>
        )}
      </Modal>
    </>
  )
}
