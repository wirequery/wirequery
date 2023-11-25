// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { Mutation } from '@generated/graphql'
import { showErrorAlert, showInfoAlert } from '@lib/alert'
import {
  Button,
  Group,
  NativeSelect,
  NumberInput,
  Textarea,
  TextInput,
} from '@mantine/core'
import { useForm } from '@mantine/form'
import { gql, OperationResult, useMutation } from 'urql'

export interface TemplateQueryFormProps {
  templateId: string
  onSave: (id: string | number) => void
  onCancel: () => void
}

interface TemplateQueryFormData {
  nameTemplate: string | undefined
  queryTemplate: string | undefined
  type: string | undefined
  queryLimit: number | undefined
}

export function TemplateQueryForm(props: TemplateQueryFormProps) {
  const [, executeCreateMutation] = useMutation<Mutation>(gql`
    mutation createTemplateQuery($input: CreateTemplateQueryInput!) {
      createTemplateQuery(input: $input) {
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

  const doSubmit = (formData: TemplateQueryFormData) => {
    const formDataWithRelations = {
      ...formData,
      templateId: parseInt(props.templateId),
    }
    executeCreateMutation({ input: formDataWithRelations })
      .then(
        (result) =>
          handleMutationResult(result) &&
          showInfoAlert('Query Template saved') &&
          props.onSave(result?.data?.createTemplateQuery?.id as any)
      )
      .catch(showErrorAlert)
  }

  const form = useForm<TemplateQueryFormData>({
    initialValues: {
      nameTemplate: '',
      queryTemplate: '',
      type: 'QUERY',
      queryLimit: 100,
    },
    validate: {},
  })

  return (
    <form role="form" onSubmit={form.onSubmit(doSubmit)}>
      <TextInput
        label="Name Template"
        placeholder="Name Template"
        {...form.getInputProps(`nameTemplate`)}
      />
      <Textarea
        label="Query Template"
        placeholder="Query Template"
        {...form.getInputProps('queryTemplate')}
      />
      <NativeSelect
        label="Type"
        {...form.getInputProps('type')}
        data={[
          { label: 'Query', value: 'QUERY' },
          { label: 'Query With Tracing', value: 'QUERY_WITH_TRACING' },
        ]}
      />
      <NumberInput
        label="Query Limit"
        description="Max number of query results"
        min={1}
        max={999}
        required
        type="number"
        {...form.getInputProps('queryLimit')}
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
