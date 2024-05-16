// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import {
  ActionIcon,
  Box,
  Button,
  Modal,
  Pagination,
  TextInput,
} from '@mantine/core'
import { useForm } from '@mantine/form'
import { IconFilter, TablerIconsProps } from '@tabler/icons-react'
import { ReactNode, useState } from 'react'
import { CombinedError } from 'urql'
import { EmptyList } from './EmptyList'
import { ErrorMessage } from './ErrorMessage'
import { LoadingScreen } from './LoadingScreen'

const MAX_PER_PAGE = 10

export interface ItemListProps<T> {
  fetching: boolean
  error: CombinedError | undefined
  filters?: { label: string; field: string; type: 'text' }[]
  emptyIcon: (props: TablerIconsProps) => ReactNode
  emptyTitle: string
  emptyDescription: string
  emptyButtonText?: string
  emptyOnClick?: () => void
  data: T[]
  maxPerPage?: number
  children: (row: T) => ReactNode
}

export function ItemList<T>(props: ItemListProps<T>) {
  const maxPerPage = props.maxPerPage ?? MAX_PER_PAGE
  const [page, setPage] = useState(1)
  const [filterModalOpened, setFilterModalOpened] = useState(false)

  const initialValues: any = {}

  props.filters?.forEach((f) => {
    if (f.type === 'text') {
      initialValues[f.field] = ''
    }
  })

  const form = useForm({ initialValues: { ...initialValues } })

  const applyFilters = (value: any) => {
    return (
      props.filters?.every((f) => {
        if (f.type === 'text') {
          if (
            form.values[f.field] !== undefined &&
            form.values[f.field] !== ''
          ) {
            return ('' + value[f.field])
              .toLowerCase()
              .includes(('' + form.values[f.field]).toLowerCase())
          }
        }
        return true
      }) ?? true
    )
  }

  if (props.fetching) {
    return <LoadingScreen />
  }

  if (props.error?.message) {
    return <ErrorMessage error={props.error} />
  }

  if (props.data.length === 0) {
    return (
      <EmptyList
        icon={props.emptyIcon}
        title={props.emptyTitle}
        description={props.emptyDescription}
        buttonText={props.emptyButtonText}
        onClick={props.emptyOnClick}
      />
    )
  }

  const filteredData = props.data
    .filter((f) => applyFilters(f))
    .slice((page - 1) * maxPerPage, page * maxPerPage)

  return (
    <>
      {props.filters ? (
        <ActionIcon
          title="Filter"
          mt={'xl'}
          color="blue"
          size="sm"
          variant="outline"
          onClick={() => setFilterModalOpened(true)}
        >
          <IconFilter size={13} />
        </ActionIcon>
      ) : (
        <></>
      )}

      {filteredData.length === 0 ? (
        <Box mt="xl">
          No results. Please update or reset your filter settings.
        </Box>
      ) : (
        filteredData.map((d) => props.children(d))
      )}

      <Pagination
        mt={'xl'}
        onChange={(p) => {
          setPage(p)
          window.scrollTo(0, 0)
        }}
        total={Math.ceil(props.data.length / maxPerPage)}
      />
      <Modal
        opened={filterModalOpened}
        onClose={() => setFilterModalOpened(false)}
        title="Filter Settings"
      >
        <form
          onSubmit={(e) => {
            setFilterModalOpened(false)
            e.preventDefault()
          }}
        >
          {props.filters?.map((filter) =>
            filter.type === 'text' ? (
              <TextInput
                label={filter.label}
                key={filter.field}
                mb="sm"
                {...form.getInputProps(filter.field)}
              />
            ) : (
              <></>
            )
          )}
          <Box mt="md">
            <Button onClick={() => setFilterModalOpened(false)}>Close</Button>
            <Button
              variant="outline"
              ml="sm"
              onClick={() => form.setValues({ ...initialValues })}
            >
              Reset
            </Button>
          </Box>
        </form>
      </Modal>
    </>
  )
}
