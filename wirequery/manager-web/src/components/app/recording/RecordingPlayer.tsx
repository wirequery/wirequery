import { Query } from '@generated/graphql'
import { Badge, Card, Code, Flex, Grid, ScrollArea } from '@mantine/core'
import { useEffect, useMemo, useRef, useState } from 'react'
import { LogData } from 'rrweb'
import rrwebPlayer from 'rrweb-player'
import 'rrweb-player/dist/style.css'
import { gql, useQuery } from 'urql'

export interface RecordingPlayerProps {
  sessionId?: string
  onUpdateCurrentTime: (value: number) => void
  onMetadataAvailable: (value: {
    start: number
    end: number
    player: rrwebPlayer
  }) => void
}

export function RecordingPlayer(props: RecordingPlayerProps) {
  const [logs, setLogs] = useState<{ time: number; value: LogData }[]>([])
  const [time, setTime] = useState(0)
  const wrapperEl = useRef(null)

  const [toggle, setToggle] = useState(false)

  const [{ data, error, fetching }] = useQuery<Query>({
    query: gql`
      query recordingPlayer($filter: RecordingFilter) {
        recordings(filter: $filter) {
          id
          sessionId
          recording
        }
      }
    `,
    variables: {
      filter: {
        sessionId:
          props.sessionId !== undefined ? parseInt(props.sessionId) : undefined,
      },
    },
    context: useMemo(
      () => ({
        additionalTypenames: ['Recording'],
      }),
      []
    ),
  })

  useEffect(() => {
    if (data && !toggle && data?.recordings?.[0]) {
      let videoEvents: any
      try {
        videoEvents = JSON.parse(data?.recordings?.[0]?.recording)
      } catch {
        return
      }
      setLogs(
        videoEvents
          .filter((v: any) => v.data.plugin === 'rrweb/console@1')
          .map((v: any) => ({ time: v.timestamp, value: v.data.payload }))
      )
      if (videoEvents && videoEvents.length && wrapperEl.current) {
        const player: rrwebPlayer = new rrwebPlayer({
          target: wrapperEl.current as any,
          props: {
            autoPlay: false,
            width: 512,
            height: 256,
            events: videoEvents.map((event: any) => ({
              ...event,
              data: event.data,
              timestamp: new Date(event.timestamp).getTime(),
            })),
          },
        })
        props.onMetadataAvailable({
          start: player.getMetaData().startTime,
          end: player.getMetaData().endTime,
          player,
        })
        player.addEventListener('ui-update-current-time', (e) => {
          const time = e.payload
          setTime(time + player.getMetaData().startTime)
          props.onUpdateCurrentTime(time + player.getMetaData().startTime)
        })
        setToggle(true)
      }
    }
  }, [data, toggle])

  if (fetching) {
    return <div>Loading...</div>
  }

  if (error?.message) {
    return <div>{error?.message}</div>
  }

  if (!data?.recordings?.[0] || data?.recordings?.[0].recording === '') {
    return <></>
  }

  return (
    <Grid className="rr-player-wq">
      <Grid.Col span={12} lg={6}>
        <Card withBorder style={{ height: '100%' }}>
          <Flex justify="center">
            <div ref={wrapperEl}></div>
          </Flex>
        </Card>
      </Grid.Col>
      <Grid.Col span={12} lg={6}>
        <Card
          padding="lg"
          withBorder
          style={{ height: '100%', fontFamily: 'Courier New' }}
        >
          <ScrollArea h={256 + 80 - 2 * 10}>
            {logs
              .filter((l) => l.time <= time)
              .map((log, index) =>
                log.value.level === 'error' ? (
                  <Code color="red" block key={index}>
                    <Badge mr={'xs'}>
                      {new Date(log.time).toLocaleTimeString()}
                    </Badge>
                    {log.value.payload}
                  </Code>
                ) : (
                  <Code block key={index}>
                    <Badge mr={'xs'}>
                      {new Date(log.time).toLocaleTimeString()}
                    </Badge>
                    {log.value.payload}
                  </Code>
                )
              )}
          </ScrollArea>
        </Card>
      </Grid.Col>
    </Grid>
  )
}
