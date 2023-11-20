// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { ApplicationForm } from '@components/shared/app/application/ApplicationForm'
import { ApplicationList } from '@components/ce/app/application/ApplicationList'
import DashboardLayout from '@components/ce/layout/DashboardLayout'
import { Button, Grid, Modal, Title } from '@mantine/core'
import { useState } from 'react'

export default function Applications() {
  const [modalActive, setModalActive] = useState(false)
  return (
    <>
      <DashboardLayout active="Applications">
        <Grid>
          <Grid.Col span="auto">
            <Title order={2}>Applications</Title>
          </Grid.Col>
          <Grid.Col span="content">
            <Button onClick={() => setModalActive(true)}>New</Button>
          </Grid.Col>
        </Grid>
        <ApplicationList onCreateApplication={() => setModalActive(true)} />
      </DashboardLayout>
      <Modal
        opened={modalActive}
        title="New Application"
        onClose={() => setModalActive(false)}
      >
        <ApplicationForm
          onSave={() => setModalActive(false)}
          onCancel={() => setModalActive(false)}
        />
      </Modal>
    </>
  )
}
