import { StoredQueryDetails } from '@components/app/stored-query/StoredQueryDetails'
import DashboardLayout from '@components/layout/DashboardLayout'
import { Anchor, Breadcrumbs } from '@mantine/core'
import Link from 'next/link'
import { useRouter } from 'next/router'

export default function ShowStoredQuery() {
  const router = useRouter()
  const { storedQueryId } = router.query
  return (
    <DashboardLayout active="Queries">
      <Breadcrumbs style={{ paddingBottom: 20 }}>
        <Anchor href={'/stored-querys'} component={Link}>
          Queries
        </Anchor>
        <Anchor href={`/stored-querys/${storedQueryId}`} component={Link}>
          Selected Query
        </Anchor>
      </Breadcrumbs>
      {storedQueryId && <StoredQueryDetails id={storedQueryId as string} />}
    </DashboardLayout>
  )
}
