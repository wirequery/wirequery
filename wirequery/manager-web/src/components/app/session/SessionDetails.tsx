import { ErrorMessage } from '@components/shared/ErrorMessage'
import { LoadingScreen } from '@components/shared/LoadingScreen'
import { SummaryBar } from '@components/shared/SummaryBar'
import { Query } from '@generated/graphql'
import { createAuditItems } from '@lib/audit'
import { Drawer, Title } from '@mantine/core'
import { useMemo, useState } from 'react'
import { gql, useQuery } from 'urql'
import { RecordingPlayer } from '../recording/RecordingPlayer'
import { TraceDetails } from '../trace/TraceDetails'
import { SessionTimeline } from './SessionTimeline'

export interface SessionDetailsProps {
  id: string | number
}

export function SessionDetails(props: SessionDetailsProps) {
  const [selectedItem, setSelectedItem] = useState<any>(undefined)
  const [currentTime, setCurrentTime] = useState(0)
  const [metadata, setMetadata] = useState<any>(undefined)
  const [{ data, error }] = useQuery<Query>({
    query: gql`
      query sessionDetails($id: ID!) {
        session(id: $id) {
          id
          name
          description
          createdAt
          updatedAt
          createdBy
          updatedBy
          storedQuerys {
            id
            name
            queryLogs {
              startTime
              endTime
              traceId
            }
          }
        }
      }
    `,
    variables: { id: props.id },
    context: useMemo(
      () => ({
        additionalTypenames: ['Session'],
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

  const queryLogEvents =
    data?.session?.storedQuerys?.map((sq) => ({
      name: sq.name,
      events: sq.queryLogs.map((ql) => ({
        start: ql.startTime,
        end: ql.endTime,
        onClick: () => {
          if (ql.traceId) {
            setSelectedItem({ storedQueryId: sq.id, traceId: ql.traceId })
          }
        },
      })),
    })) ?? []

  const sessionData = [
    ...(metadata
      ? [
          {
            name: 'Frontend Recording',
            events: [
              {
                start: metadata.start,
                end: metadata.end,
                onClick: (time: number) => {
                  metadata.player?.goto(time - metadata.start)
                },
              },
            ],
          },
        ]
      : []),
    ...queryLogEvents,
  ]

  return (
    <>
      <Title order={2}>{data?.session?.name}</Title>

      <SummaryBar items={createAuditItems(data?.session)} />

      <p>{data?.session?.description}</p>

      <div>
        {props.id && (
          <RecordingPlayer
            sessionId={props.id as string}
            onUpdateCurrentTime={setCurrentTime}
            onMetadataAvailable={setMetadata}
          />
        )}
      </div>
      {data?.session?.storedQuerys && (
        <SessionTimeline
          currentTime={currentTime}
          data={sessionData}
          onEventClick={(x) => console.log(x)}
        />
      )}

      <Drawer
        opened={selectedItem !== undefined}
        onClose={() => setSelectedItem(undefined)}
        size="xl"
        position="right"
      >
        {selectedItem?.storedQueryId && (
          <TraceDetails
            storedQueryId={selectedItem.storedQueryId as string}
            traceId={selectedItem.traceId as string}
          />
        )}
      </Drawer>
    </>
  )
}
