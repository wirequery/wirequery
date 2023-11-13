import { Mutation, Query, Template } from '@generated/graphql'
import { showErrorAlert, showInfoAlert } from '@lib/alert'
import {
  Button,
  Checkbox,
  Group,
  NativeSelect,
  NumberInput,
  Textarea,
  TextInput,
  UnstyledButton,
} from '@mantine/core'
import { DateTimePicker } from '@mantine/dates'
import { useForm } from '@mantine/form'
import { useMemo } from 'react'
import { gql, OperationResult, useMutation, useQuery } from 'urql'

export interface SessionFormProps {
  templateId?: string
  onSave: (id: string | undefined) => void
  onCancel: () => void
}

interface SessionFormData {
  templateId?: string | number
  variables: any
}

export function SessionForm(props: SessionFormProps) {
  const [, executeCreateMutation] = useMutation<Mutation>(gql`
    mutation createSession($input: CreateSessionInput!) {
      createSession(input: $input) {
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

  const [templatesQueryResult] = useQuery<Query>({
    query: gql`
      query templatesForSessionOptions {
        templates {
          id
          name
          fields {
            key
            label
            type
          }
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

  const form = useForm({
    initialValues: {
      templateId:
        props.templateId !== undefined ? parseInt(props.templateId) : undefined,
      variables: {},
    } as SessionFormData,

    validate: {
      templateId: (value) => (value ? undefined : 'Template not set'),
    },
  })

  const selectedTemplate = templatesQueryResult?.data?.templates?.filter(
    (t) => parseInt(t.id) === parseInt('' + form.values['templateId'])
  )?.[0]

  const doSubmit = (formData: SessionFormData) => {
    const variables =
      selectedTemplate?.fields?.map((field) => ({
        key: field.key,
        value: formData.variables[field.key] ?? '',
      })) ?? []
    const formDataWithRelations = { ...formData, variables }
    if (props.templateId) {
      formDataWithRelations.templateId = props.templateId
    }

    formDataWithRelations.templateId = formDataWithRelations.templateId
      ? parseInt('' + formDataWithRelations.templateId)
      : undefined

    executeCreateMutation({ input: formDataWithRelations })
      .then(
        (result) =>
          handleMutationResult(result) &&
          showInfoAlert('Session saved') &&
          props.onSave(result.data?.createSession?.id)
      )
      .catch(showErrorAlert)
  }

  const setEndDateInMinutes = (hours: number) => {
    const newDateTime = new Date()
    newDateTime.setTime(newDateTime.getTime() + 60 * 1000 * hours)
    form.setFieldValue('endDate', newDateTime as any)
  }

  const setEndDateInHours = (hours: number) => {
    const newDateTime = new Date()
    newDateTime.setTime(newDateTime.getTime() + 60 * 60 * 1000 * hours)
    form.setFieldValue('endDate', newDateTime as any)
  }

  return (
    <form role="form" onSubmit={form.onSubmit(doSubmit)}>
      {!props.templateId ? (
        <NativeSelect
          required
          name="templateId"
          label="Template"
          data={[
            { label: '- Pick a template -', value: '' },
            ...(templatesQueryResult?.data?.templates?.map(
              (template: Template) => ({
                key: template.name,
                value: template.id,
                label: template.name,
              })
            ) ?? []),
          ]}
          {...form.getInputProps('templateId')}
        ></NativeSelect>
      ) : undefined}

      {selectedTemplate?.fields?.map((f) => {
        if (f.type === 'TEXT') {
          return (
            <TextInput
              key={f.key}
              mb={3}
              name={f.key}
              label={f.label}
              required
              onChange={(e) =>
                form.setFieldValue(`variables.${f.key}`, e.target.value)
              }
            />
          )
        } else if (f.type === 'TEXTAREA') {
          return (
            <Textarea
              key={f.key}
              mb={3}
              name={f.key}
              label={f.label}
              required
              onChange={(e) =>
                form.setFieldValue(`variables.${f.key}`, e.target.value)
              }
            />
          )
        } else if (f.type === 'INTEGER') {
          return (
            <NumberInput
              key={f.key}
              mb={3}
              name={f.key}
              label={f.label}
              required
              onChange={(value) =>
                form.setFieldValue(`variables.${f.key}`, value)
              }
            />
          )
        } else if (f.type === 'FLOAT') {
          return (
            <NumberInput
              key={f.key}
              mb={3}
              name={f.key}
              label={f.label}
              precision={2}
              required
              onChange={(value) =>
                form.setFieldValue(`variables.${f.key}`, value)
              }
            />
          )
        } else if (f.type === 'BOOLEAN') {
          return (
            <Checkbox
              key={f.key}
              mb={3}
              name={f.key}
              label={f.label}
              onChange={(e) =>
                form.setFieldValue(`variables.${f.key}`, e.target.checked)
              }
            />
          )
        }
      })}

      <DateTimePicker
        required
        dropdownType="modal"
        label="Ends at"
        description="No end moment if left empty."
        clearable
        {...form.getInputProps('endDate')}
      />
      <Group>
        <UnstyledButton onClick={() => setEndDateInMinutes(3)}>
          3m
        </UnstyledButton>
        <UnstyledButton onClick={() => setEndDateInMinutes(10)}>
          10m
        </UnstyledButton>
        <UnstyledButton onClick={() => setEndDateInHours(1)}>1h</UnstyledButton>
        <UnstyledButton onClick={() => setEndDateInHours(12)}>
          12h
        </UnstyledButton>
        <UnstyledButton onClick={() => setEndDateInHours(24)}>
          24h
        </UnstyledButton>
      </Group>

      <Group style={{ paddingTop: 15 }}>
        <Button type="submit">Save</Button>
        <Button type="button" variant="outline" onClick={props.onCancel}>
          Cancel
        </Button>
      </Group>
    </form>
  )
}
