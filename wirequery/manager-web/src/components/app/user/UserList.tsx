import { Item } from '@components/shared/Item'
import { ItemList } from '@components/shared/ItemList'
import { Mutation, Query } from '@generated/graphql'
import { showErrorAlert, showInfoAlert } from '@lib/alert'
import { createAuditItems } from '@lib/audit'
import { IconUsers } from '@tabler/icons-react'
import { useMemo } from 'react'
import { gql, useMutation, useQuery } from 'urql'

export interface UserListProps {
  onCreateUser?: () => void
}

export function UserList(props: UserListProps) {
  const [, executeDeleteMutation] = useMutation<Mutation>(gql`
    mutation deleteUser($id: ID!) {
      deleteUser(id: $id)
    }
  `)

  const handleDelete = (id: string) => {
    confirm('Are you sure you want to delete User?') &&
      executeDeleteMutation({ id }, { additionalTypenames: ['User'] })
        .then(() => showInfoAlert('User deleted'))
        .catch(showErrorAlert)
  }

  const [{ data, error, fetching }] = useQuery<Query>({
    query: gql`
      query users {
        users {
          id
          username
          enabled
          roles
          createdAt
          updatedAt
          createdBy
          updatedBy
        }
      }
    `,
    context: useMemo(
      () => ({
        additionalTypenames: ['User'],
      }),
      []
    ),
  })

  return (
    <ItemList
      fetching={fetching}
      error={error}
      filters={[
        { label: 'Username', field: 'username', type: 'text' },
        { label: 'Role', field: 'roles', type: 'text' },
      ]}
      emptyIcon={IconUsers}
      emptyTitle="No users found"
      emptyDescription="No users were found."
      emptyButtonText="Add new user"
      emptyOnClick={props.onCreateUser}
      data={[...(data?.users ?? [])].sort((a, b) =>
        a.username.localeCompare(b.username)
      )}
    >
      {(row) => (
        <Item
          key={row.id}
          label={row.username}
          onDelete={() => handleDelete(row.id)}
          href={`/users/${row.id}`}
          items={[
            row.enabled ? 'Enabled' : 'Disabled',
            row.roles
              .split(',')
              .filter((r) => r !== '')
              .join(', '),
            ...createAuditItems(row),
          ]}
        />
      )}
    </ItemList>
  )
}
