// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { TraceDetails } from '@components/shared/app/trace/TraceDetails'
import { Box, Divider, Flex, Modal, UnstyledButton } from '@mantine/core'
import { IconCopy, IconZoomScan } from '@tabler/icons-react'
import { useState } from 'react'
import { LogTree } from './LogTree'

export interface LogTreeListProps {
  storedQueryId: string | number
  rows?: {
    message: string
    traceId?: string | null
    startTime: number
    endTime: number
  }[]
  extendedTracing: boolean
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
          <>
            <Flex key={i}>
              <div style={{ flex: 1 }}>
                <LogTree
                  display={display}
                  startTime={row.startTime}
                  endTime={row.endTime}
                  traceId={row.traceId}
                />
              </div>
              <Box mt="xs">
                {row.traceId && props.extendedTracing && (
                  <UnstyledButton
                    mr={'lg'}
                    title="Extended Tracing"
                    onClick={() => {
                      setSelectedItem({
                        storedQueryId: props.storedQueryId,
                        traceId: row.traceId,
                      })
                    }}
                  >
                    <IconZoomScan size={16} />
                  </UnstyledButton>
                )}

                <UnstyledButton
                  onClick={() =>
                    navigator.clipboard.writeText(JSON.stringify(display))
                  }
                >
                  <IconCopy size={16} />
                </UnstyledButton>
              </Box>
            </Flex>
            {i < (props?.rows?.length ?? 0) - 1 ? <Divider /> : <></>}
          </>
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
