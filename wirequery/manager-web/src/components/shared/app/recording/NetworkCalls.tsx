// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { InitiatorType } from '@lib/rrweb-network-replay'
import { Divider, UnstyledButton } from '@mantine/core'
import { IconZoomScan } from '@tabler/icons-react'

export interface Event {
  start: number
  end: number
}

export interface NetworkCall {
  type: InitiatorType
  time: number
  events: Event[]
  url: string
  correlationId: string
  method: string
  statusCode: number
}

export const NetworkCalls = (props: {
  data: NetworkCall[]
  correlationMap: any
  onSelect: any
  time: number
}) => (
  <>
    {props.data
      .filter(
        (networkCall: NetworkCall) =>
          networkCall?.events?.[0]?.start <= props.time
      )
      .map((call: NetworkCall) => (
        <div>
          <div
            style={{
              float: 'right',
              fontSize: 14,
              marginRight: '15px',
            }}
          >
            {new Date(call.events[0].start).toLocaleTimeString()} (
            {call.events[0].end - call.events[0].start}ms)
          </div>
          <div style={{ marginTop: 10, marginBottom: 10 }}>
            <div>{call.url}</div>
            <div style={{ float: 'right', marginRight: '15px' }}>
              {props.correlationMap?.[call.correlationId] ? (
                <UnstyledButton
                  ml={'lg'}
                  onClick={() =>
                    props.onSelect(props.correlationMap?.[call.correlationId])
                  }
                  title="Extended Tracing"
                >
                  <IconZoomScan size={16} />
                </UnstyledButton>
              ) : (
                <></>
              )}
            </div>
            <div style={{ color: call.statusCode > 299 ? 'red' : '' }}>
              {call.statusCode} {call.method}
            </div>
          </div>
          <Divider />
        </div>
      ))}
  </>
)
