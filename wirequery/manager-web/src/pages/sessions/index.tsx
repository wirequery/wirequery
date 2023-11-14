// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { SessionForm } from '@components/app/session/SessionForm'
import { SessionList } from '@components/app/session/SessionList'
import DashboardLayout from '@components/layout/DashboardLayout'
import { Button, Grid, Modal, Title } from '@mantine/core'
import { useState } from 'react'

export default function Sessions() {
  const [modalActive, setModalActive] = useState(false)
  return (
    <>
      <DashboardLayout active="Sessions">
        <Grid>
          <Grid.Col span="auto">
            <Title order={2}>Sessions</Title>
          </Grid.Col>
          <Grid.Col span="content">
            <Button onClick={() => setModalActive(true)}>New</Button>
          </Grid.Col>
        </Grid>
        <SessionList onCreateSession={() => setModalActive(true)} />
      </DashboardLayout>
      <Modal
        size="lg"
        opened={modalActive}
        title="New Session"
        onClose={() => setModalActive(false)}
      >
        <SessionForm
          onSave={() => setModalActive(false)}
          onCancel={() => setModalActive(false)}
        />
      </Modal>
    </>
  )
}
