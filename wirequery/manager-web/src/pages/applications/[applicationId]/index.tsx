import { ApplicationDetails } from '@components/app/application/ApplicationDetails'
import { GroupApplicationList } from 'src/ee/components/group-application/GroupApplicationList'
import { StoredQueryList } from '@components/app/stored-query/StoredQueryList'
import DashboardLayout from '@components/layout/DashboardLayout'
import { Anchor, Breadcrumbs, Tabs } from '@mantine/core'
import Link from 'next/link'
import { useRouter } from 'next/router'

export default function ShowApplication() {
  const router = useRouter()
  const { applicationId } = router.query
  return (
    <DashboardLayout active="Applications">
      <Breadcrumbs style={{ paddingBottom: 20 }}>
        <Anchor href={'/applications'} component={Link}>
          Applications
        </Anchor>
        <Anchor href={`/applications/${applicationId}`} component={Link}>
          Selected Application
        </Anchor>
      </Breadcrumbs>
      {applicationId && <ApplicationDetails id={applicationId as string} />}
      <Tabs defaultValue="groups">
        <Tabs.List>
          <Tabs.Tab value="groups">Groups</Tabs.Tab>
          <Tabs.Tab value="storedQueries">Queries</Tabs.Tab>
        </Tabs.List>
        <Tabs.Panel value="groups">
          <GroupApplicationList applicationId={'' + applicationId} />
        </Tabs.Panel>
        <Tabs.Panel value="storedQueries">
          <StoredQueryList applicationId={'' + applicationId} />
        </Tabs.Panel>
      </Tabs>
    </DashboardLayout>
  )
}
