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
import {
  Box,
  Button,
  Center,
  Checkbox,
  Group,
  Select,
  Textarea,
  TextInput,
} from '@mantine/core'
import { useForm } from '@mantine/form'
import { IconGripVertical } from '@tabler/icons-react'
import { useEffect, useMemo } from 'react'
import { DragDropContext, Draggable, Droppable } from 'react-beautiful-dnd'
import { gql, OperationResult, useMutation, useQuery } from 'urql'

export interface TemplateFormProps {
  id?: string | number
  onSave: () => void
  onCancel: () => void
}

interface TemplateFormData {
  name: string | undefined
  description: string | undefined
  fields: any | undefined
  nameTemplate: string | undefined
  descriptionTemplate: string | undefined
  allowUserInitiation: boolean | undefined
}

export function TemplateForm(props: TemplateFormProps) {
  const [, executeCreateMutation] = useMutation<Mutation>(gql`
    mutation createTemplate($input: CreateTemplateInput!) {
      createTemplate(input: $input) {
        id
      }
    }
  `)

  const [, executeUpdateMutation] = useMutation<Mutation>(gql`
    mutation updateTemplate($id: ID!, $input: UpdateTemplateInput!) {
      updateTemplate(id: $id, input: $input) {
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

  const doSubmit = (formData: TemplateFormData) => {
    if (props.id) {
      executeUpdateMutation({ id: props.id, input: formData })
        .then(
          (result) =>
            handleMutationResult(result) &&
            showInfoAlert('Template saved') &&
            props.onSave()
        )
        .catch(showErrorAlert)
    } else {
      executeCreateMutation({ input: formData })
        .then(
          (result) =>
            handleMutationResult(result) &&
            showInfoAlert('Template saved') &&
            props.onSave()
        )
        .catch(showErrorAlert)
    }
  }

  const [{ data, error, fetching }] = useQuery<Query>({
    query: gql`
      query templateForm($id: ID!) {
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
        }
      }
    `,
    pause: !props.id,
    variables: { id: props.id },
    context: useMemo(
      () => ({
        additionalTypenames: ['Template'],
      }),
      []
    ),
  })

  const form = useForm({
    initialValues: {
      name: '',
      description: '',
      fields: [],
      nameTemplate: '',
      descriptionTemplate: '',
      allowUserInitiation: false,
    },

    validate: {
      name: (value) => (value?.length !== 0 ? null : 'No name set'),
    },
  })

  useEffect(() => {
    if (!fetching && !error && data?.template) {
      form.setValues({
        name: data.template.name,
        description: data.template.description,
        fields:
          data.template.fields?.map((field) => {
            const newField = { ...field }
            delete newField.__typename
            return newField
          }) ?? [],
        nameTemplate: data.template.nameTemplate,
        descriptionTemplate: data.template.descriptionTemplate,
        allowUserInitiation: data.template.allowUserInitiation,
      } as any)
      form.resetDirty()
    }
  }, [fetching])

  if (fetching) {
    return <LoadingScreen />
  }

  if (error) {
    return <ErrorMessage error={error} />
  }

  const fields = form.values.fields.map((_, index) => (
    <Draggable key={index} index={index} draggableId={index.toString()}>
      {(provided: any, snapshot: any) => {
        if (snapshot.isDragging) {
          provided.draggableProps.style.left =
            provided.draggableProps.style.offsetLeft
          provided.draggableProps.style.top =
            provided.draggableProps.style.offsetTop
        }
        return (
          <Group ref={provided.innerRef} mt="xs" {...provided.draggableProps}>
            <Center {...provided.dragHandleProps}>
              <IconGripVertical size="1.2rem" />
            </Center>
            <TextInput
              placeholder="Label, e.g. Customer Id"
              {...form.getInputProps(`fields.${index}.label`)}
            />
            <TextInput
              placeholder="Key, e.g. customerId"
              {...form.getInputProps(`fields.${index}.key`)}
            />
            <Select
              data={[
                { value: 'TEXT', label: 'Text' },
                { value: 'TEXTAREA', label: 'Text Area' },
                { value: 'INTEGER', label: 'Integer Number' },
                { value: 'FLOAT', label: 'Floating Point Number' },
                { value: 'BOOLEAN', label: 'Boolean' },
              ]}
              {...form.getInputProps(`fields.${index}.type`)}
            />
          </Group>
        )
      }}
    </Draggable>
  ))

  return (
    <>
      <form role="form" onSubmit={form.onSubmit(doSubmit)}>
        <TextInput
          name="name"
          label="Name"
          required
          {...form.getInputProps('name')}
        />
        <Textarea
          name="description"
          label="Description"
          {...form.getInputProps('description')}
        />

        <Box maw={800}>
          <DragDropContext
            onDragEnd={({ destination, source }: any) => {
              if (!destination) {
                form.removeListItem('fields', source.index)
              } else {
                form.reorderListItem('fields', {
                  from: source.index,
                  to: destination.index,
                })
              }
            }}
          >
            <Droppable droppableId="dnd-list" direction="vertical">
              {(provided: any) => (
                <div {...provided.droppableProps} ref={provided.innerRef}>
                  {fields}
                  {provided.placeholder}
                </div>
              )}
            </Droppable>
          </DragDropContext>

          <Group mt="md">
            <Button
              onClick={() =>
                form.insertListItem('fields', { key: '', label: '' })
              }
            >
              Add field
            </Button>
          </Group>
        </Box>

        <TextInput
          name="nameTemplate"
          label="Name Template "
          required
          {...form.getInputProps('nameTemplate')}
        />
        <Textarea
          name="descriptionTemplate"
          label="Description Template"
          {...form.getInputProps('descriptionTemplate')}
        />
        <Checkbox
          mt={'sm'}
          name="allowUserInitiation"
          label="Allow User Initiation"
          {...form.getInputProps('allowUserInitiation', { type: 'checkbox' })}
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
