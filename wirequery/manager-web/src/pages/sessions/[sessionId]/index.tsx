import { SessionDetails } from '@components/app/session/SessionDetails'
import DashboardLayout from '@components/layout/DashboardLayout'
import { Anchor, Breadcrumbs } from '@mantine/core'
import Link from 'next/link'
import { useRouter } from 'next/router'

export default function ShowSession() {
  const router = useRouter()
  const { sessionId } = router.query
  return (
    <DashboardLayout active="Sessions">
      <Breadcrumbs style={{ paddingBottom: 20 }}>
        <Anchor href={'/sessions'} component={Link}>
          Sessions
        </Anchor>
        <Anchor href={`/sessions/${sessionId}`} component={Link}>
          Selected Session
        </Anchor>
      </Breadcrumbs>
      {sessionId && <SessionDetails id={sessionId as string} />}
    </DashboardLayout>
  )
}
