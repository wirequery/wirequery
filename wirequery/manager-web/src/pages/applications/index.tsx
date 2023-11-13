import { ApplicationList } from '@components/app/application/ApplicationList'
import DashboardLayout from '@components/layout/DashboardLayout'
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
        <p>You can add a new application from the Groups screen.</p>
        <p>Go to:</p>
        <ul>
          <li>Click 'Groups' in the navigation bar</li>
          <li>Select a group</li>
          <li>Click 'Actions'</li>
          <li>Click 'Add New Application'</li>
        </ul>
        <Button onClick={() => setModalActive(false)}>OK</Button>
      </Modal>
    </>
  )
}
