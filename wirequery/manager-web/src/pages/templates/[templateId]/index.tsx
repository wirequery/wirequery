// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { TemplateDetails } from '@components/app/template/TemplateDetails'
import DashboardLayout from '@components/layout/DashboardLayout'
import { Anchor, Breadcrumbs } from '@mantine/core'
import Link from 'next/link'
import { useRouter } from 'next/router'

export default function ShowTemplate() {
  const router = useRouter()
  const { templateId } = router.query
  return (
    <DashboardLayout active="Templates">
      <Breadcrumbs style={{ paddingBottom: 20 }}>
        <Anchor href={'/templates'} component={Link}>
          Templates
        </Anchor>
        <Anchor href={`/templates/${templateId}`} component={Link}>
          Selected Templates
        </Anchor>
      </Breadcrumbs>
      {templateId && <TemplateDetails id={templateId as string} />}
    </DashboardLayout>
  )
}
