// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { Item } from '@components/shared/Item'
import { ItemList } from '@components/shared/ItemList'
import { Mutation, Query } from '@generated/graphql'
import { createAuditItems } from '@lib/audit'
import { IconTemplate } from '@tabler/icons-react'
import { useMemo } from 'react'
import { gql, useMutation, useQuery } from 'urql'

export interface TemplateProps {
  onCreateTemplate?: () => void
}

export function TemplateList(props: TemplateProps) {
  const [, executeDeleteMutation] = useMutation<Mutation>(gql`
    mutation deleteTemplate($id: ID!) {
      deleteTemplate(id: $id)
    }
  `)

  const handleDelete = (id: string) => {
    confirm('Are you sure you want to delete Template?') &&
      executeDeleteMutation(
        { id },
        { additionalTypenames: ['Template'] }
      ).catch((reason) => alert(`Deleting Template failed. Reason: ${reason}`))
  }

  const [{ data, error, fetching }] = useQuery<Query>({
    query: gql`
      query templateList {
        templates {
          id
          name
          description
          fields {
            label
          }
          allowUserInitiation
          createdAt
          updatedAt
          createdBy
          updatedBy
        }
      }
    `,
    context: useMemo(
      () => ({
        additionalTypenames: ['Template'],
      }),
      []
    ),
  })

  return (
    <ItemList
      fetching={fetching}
      error={error}
      filters={[
        { label: 'Name', field: 'name', type: 'text' },
        { label: 'Description', field: 'description', type: 'text' },
      ]}
      emptyIcon={IconTemplate}
      emptyTitle="No templates found"
      emptyDescription="No templates were found."
      emptyButtonText="Create new template"
      emptyOnClick={props.onCreateTemplate}
      data={[...(data?.templates ?? [])].sort((a, b) =>
        a.name.localeCompare(b.name)
      )}
    >
      {(row) => (
        <Item
          key={row.id}
          label={row.name}
          onDelete={() => handleDelete(row.id)}
          href={`/templates/${row.id}`}
          description={row.description}
          items={[
            ...(row.fields?.map((f) => f.label) ?? []),
            row.allowUserInitiation ? 'User Initiation Allowed' : undefined,
            ...createAuditItems(row),
          ]}
        />
      )}
    </ItemList>
  )
}
