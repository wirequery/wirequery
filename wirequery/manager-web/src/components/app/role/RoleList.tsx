import { EmptyList } from '@components/shared/EmptyList'
import { ErrorMessage } from '@components/shared/ErrorMessage'
import { Item } from '@components/shared/Item'
import { LoadingScreen } from '@components/shared/LoadingScreen'
import { Mutation, Query } from '@generated/graphql'
import { showErrorAlert, showInfoAlert } from '@lib/alert'
import { createAuditItems } from '@lib/audit'
import { Modal } from '@mantine/core'
import { IconUsers } from '@tabler/icons-react'
import { useMemo, useState } from 'react'
import { gql, useMutation, useQuery } from 'urql'
import { RoleForm } from './RoleForm'

export interface RoleListProps {
  onCreateRole?: () => void
}

export function RoleList(props: RoleListProps) {
  const [editModalActive, setEditModalActive] = useState<string | undefined>(
    undefined
  )

  const [, executeDeleteMutation] = useMutation<Mutation>(gql`
    mutation deleteRole($id: ID!) {
      deleteRole(id: $id)
    }
  `)

  const handleDelete = (id: string) => {
    confirm('Are you sure you want to delete Role?') &&
      executeDeleteMutation({ id }, { additionalTypenames: ['Role'] })
        .then(() => showInfoAlert('Role deleted'))
        .catch(showErrorAlert)
  }

  const [{ data, error, fetching }] = useQuery<Query>({
    query: gql`
      query roleList {
        roles {
          id
          name
          authorisations {
            name
            label
            description
          }
          createdAt
          updatedAt
          createdBy
          updatedBy
        }
      }
    `,
    context: useMemo(
      () => ({
        additionalTypenames: ['Role'],
      }),
      []
    ),
  })

  if (fetching) {
    return <LoadingScreen />
  }

  if (error?.message) {
    return <ErrorMessage error={error} />
  }

  if (data?.roles?.length === 0) {
    return (
      <EmptyList
        icon={IconUsers}
        title="No roles found"
        description="No roles were found."
        onClick={props.onCreateRole}
      />
    )
  }

  return (
    <>
      {[...(data?.roles ?? [])]
        .sort((a, b) => a.name.localeCompare(b.name))
        .map((row) => (
          <Item
            key={row.id}
            label={row.name}
            onClick={() => setEditModalActive(row.id)}
            onDelete={() => handleDelete(row.id)}
            items={[
              ...(row.authorisations?.map((a) => a.label) ?? []),
              ...createAuditItems(row),
            ]}
          />
        ))}

      <Modal
        opened={!!editModalActive}
        title="Edit Role"
        onClose={() => setEditModalActive(undefined)}
      >
        <RoleForm
          id={editModalActive}
          onSave={() => setEditModalActive(undefined)}
          onCancel={() => setEditModalActive(undefined)}
        />
      </Modal>
    </>
  )
}
