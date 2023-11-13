import { Item } from '@components/shared/Item'
import { ItemList } from '@components/shared/ItemList'
import { Mutation, Query } from '@generated/graphql'
import { showErrorAlert, showInfoAlert } from '@lib/alert'
import { createAuditItems } from '@lib/audit'
import { IconServer } from '@tabler/icons-react'
import { useMemo } from 'react'
import { gql, useMutation, useQuery } from 'urql'

export interface ApplicationListProps {
  onCreateApplication?: () => void
}

export function ApplicationList(props: ApplicationListProps) {
  const [, executeDeleteMutation] = useMutation<Mutation>(gql`
    mutation deleteApplication($id: ID!) {
      deleteApplication(id: $id)
    }
  `)

  const handleDelete = (id: string) => {
    confirm('Are you sure you want to delete Application?') &&
      executeDeleteMutation({ id }, { additionalTypenames: ['Application'] })
        .then(() => showInfoAlert('Application deleted'))
        .catch(showErrorAlert)
  }

  const [{ data, error, fetching }] = useQuery<Query>({
    query: gql`
      query applications {
        applications {
          id
          name
          description
          inQuarantine
          createdAt
          updatedAt
          createdBy
          updatedBy
        }
      }
    `,
    context: useMemo(
      () => ({
        additionalTypenames: ['Application'],
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
      emptyIcon={IconServer}
      emptyTitle="No applications found"
      emptyDescription="No applications were found"
      emptyButtonText="Create application"
      emptyOnClick={props.onCreateApplication}
      data={[...(data?.applications ?? [])].sort((a, b) =>
        a.name.localeCompare(b.name)
      )}
    >
      {(row) => (
        <Item
          key={row.id}
          label={row.name}
          onDelete={() => handleDelete(row.id)}
          href={`/applications/${row.id}`}
          description={row.description}
          items={[
            row.inQuarantine ? 'In Quarantine' : undefined,
            ...createAuditItems(row),
          ]}
        />
      )}
    </ItemList>
  )
}
