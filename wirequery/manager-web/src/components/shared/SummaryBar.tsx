// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { Badge, Text } from '@mantine/core'
import { ReactNode } from 'react'

export interface SummaryBarProps {
  items?: (string | undefined | ReactNode)[]
  type?: 'badges' | 'dot-separated'
}

export const SummaryBar = (props: SummaryBarProps) => {
  const items = props.items?.filter((x) => x) ?? []
  if (props.type === 'badges') {
    return (
      <>
        {items?.map((el, i) => (
          <Badge mr="xs" size="xs" key={i}>
            {el}
          </Badge>
        ))}
      </>
    )
  }
  return (
    <div>
      {items?.map((el, i) => (
        <Text size="xs" key={i} component={'span'}>
          {el}
          {i !== items.length - 1 ? <span> · </span> : <></>}
        </Text>
      ))}
    </div>
  )
}
