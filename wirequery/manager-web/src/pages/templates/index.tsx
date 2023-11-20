// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { TemplateForm } from '@components/shared/app/template/TemplateForm'
import { TemplateList } from '@components/shared/app/template/TemplateList'
import DashboardLayout from '@components/ce/layout/DashboardLayout'
import { Button, Grid, Modal, Title } from '@mantine/core'
import { useState } from 'react'

export default function Templates() {
  const [modalActive, setModalActive] = useState(false)
  return (
    <>
      <DashboardLayout active="Templates">
        <Grid>
          <Grid.Col span="auto">
            <Title order={2}>Templates</Title>
          </Grid.Col>
          <Grid.Col span="content">
            <Button onClick={() => setModalActive(true)}>New</Button>
          </Grid.Col>
        </Grid>
        <TemplateList onCreateTemplate={() => setModalActive(true)} />
      </DashboardLayout>
      <Modal
        size="xl"
        opened={modalActive}
        title="New Template"
        onClose={() => setModalActive(false)}
      >
        <TemplateForm
          onSave={() => setModalActive(false)}
          onCancel={() => setModalActive(false)}
        />
      </Modal>
    </>
  )
}
