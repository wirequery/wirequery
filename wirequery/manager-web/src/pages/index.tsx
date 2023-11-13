import { QueryForm } from '@components/app/query/QueryForm'
import { StoredQueryForm } from '@components/app/stored-query/StoredQueryForm'
import DashboardLayout from '@components/layout/DashboardLayout'
import { Modal } from '@mantine/core'
import { useRouter } from 'next/router'
import { useState } from 'react'

export default function Home() {
  const [modalActive, setModalActive] = useState<boolean>(false)
  const [query, setQuery] = useState<string | undefined>(undefined)
  const router = useRouter()
  return (
    <DashboardLayout active="Explore">
      <QueryForm
        onSaveClick={(query) => {
          setModalActive(true)
          setQuery(query)
        }}
      />
      <Modal
        opened={!!modalActive}
        title="Save query"
        onClose={() => setModalActive(false)}
      >
        <StoredQueryForm
          query={query}
          onSave={(id) => {
            setModalActive(false)
            router.push(`/stored-querys/${id}`)
          }}
          onCancel={() => setModalActive(false)}
        />
      </Modal>
    </DashboardLayout>
  )
}
