import { TemplateForm } from '@components/app/template/TemplateForm'
import { TemplateList } from '@components/app/template/TemplateList'
import DashboardLayout from '@components/layout/DashboardLayout'
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
