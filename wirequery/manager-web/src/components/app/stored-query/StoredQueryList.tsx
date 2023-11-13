import { Item } from '@components/shared/Item'
import { ItemList } from '@components/shared/ItemList'
import { Mutation, Query } from '@generated/graphql'
import { showErrorAlert, showInfoAlert } from '@lib/alert'
import { createAuditItems } from '@lib/audit'
import { IconListSearch } from '@tabler/icons-react'
import { useMemo } from 'react'
import { gql, useMutation, useQuery } from 'urql'

export interface StoredQueryListProps {
  applicationId?: string
  sessionId?: string
  hasSessionId?: boolean
  onCreateStoredQuery?: () => void
}

export function StoredQueryList(props: StoredQueryListProps) {
  const [, executeDeleteMutation] = useMutation<Mutation>(gql`
    mutation deleteStoredQuery($id: ID!) {
      deleteStoredQuery(id: $id)
    }
  `)

  const handleDelete = (id: string) => {
    confirm('Are you sure you want to delete StoredQuery?') &&
      executeDeleteMutation({ id }, { additionalTypenames: ['StoredQuery'] })
        .then(() => showInfoAlert('Query deleted'))
        .catch(showErrorAlert)
  }

  const [{ data, error, fetching }] = useQuery<Query>({
    query: gql`
      query storedQueryList($filter: StoredQueryFilter) {
        storedQuerys(filter: $filter) {
          id
          application {
            id
            name
          }
          session {
            id
            name
          }
          query
          queryLimit
          endDate
          name
          type
          createdAt
          updatedAt
          createdBy
          updatedBy
        }
      }
    `,
    variables: {
      filter: {
        applicationId:
          props.applicationId !== undefined
            ? parseInt(props.applicationId)
            : undefined,
        sessionId:
          props.sessionId !== undefined ? parseInt(props.sessionId) : undefined,
        hasSessionId: props.hasSessionId,
      },
    },
    context: useMemo(
      () => ({
        additionalTypenames: ['StoredQuery'],
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
      emptyIcon={IconListSearch}
      emptyTitle="No queries found"
      emptyDescription="No queries were found."
      emptyButtonText="Create new query"
      emptyOnClick={props.onCreateStoredQuery}
      data={[...(data?.storedQuerys ?? [])].sort((a, b) =>
        a.name.localeCompare(b.name)
      )}
    >
      {(row) => (
        <Item
          key={row.id}
          label={row.name}
          onDelete={() => handleDelete(row.id)}
          href={
            props.sessionId
              ? `/sessions/${props.sessionId}/stored-querys/${row.id}`
              : `/stored-querys/${row.id}`
          }
          items={[
            row.session?.name,
            row.application?.name,
            row.queryLimit && 'Max ' + row.queryLimit,
            row.endDate && 'Ends ' + new Date(row.endDate).toLocaleString(),
            ...createAuditItems(row),
          ]}
        />
      )}
    </ItemList>
  )
}
