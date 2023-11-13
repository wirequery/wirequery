import { ErrorMessage } from '@components/shared/ErrorMessage'
import { LoadingScreen } from '@components/shared/LoadingScreen'
import { SummaryBar } from '@components/shared/SummaryBar'
import { Query } from '@generated/graphql'
import { createAuditItems } from '@lib/audit'
import { Button, Grid, Menu, Modal, Title } from '@mantine/core'
import {
  IconBoxMultiple,
  IconChevronDown,
  IconSettings,
} from '@tabler/icons-react'
import { useMemo, useState } from 'react'
import { gql, useQuery } from 'urql'
import { GroupUserForm } from '../../../ee/components/group-user/GroupUserForm'
import { UserForm } from './UserForm'

export interface UserDetailsProps {
  id: string | number
}

export function UserDetails(props: UserDetailsProps) {
  const [newGroupUserModalActive, setNewGroupUserModalActive] = useState(false)
  const [editModalActive, setEditModalActive] = useState(false)

  const [{ data, error }] = useQuery<Query>({
    query: gql`
      query userDetails($id: ID!) {
        user(id: $id) {
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
    variables: { id: props.id },
    context: useMemo(
      () => ({
        additionalTypenames: ['User'],
      }),
      []
    ),
  })

  if (error?.message) {
    return <ErrorMessage error={error} />
  }

  if (!data) {
    return <LoadingScreen />
  }

  return (
    <>
      <Grid>
        <Grid.Col span="auto">
          <Title order={2}>{data?.user?.username}</Title>
        </Grid.Col>
        <Grid.Col span="content">
          <Menu shadow="md" width={200}>
            <Menu.Target>
              <Button leftIcon={<IconChevronDown size="1rem" />}>
                Actions
              </Button>
            </Menu.Target>

            <Menu.Dropdown>
              <Menu.Item
                icon={<IconSettings size={14} />}
                onClick={() => setEditModalActive(true)}
              >
                Edit
              </Menu.Item>
              <Menu.Item
                icon={<IconBoxMultiple size={14} />}
                onClick={() => setNewGroupUserModalActive(true)}
              >
                Add Group
              </Menu.Item>
            </Menu.Dropdown>
          </Menu>
        </Grid.Col>
      </Grid>

      <SummaryBar
        items={[
          data?.user?.enabled ? 'Enabled' : 'Disabled',
          ...(data?.user?.roles.split(',').filter((r) => r !== '') ?? []),
          ...createAuditItems(data?.user),
        ]}
      />

      <Modal
        opened={newGroupUserModalActive}
        title="Add User to Group"
        onClose={() => setNewGroupUserModalActive(false)}
      >
        <GroupUserForm
          userId={'' + props.id}
          onSave={() => setNewGroupUserModalActive(false)}
          onCancel={() => setNewGroupUserModalActive(false)}
        />
      </Modal>

      <Modal
        opened={editModalActive}
        title="Edit User"
        onClose={() => setEditModalActive(false)}
      >
        <UserForm
          id={props.id}
          onSave={() => setEditModalActive(false)}
          onCancel={() => setEditModalActive(false)}
        />
      </Modal>
    </>
  )
}
