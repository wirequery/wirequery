import { Item } from '@components/shared/Item'
import { ItemList } from '@components/shared/ItemList'
import { Mutation, Query } from '@generated/graphql'
import { showErrorAlert, showInfoAlert } from '@lib/alert'
import { createAuditItems } from '@lib/audit'
import { IconReportSearch } from '@tabler/icons-react'
import React, { useMemo } from 'react'
import { gql, useMutation, useQuery } from 'urql'

export interface SessionListProps {
  onCreateSession?: () => void
}

export function SessionList(props: SessionListProps) {
  const [, executeDeleteMutation] = useMutation<Mutation>(gql`
    mutation deleteSession($id: ID!) {
      deleteSession(id: $id)
    }
  `)

  const handleDelete = (id: string) => {
    confirm('Are you sure you want to delete Session?') &&
      executeDeleteMutation({ id }, { additionalTypenames: ['Session'] })
        .then(() => showInfoAlert('Session deleted'))
        .catch(showErrorAlert)
  }

  const [{ data, error, fetching }] = useQuery<Query>({
    query: gql`
      query sessions {
        sessions {
          id
          name
          description
          createdAt
          updatedAt
          createdBy
          updatedBy
        }
      }
    `,
    context: useMemo(
      () => ({
        additionalTypenames: ['Session'],
      }),
      []
    ),
  })

  return (
    <ItemList
      fetching={fetching}
      error={error}
      filters={[
        { label: 'Name', field: 'name', type: 'text' },
        { label: 'Description', field: 'description', type: 'text' },
      ]}
      emptyIcon={IconReportSearch}
      emptyTitle="No sessions found"
      emptyDescription="No sessions were found."
      emptyButtonText="Create session"
      emptyOnClick={props.onCreateSession}
      data={[...(data?.sessions ?? [])].sort((a, b) =>
        a.name.localeCompare(b.name)
      )}
    >
      {(row) => (
        <Item
          key={row.id}
          label={row.name}
          onDelete={() => handleDelete(row.id)}
          href={`/sessions/${row.id}`}
          description={row.description}
          items={createAuditItems(row)}
        />
      )}
    </ItemList>
  )
}
