// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { Divider, Modal, UnstyledButton } from '@mantine/core'
import { IconBinaryTree2, IconCopy } from '@tabler/icons-react'
import { LogTree } from './LogTree'
import { useState } from 'react'
import { TraceDetails } from '@components/shared/app/trace/TraceDetails'

export interface LogTreeListProps {
  sessionId?: string | number | null
  storedQueryId: string | number
  rows?: {
    message: string
    traceId?: string | null
    startTime: number
    endTime: number
  }[]
}

export const LogTreeList = (props: LogTreeListProps) => {
  const [selectedItem, setSelectedItem] = useState<any>(undefined)
  if (!props.rows) {
    return <></>
  }

  return (
    <>
      {props.rows.map((row, i) => {
        const json = JSON.parse(row.message)
        const display = json?.result ?? json?.error
        return (
          <div key={i}>
            <div style={{ float: 'right' }}>
              {row.traceId && (
                <UnstyledButton
                  mr={'lg'}
                  onClick={() => {
                    setSelectedItem({
                      storedQueryId: props.storedQueryId,
                      traceId: row.traceId,
                    })
                  }}
                >
                  <IconBinaryTree2 size={16} />
                </UnstyledButton>
              )}

              <UnstyledButton
                onClick={() =>
                  navigator.clipboard.writeText(JSON.stringify(display))
                }
              >
                <IconCopy size={16} />
              </UnstyledButton>
            </div>
            <LogTree
              display={display}
              startTime={row.startTime}
              endTime={row.endTime}
              traceId={row.traceId}
            />
            {i < (props?.rows?.length ?? 0) - 1 ? <Divider /> : <></>}
          </div>
        )
      })}

      <Modal
        opened={selectedItem !== undefined}
        onClose={() => setSelectedItem(undefined)}
        size="xl"
        title={selectedItem?.traceId}
      >
        {selectedItem?.storedQueryId && (
          <TraceDetails
            storedQueryId={selectedItem.storedQueryId}
            traceId={selectedItem.traceId}
          />
        )}
      </Modal>
    </>
  )
}
