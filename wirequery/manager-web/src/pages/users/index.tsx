import { GroupRoleForm } from 'src/ee/components/group-role/GroupRoleForm'
import { GroupRoleList } from 'src/ee/components/group-role/GroupRoleList'
import { RoleForm } from '@components/app/role/RoleForm'
import { RoleList } from '@components/app/role/RoleList'
import { UserForm } from '@components/app/user/UserForm'
import { UserList } from '@components/app/user/UserList'
import DashboardLayout from '@components/layout/DashboardLayout'
import { Button, Grid, Menu, Modal, Tabs, Title } from '@mantine/core'
import {
  IconChevronDown,
  IconUser,
  IconUserCheck,
  IconUsersGroup,
} from '@tabler/icons-react'
import { useState } from 'react'

export default function Users() {
  const [userModalActive, setUserModalActive] = useState(false)
  const [roleModalActive, setRoleModalActive] = useState(false)
  const [groupRoleModalActive, setGroupRoleModalActive] = useState(false)
  return (
    <>
      <DashboardLayout active="User Management">
        <Grid>
          <Grid.Col span="auto">
            <Title order={2}>User Management</Title>
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
                  icon={<IconUser size={14} />}
                  onClick={() => setUserModalActive(true)}
                >
                  Add New User
                </Menu.Item>
                <Menu.Item
                  icon={<IconUserCheck size={14} />}
                  onClick={() => setRoleModalActive(true)}
                >
                  Add New Role
                </Menu.Item>
                <Menu.Item
                  icon={<IconUsersGroup size={14} />}
                  onClick={() => setGroupRoleModalActive(true)}
                >
                  Add New Group Role
                </Menu.Item>
              </Menu.Dropdown>
            </Menu>
          </Grid.Col>
        </Grid>

        <Tabs defaultValue="users" mt="xl">
          <Tabs.List>
            <Tabs.Tab value="users">Users</Tabs.Tab>
            <Tabs.Tab value="roles">User Roles</Tabs.Tab>
            <Tabs.Tab value="groupRoles">Group Roles</Tabs.Tab>
          </Tabs.List>
          <Tabs.Panel value="users">
            <UserList />
          </Tabs.Panel>
          <Tabs.Panel value="roles">
            <RoleList />
          </Tabs.Panel>
          <Tabs.Panel value="groupRoles">
            <GroupRoleList />
          </Tabs.Panel>
        </Tabs>
      </DashboardLayout>

      <Modal
        opened={userModalActive}
        title="New User"
        onClose={() => setUserModalActive(false)}
      >
        <UserForm
          onSave={() => setUserModalActive(false)}
          onCancel={() => setUserModalActive(false)}
        />
      </Modal>

      <Modal
        opened={roleModalActive}
        title="New Role"
        onClose={() => setRoleModalActive(false)}
      >
        <RoleForm
          onSave={() => setRoleModalActive(false)}
          onCancel={() => setRoleModalActive(false)}
        />
      </Modal>

      <Modal
        opened={groupRoleModalActive}
        title="New Group Role"
        onClose={() => setGroupRoleModalActive(false)}
      >
        <GroupRoleForm
          onSave={() => setGroupRoleModalActive(false)}
          onCancel={() => setGroupRoleModalActive(false)}
        />
      </Modal>
    </>
  )
}
