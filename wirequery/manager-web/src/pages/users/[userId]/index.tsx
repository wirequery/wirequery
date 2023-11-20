// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { UserDetails } from '@components/ce/app/user/UserDetails'
import DashboardLayout from '@components/ce/layout/DashboardLayout'
import { Anchor, Breadcrumbs } from '@mantine/core'
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
    </DashboardLayout>
  )
}
