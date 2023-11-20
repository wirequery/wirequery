// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { ErrorMessage } from '@components/shared/ErrorMessage'
import { LoadingScreen } from '@components/shared/LoadingScreen'
import { Mutation, Query } from '@generated/graphql'
import { showErrorAlert, showInfoAlert } from '@lib/alert'
import { Button, Group, MultiSelect, TextInput } from '@mantine/core'
import { useForm } from '@mantine/form'
import { useEffect, useMemo } from 'react'
import { gql, OperationResult, useMutation, useQuery } from 'urql'

export interface RoleFormProps {
  id?: string | number
  onSave: () => void
  onCancel: () => void
}

interface RoleFormData {
  name: string | undefined
  authorisationNames: string[] | undefined
}

export function RoleForm(props: RoleFormProps) {
  const [, executeCreateMutation] = useMutation<Mutation>(gql`
    mutation createRole($input: CreateRoleInput!) {
      createRole(input: $input) {
        id
      }
    }
  `)

  const [, executeUpdateMutation] = useMutation<Mutation>(gql`
    mutation updateRole($id: ID!, $input: UpdateRoleInput!) {
      updateRole(id: $id, input: $input) {
        id
      }
    }
  `)

  function handleMutationResult(result: OperationResult<Mutation>) {
    if (result.error) {
      showErrorAlert(result.error)
    }
    return !result.error
  }

  const doSubmit = (formData: RoleFormData) => {
    if (props.id) {
      executeUpdateMutation({ id: props.id, input: formData })
        .then(
          (result) =>
            handleMutationResult(result) &&
            showInfoAlert('Role saved') &&
            props.onSave()
        )
        .catch(showErrorAlert)
    } else {
      executeCreateMutation({ input: formData })
        .then(
          (result) =>
            handleMutationResult(result) &&
            showInfoAlert('Role saved') &&
            props.onSave()
        )
        .catch(showErrorAlert)
    }
  }

  const [{ data, error, fetching }] = useQuery<Query>({
    query: gql`
      query roleForm($id: ID!) {
        role(id: $id) {
          id
          name
          authorisations {
            name
          }
        }
      }
    `,
    pause: !props.id,
    variables: { id: props.id },
    context: useMemo(
      () => ({
        additionalTypenames: ['Role'],
      }),
      []
    ),
  })

  const [authorisations] = useQuery<Query>({
    query: gql`
      query authorisationsOptions {
        authorisations {
          name
          label
          description
        }
      }
    `,
    context: useMemo(
      () => ({
        additionalTypenames: ['Authorisation'],
      }),
      []
    ),
  })

  const form = useForm({
    initialValues: {
      name: '',
      authorisationNames: [],
    },

    validate: {
      name: (value) => (value?.length !== 0 ? null : 'No name set'),
    },
  })

  useEffect(() => {
    if (!fetching && !error && data?.role) {
      form.setValues({
        name: data.role.name,
        authorisationNames: data.role.authorisations.map((a) => a.name) as any,
      })
      form.resetDirty()
    }
  }, [fetching])

  if (fetching) {
    return <LoadingScreen />
  }

  if (error) {
    return <ErrorMessage error={error} />
  }

  return (
    <>
      <form role="form" onSubmit={form.onSubmit(doSubmit as any)}>
        <TextInput
          name="name"
          label="Name"
          required
          {...form.getInputProps('name')}
        />
        <MultiSelect
          label="Authorisations"
          description="Zero or more group authorisations"
          placeholder="- Pick one or more authorisations or leave empty -"
          name="authorisations"
          data={
            authorisations.data?.authorisations?.map((a) => ({
              key: a.name,
              value: a.name,
              label: a.label,
            })) ?? []
          }
          searchable
          {...form.getInputProps('authorisationNames')}
          value={form.values.authorisationNames?.filter((r) => r !== '') ?? []}
          onChange={(value) => {
            form.setFieldValue('authorisationNames', value as any)
          }}
        />
        <Group style={{ paddingTop: 15 }}>
          <Button type="submit">Save</Button>
          <Button type="button" variant="outline" onClick={props.onCancel}>
            Cancel
          </Button>
        </Group>
      </form>
    </>
  )
}
