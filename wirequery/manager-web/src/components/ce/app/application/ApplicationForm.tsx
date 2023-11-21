// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { gql, OperationResult, useMutation, useQuery } from 'urql'
import { useMemo } from 'react'
import { Mutation, Query } from '@generated/graphql'
import { Button, Group, Textarea, TextInput } from '@mantine/core'
import { LoadingScreen } from '@components/shared/LoadingScreen'
import { ErrorMessage } from '@components/shared/ErrorMessage'
import { useForm } from '@mantine/form'
import { showErrorAlert, showInfoAlert } from '@lib/alert'
import { useEffect } from 'react'

export interface ApplicationFormProps {
  id?: string | number
  onSave: () => void
  onCancel: () => void
}

interface ApplicationFormData {
  name: string | undefined
  description: string | undefined
}

export function ApplicationForm(props: ApplicationFormProps) {
  const [, executeCreateMutation] = useMutation<Mutation>(gql`
    mutation createApplication($input: CreateApplicationInput!) {
      createApplication(input: $input) {
        id
      }
    }
  `)

  const [, executeUpdateMutation] = useMutation<Mutation>(gql`
    mutation updateApplication($id: ID!, $input: UpdateApplicationInput!) {
      updateApplication(id: $id, input: $input) {
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

  const doSubmit = (formData: ApplicationFormData) => {
    if (props.id) {
      if (formData.name) {
        formData = { ...formData } // Ensure doSubmit does not alter its input args.
        delete formData.name
      }
      executeUpdateMutation({ id: props.id, input: formData })
        .then(
          (result) =>
            handleMutationResult(result) &&
            showInfoAlert('Application saved') &&
            props.onSave()
        )
        .catch(showErrorAlert)
    } else {
      executeCreateMutation({ input: formData })
        .then(
          (result) =>
            handleMutationResult(result) &&
            showInfoAlert('Application saved') &&
            props.onSave()
        )
        .catch(showErrorAlert)
    }
  }

  const [{ data, error, fetching }] = useQuery<Query>({
    query: gql`
      query applicationForm($id: ID!) {
        application(id: $id) {
          id
          name
          description
        }
      }
    `,
    pause: !props.id,
    variables: { id: props.id },
    context: useMemo(
      () => ({
        additionalTypenames: ['Application'],
      }),
      []
    ),
  })

  const form = useForm({
    initialValues: {
      name: '',
      description: '',
    } as ApplicationFormData,

    validate: props.id
      ? {}
      : {
          name: (value) =>
            value?.length !== 0
              ? value?.includes(' ')
                ? 'Spaces are not allowed'
                : null
              : 'No name set',
        },
  })

  useEffect(() => {
    if (!fetching && !error && data?.application) {
      form.setValues({
        name: data.application.name,
        description: data.application.description,
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
    <form role="form" onSubmit={form.onSubmit(doSubmit)}>
      {props.id ? (
        <></>
      ) : (
        <TextInput
          name="name"
          label="Name"
          required
          {...form.getInputProps('name')}
        />
      )}
      <Textarea
        name="description"
        label="Description"
        {...form.getInputProps('description')}
      />
      <Group style={{ paddingTop: 15 }}>
        <Button type="submit">Save</Button>
        <Button type="button" variant="outline" onClick={props.onCancel}>
          Cancel
        </Button>
      </Group>
    </form>
  )
}
