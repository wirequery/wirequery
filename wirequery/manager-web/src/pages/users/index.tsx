// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { RoleForm } from '@components/shared/app/role/RoleForm'
import { RoleList } from '@components/shared/app/role/RoleList'
import { UserForm } from '@components/ce/app/user/UserForm'
import { UserList } from '@components/shared/app/user/UserList'
import DashboardLayout from '@components/ce/layout/DashboardLayout'
import { Button, Grid, Menu, Modal, Tabs, Title } from '@mantine/core'
import { IconChevronDown, IconUser, IconUserCheck } from '@tabler/icons-react'
import { useState } from 'react'
import { StatisticList } from '@components/shared/app/statistic/StatisticList'

export default function Users() {
  const [userModalActive, setUserModalActive] = useState(false)
  const [roleModalActive, setRoleModalActive] = useState(false)
  return (
    <>
      <DashboardLayout active="Management">
        <Grid>
          <Grid.Col span="auto">
            <Title order={2}>Management</Title>
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
              </Menu.Dropdown>
            </Menu>
          </Grid.Col>
        </Grid>

        <Tabs defaultValue="users" mt="xl">
          <Tabs.List>
            <Tabs.Tab value="users">Users</Tabs.Tab>
            <Tabs.Tab value="roles">User Roles</Tabs.Tab>
            <Tabs.Tab value="statistics">Statistics</Tabs.Tab>
          </Tabs.List>
          <Tabs.Panel value="users">
            <UserList />
          </Tabs.Panel>
          <Tabs.Panel value="roles">
            <RoleList />
          </Tabs.Panel>
          <Tabs.Panel value="statistics">
            <StatisticList />
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
    </>
  )
}
