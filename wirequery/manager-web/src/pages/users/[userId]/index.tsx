import { GroupUserList } from 'src/ee/components/group-user/GroupUserList'
import { UserDetails } from '@components/app/user/UserDetails'
import DashboardLayout from '@components/layout/DashboardLayout'
import { Anchor, Breadcrumbs, Tabs } from '@mantine/core'
import Link from 'next/link'
import { useRouter } from 'next/router'

export default function ShowUser() {
  const router = useRouter()
  const { userId } = router.query
  return (
    <DashboardLayout active="Users">
      <Breadcrumbs style={{ paddingBottom: 20 }}>
        <Anchor href={'/users'} component={Link}>
          Users
        </Anchor>
        <Anchor href={`/users/${userId}`} component={Link}>
          Selected User
        </Anchor>
      </Breadcrumbs>
      <UserDetails id={userId as string} />
      <Tabs defaultValue="groups">
        <Tabs.List>
          <Tabs.Tab value="groups">Groups</Tabs.Tab>
        </Tabs.List>
        <Tabs.Panel value="groups">
          <GroupUserList userId={'' + userId} />
        </Tabs.Panel>
      </Tabs>
    </DashboardLayout>
  )
}
