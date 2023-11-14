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
import { Query } from '@generated/graphql'
import { createAuditItems } from '@lib/audit'
import { Button, Grid, Menu, Modal, Tabs, Title } from '@mantine/core'
import {
  IconBoxMultiple,
  IconChevronDown,
  IconSettings,
} from '@tabler/icons-react'
import { useMemo, useState } from 'react'
import { gql, useQuery } from 'urql'
import { TemplateQueryForm } from '../template-query/TemplateQueryForm'
import { TemplateQueryList } from '../template-query/TemplateQueryList'
import { TemplateForm } from './TemplateForm'

export interface TemplateDetailsProps {
  id: string | number
}

export function TemplateDetails(props: TemplateDetailsProps) {
  const [editModalActive, setEditModalActive] = useState(false)
  const [newTemplateQueryModalActive, setNewTemplateQueryModalActive] =
    useState(false)

  const [{ data, error }] = useQuery<Query>({
    query: gql`
      query templateDetails($id: ID!) {
        template(id: $id) {
          id
          name
          description
          fields {
            key
            label
            type
          }
          nameTemplate
          descriptionTemplate
          allowUserInitiation
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
        additionalTypenames: ['Template'],
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
          <Title order={2}>{data?.template?.name}</Title>
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
                icon={<IconBoxMultiple size={14} />}
                onClick={() => setNewTemplateQueryModalActive(true)}
              >
                Add Query
              </Menu.Item>
            </Menu.Dropdown>
          </Menu>
        </Grid.Col>
      </Grid>

      <SummaryBar items={createAuditItems(data?.template)} />

      <p>{data?.template?.description}</p>

      <Tabs defaultValue="queries" mt="xl">
        <Tabs.List>
          <Tabs.Tab value="queries">Queries</Tabs.Tab>
          <Tabs.Tab value="details">Details</Tabs.Tab>
        </Tabs.List>
        <Tabs.Panel value="queries">
          <TemplateQueryList templateId={'' + props.id} />
        </Tabs.Panel>
        <Tabs.Panel value="details">
          <DetailsTable>
            <tbody>
              <tr>
                <td>
                  <b>Fields</b>
                </td>
                <td>
                  <ul
                    style={{
                      paddingLeft: '1rem',
                      marginBottom: 0,
                    }}
                  >
                    {data?.template?.fields?.map((field) => (
                      <li>
                        {field.label} ({field.key}: {field.type})
                      </li>
                    ))}
                  </ul>
                </td>
              </tr>
              <tr>
                <td>
                  <b>Name template</b>
                </td>
                <td>{data?.template?.nameTemplate}</td>
              </tr>
              <tr>
                <td>
                  <b>Description template</b>
                </td>
                <td>{data?.template?.descriptionTemplate}</td>
              </tr>
              <tr>
                <td>
                  <b>Allow user initiation</b>
                </td>
                <td>{data?.template?.allowUserInitiation ? 'Yes' : 'No'}</td>
              </tr>
            </tbody>
          </DetailsTable>
        </Tabs.Panel>
      </Tabs>

      <Modal
        size="xl"
        opened={editModalActive}
        title="Edit Template"
        onClose={() => setEditModalActive(false)}
      >
        <TemplateForm
          id={props.id}
          onSave={() => setEditModalActive(false)}
          onCancel={() => setEditModalActive(false)}
        />
      </Modal>

      <Modal
        opened={newTemplateQueryModalActive}
        title="Add Query to Template"
        onClose={() => setNewTemplateQueryModalActive(false)}
      >
        <TemplateQueryForm
          templateId={'' + props.id}
          onSave={() => setNewTemplateQueryModalActive(false)}
          onCancel={() => setNewTemplateQueryModalActive(false)}
        />
      </Modal>
    </>
  )
}
