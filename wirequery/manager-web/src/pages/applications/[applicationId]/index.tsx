// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { ApplicationDetails } from '@components/ce/app/application/ApplicationDetails'
import DashboardLayout from '@components/ce/layout/DashboardLayout'
import { Anchor, Breadcrumbs } from '@mantine/core'
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
    </DashboardLayout>
  )
}
