// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { Divider } from '@mantine/core'
import { LogData } from 'rrweb'

export const Console = (props: {
  data: { time: number; value: LogData }[]
  time: number
}) => {
  const dateTimeStyling: any = {
    error: {
      float: 'right',
      fontFamily: 'Monaco',
      paddingLeft: '20px',
      fontSize: 14,
      color: 'white',
    },
    warn: {
      float: 'right',
      fontFamily: 'Monaco',
      paddingLeft: '20px',
      fontSize: 14,
      color: 'white',
    },
    default: {
      float: 'right',
      fontFamily: 'Monaco',
      paddingLeft: '20px',
      fontSize: 14,
    },
  }
  const loggingStyling: any = {
    error: {
      background: '#FFC9BB',
      color: 'white',
    },
    warn: {
      background: '#FFFDD0',
      color: 'black',
    },
    default: {},
  }
  return (
    <>
      {props.data
        .filter((l) => l.time <= props.time)
        .map((log) => {
          const parsedPayload = JSON.parse(log.value.payload[0])
          return (
            <div
              style={{ marginTop: '10px', fontFamily: 'Monaco', fontSize: 14 }}
            >
              <div
                style={
                  dateTimeStyling[log.value.level] || dateTimeStyling['default']
                }
              >
                {new Date(log.time).toLocaleTimeString()}
              </div>
              <div
                style={
                  loggingStyling[log.value.level] || loggingStyling['default']
                }
              >
                {typeof parsedPayload === 'string'
                  ? parsedPayload
                  : log.value.payload}
              </div>
              <Divider mt={10} />
            </div>
          )
        })}
    </>
  )
}
