// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { StoredQueryDetails } from '@components/shared/app/stored-query/StoredQueryDetails'
import DashboardLayout from '@components/ce/layout/DashboardLayout'
import { Anchor, Breadcrumbs } from '@mantine/core'
import Link from 'next/link'
import { useRouter } from 'next/router'

export default function ShowStoredQuery() {
  const router = useRouter()
  const { storedQueryId, sessionId } = router.query
  return (
    <DashboardLayout active="Sessions">
      <Breadcrumbs style={{ paddingBottom: 20 }}>
        <Anchor href={'/sessions'} component={Link}>
          Sessions
        </Anchor>
        <Anchor href={`/sessions/${sessionId}`} component={Link}>
          Selected Session
        </Anchor>
        <Anchor
          href={`/sessions/${sessionId}/stored-querys/${storedQueryId}`}
          component={Link}
        >
          Selected Query
        </Anchor>
      </Breadcrumbs>
      {storedQueryId && (
        <StoredQueryDetails
          id={storedQueryId as string}
          sessionId={sessionId as string}
        />
      )}
    </DashboardLayout>
  )
}
