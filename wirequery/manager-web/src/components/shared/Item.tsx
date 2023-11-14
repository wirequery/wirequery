// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { Anchor, createStyles, Text, ActionIcon } from '@mantine/core'
import { IconTrash } from '@tabler/icons-react'
import Link from 'next/link'
import { ReactNode } from 'react'

export interface ItemProps {
  label: string | ReactNode
  description?: string | ReactNode
  items?: (string | undefined | ReactNode)[]
  onDelete?: () => void
  onClick?: () => void
  href?: string | undefined
}

const useStyles = createStyles((theme) => {
  return {
    deleteButton: {
      marginLeft: theme.spacing.sm,
    },
    item: {
      paddingTop: theme.spacing.xl,
    },
    label: {
      fontWeight: 600,
      color:
        theme.colorScheme == 'dark'
          ? theme.colors.purple?.[3]
          : theme.colors.purple?.[5],
    },
  }
})

export const Item = (props: ItemProps) => {
  const { classes } = useStyles()
  const items = props.items?.filter((i) => i && i !== '') ?? []
  return (
    <div className={classes.item}>
      {props.onDelete ? (
        <ActionIcon
          title="Delete"
          mt={'xs'}
          color="purple"
          radius="xl"
          size="sm"
          style={{ float: 'right' }}
          variant="outline"
          onClick={() => props.onDelete?.()}
          className={classes.deleteButton}
        >
          <IconTrash size={13} />
        </ActionIcon>
      ) : (
        <></>
      )}

      {props.href && (
        <Anchor
          size="lg"
          href={props.href}
          component={Link}
          className={classes.label}
        >
          {props.label}
        </Anchor>
      )}

      {props.onClick && (
        <Anchor size="lg" onClick={props.onClick} className={classes.label}>
          {props.label}
        </Anchor>
      )}

      {!props.href && !props.onClick ? (
        <Text size="lg" className={classes.label}>
          {props.label}
        </Text>
      ) : (
        <></>
      )}

      {props.description && <Text size="md">{props.description}</Text>}

      <div>
        {items.map((el, i) => (
          <Text size="xs" key={i} component={'span'}>
            {el}
            {i !== items.length - 1 ? <span> · </span> : <></>}
          </Text>
        ))}
      </div>
    </div>
  )
}
