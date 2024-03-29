// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { SessionDetails } from '@components/shared/app/session/SessionDetails'
import DashboardLayout from '@components/ce/layout/DashboardLayout'
import { Anchor, Breadcrumbs } from '@mantine/core'
import Link from 'next/link'
import { useRouter } from 'next/router'

export default function ShowSession() {
  const router = useRouter()
  const { sessionId } = router.query
  return (
    <DashboardLayout active="Sessions">
      <Breadcrumbs style={{ paddingBottom: 20 }}>
        <Anchor href={'/'} component={Link}>
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
