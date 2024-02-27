// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { LoadingScreen } from '@components/shared/LoadingScreen'
import { Query } from '@generated/graphql'
import { Card, Flex, Grid, ScrollArea, Tabs } from '@mantine/core'
import { useEffect, useMemo, useRef, useState } from 'react'
import { LogData } from 'rrweb'
import rrwebPlayer from 'rrweb-player'
import 'rrweb-player/dist/style.css'
import { gql, useQuery } from 'urql'
import { Console } from './Console'
import { NetworkCall, NetworkCalls } from './NetworkCalls'

export interface RecordingPlayerProps {
  sessionId?: string
  onSelectNetworkCorrelation(correlationId: string): void
  onUpdateCurrentTime: (value: number) => void
  onMetadataAvailable: (value: {
    start: number
    end: number
    player: rrwebPlayer
  }) => void
  correlationMap: any
}

export function RecordingPlayer(props: RecordingPlayerProps) {
  const [logs, setLogs] = useState<{ time: number; value: LogData }[]>([])
  const [networkCalls, setNetworkCalls] = useState<NetworkCall[]>([])
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

      setNetworkCalls(
        videoEvents
          .filter((v: any) => v.data.plugin === 'rrweb/network@1')
          .map((v: any) => ({
            method: v.data.payload.requests[0].method,
            statusCode: v.data.payload.requests[0].status,
            url: v.data.payload.requests[0].url,
            correlationId:
              v.data.payload.requests[0].requestHeaders?.[
                'wirequery-request-correlation-id'
              ],
            events: [
              {
                start: v.timestamp,
                end:
                  v.timestamp -
                  v.data.payload.requests[0].startTime +
                  v.data.payload.requests[0].endTime,
              },
            ],
          }))
      )

      if (videoEvents && videoEvents.length && wrapperEl.current) {
        const player: rrwebPlayer = new rrwebPlayer({
          target: wrapperEl.current as any,
          props: {
            autoPlay: false,
            width: 500,
            height: 300,
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
    return (
      <div>
        <LoadingScreen />
      </div>
    )
  }

  if (error?.message) {
    return <div>{error?.message}</div>
  }

  if (!data?.recordings?.[0] || data?.recordings?.[0].recording === '') {
    return <></>
  }

  const scrollAreaHeight = 300

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
        <Card padding="lg" withBorder style={{ height: '100%' }}>
          <Tabs defaultValue="network">
            <Tabs.List>
              <Tabs.Tab value="network">Network</Tabs.Tab>
              <Tabs.Tab value="console">Console</Tabs.Tab>
              <Tabs.Tab value="errors">Errors</Tabs.Tab>
            </Tabs.List>
            <Tabs.Panel value="network">
              <ScrollArea h={scrollAreaHeight}>
                <NetworkCalls
                  data={networkCalls}
                  time={time}
                  correlationMap={props.correlationMap}
                  onSelect={props.onSelectNetworkCorrelation}
                />
              </ScrollArea>
            </Tabs.Panel>
            <Tabs.Panel value="console">
              <ScrollArea h={scrollAreaHeight}>
                <Console data={logs} time={time} />
              </ScrollArea>
            </Tabs.Panel>
            <Tabs.Panel value="errors">
              <ScrollArea h={scrollAreaHeight}>
                <Console
                  data={logs.filter((l) => l.value.level === 'error')}
                  time={time}
                />
              </ScrollArea>
            </Tabs.Panel>
          </Tabs>
        </Card>
      </Grid.Col>
    </Grid>
  )
}
