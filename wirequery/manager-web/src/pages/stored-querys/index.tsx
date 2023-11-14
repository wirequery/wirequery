// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { StoredQueryForm } from '@components/app/stored-query/StoredQueryForm'
import { StoredQueryList } from '@components/app/stored-query/StoredQueryList'
import DashboardLayout from '@components/layout/DashboardLayout'
import { Button, Grid, Modal, Title } from '@mantine/core'
import { useState } from 'react'

export default function StoredQueries() {
  const [modalActive, setModalActive] = useState(false)
  return (
    <>
      <DashboardLayout active="Queries">
        <Grid>
          <Grid.Col span="auto">
            <Title order={2}>Queries</Title>
          </Grid.Col>
          <Grid.Col span="content">
            <Button onClick={() => setModalActive(true)}>New</Button>
          </Grid.Col>
        </Grid>
        <StoredQueryList
          onCreateStoredQuery={() => setModalActive(true)}
          hasSessionId={false}
        />
      </DashboardLayout>
      <Modal
        opened={modalActive}
        title="New Stored Query"
        onClose={() => setModalActive(false)}
      >
        <StoredQueryForm
          onSave={() => setModalActive(false)}
          onCancel={() => setModalActive(false)}
        />
      </Modal>
    </>
  )
}
