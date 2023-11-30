// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { DetailsTable } from '@components/shared/DetailsTable'
import { ErrorMessage } from '@components/shared/ErrorMessage'
import { LoadingScreen } from '@components/shared/LoadingScreen'
import { SummaryBar } from '@components/shared/SummaryBar'
import { Mutation, Query } from '@generated/graphql'
import { showErrorAlertForMessage, showInfoAlert } from '@lib/alert'
import { createAuditItems } from '@lib/audit'
import { Badge, Button, Grid, Menu, Modal, Title } from '@mantine/core'
import {
  IconChevronDown,
  IconLockOpen,
  IconSettings,
} from '@tabler/icons-react'
import { useMemo, useState } from 'react'
import { gql, useMutation, useQuery } from 'urql'
import { ApplicationForm } from './ApplicationForm'

export interface ApplicationDetailsProps {
  id: string | number
}

export function ApplicationDetails(props: ApplicationDetailsProps) {
  const [modalActive, setEditModalActive] = useState(false)

  const [, unquarantineApplication] = useMutation<Mutation>(gql`
    mutation unquarantineApplication(
      $id: ID!
      $input: UnquarantineApplicationInput!
    ) {
      unquarantineApplication(id: $id, input: $input) {
        id
        inQuarantine
      }
    }
  `)

  const [, revealApiKey] = useMutation<Mutation>(gql`
    mutation revealApiKey($id: ID!) {
      revealApiKey(id: $id)
    }
  `)

  const revealApiKeyAlert = async () => {
    const apiKey = (await revealApiKey({ id: props.id }))?.data?.revealApiKey
    if (!apiKey) {
      showErrorAlertForMessage('Unable to show api key.')
    } else {
      showInfoAlert('Api key: ' + apiKey)
    }
  }

  const unquarantinePrompt = async () => {
    const reason = window.prompt('Reason for unquarantining')
    if (reason) {
      await unquarantineApplication({ id: props.id, input: { reason } })
    }
  }

  const [{ data, error }] = useQuery<Query>({
    query: gql`
      query applicationDetails($id: ID!) {
        application(id: $id) {
          id
          name
          description
          inQuarantine
          quarantineRule
          quarantineReason
          createdAt
          updatedAt
          createdBy
          updatedBy
        }
      }
    `,
    variables: { id: props.id },
    context: useMemo(
      () => ({
        additionalTypenames: ['Application'],
      }),
      []
    ),
  })

  if (error?.message) {
    return <ErrorMessage error={error} />
  }

  if (!data) {
    return <LoadingScreen />
  }

  return (
    <>
      <Grid>
        <Grid.Col span="auto">
          <Title order={2}>{data?.application?.name}</Title>
          {data?.application?.inQuarantine ? (
            <Badge color="orange">In Quarantine</Badge>
          ) : (
            <></>
          )}
        </Grid.Col>
        <Grid.Col span="content">
          <Menu shadow="md" width={200}>
            <Menu.Target>
              <Button leftIcon={<IconChevronDown size="1rem" />}>
                Actions
              </Button>
            </Menu.Target>
            <Menu.Dropdown>
              <Menu.Item
                icon={<IconSettings size={14} />}
                onClick={() => setEditModalActive(true)}
              >
                Edit
              </Menu.Item>
              <Menu.Item
                icon={<IconLockOpen size={14} />}
                onClick={() => revealApiKeyAlert()}
              >
                Show API Key
              </Menu.Item>
            </Menu.Dropdown>
          </Menu>
        </Grid.Col>
      </Grid>

      <SummaryBar items={createAuditItems(data?.application)} />

      <p>{data?.application?.description}</p>

      {data?.application?.inQuarantine ? (
        <>
          <DetailsTable>
            <tbody>
              <tr>
                <td>
                  <b>Rule</b>
                </td>
                <td>{data?.application?.quarantineRule}</td>
              </tr>
              <tr>
                <td>
                  <b>Reason</b>
                </td>
                <td>{data?.application?.quarantineReason}</td>
              </tr>
            </tbody>
          </DetailsTable>

          <Button type="button" onClick={() => unquarantinePrompt()}>
            Unquarantine
          </Button>
        </>
      ) : (
        <></>
      )}

      <Modal
        transitionProps={{ duration: 0 }}
        opened={modalActive}
        title="Edit Application"
        onClose={() => setEditModalActive(false)}
      >
        <ApplicationForm
          id={props.id}
          onSave={() => setEditModalActive(false)}
          onCancel={() => setEditModalActive(false)}
        />
      </Modal>
    </>
  )
}
