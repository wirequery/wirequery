import { ErrorMessage } from '@components/shared/ErrorMessage'
import { LoadingScreen } from '@components/shared/LoadingScreen'
import { Mutation, Query } from '@generated/graphql'
import { showErrorAlert, showInfoAlert } from '@lib/alert'
import {
  Button,
  Checkbox,
  Group,
  MultiSelect,
  PasswordInput,
  TextInput,
} from '@mantine/core'
import { useForm } from '@mantine/form'
import React, { useEffect, useMemo } from 'react'
import { gql, OperationResult, useMutation, useQuery } from 'urql'

export interface UserFormProps {
  id?: string | number
  onSave: () => void
  onCancel: () => void
}

interface UserFormData {
  username: string | undefined
  password: string | undefined
  enabled: boolean | undefined
  roles: string | undefined
}

function keepUpdateFields(formData: UserFormData) {
  const result = { ...formData }
  delete result.username
  return result
}

export function UserForm(props: UserFormProps) {
  const [, executeCreateMutation] = useMutation<Mutation>(gql`
    mutation register($input: RegisterInput!) {
      register(input: $input) {
        id
      }
    }
  `)

  const [, executeUpdateMutation] = useMutation<Mutation>(gql`
    mutation updateUser($id: ID!, $input: UpdateUserInput!) {
      updateUser(id: $id, input: $input) {
        id
      }
    }
  `)

  const [roles] = useQuery<Query>({
    query: gql`
      query userFormRoles {
        roles {
          id
          name
        }
      }
    `,
    context: useMemo(
      () => ({
        additionalTypenames: ['QuarantineGroup'],
      }),
      []
    ),
  })

  function handleMutationResult(result: OperationResult<Mutation>) {
    if (result.error) {
      showErrorAlert(result.error)
    }
    return !result.error
  }

  const doSubmit = (formData: UserFormData) => {
    if (props.id) {
      executeUpdateMutation({ id: props.id, input: keepUpdateFields(formData) })
        .then(
          (result) =>
            handleMutationResult(result) &&
            showInfoAlert('User saved') &&
            props.onSave()
        )
        .catch(showErrorAlert)
    } else {
      executeCreateMutation({ input: formData })
        .then(
          (result) =>
            handleMutationResult(result) &&
            showInfoAlert('User saved') &&
            props.onSave()
        )
        .catch(showErrorAlert)
    }
  }

  const [{ data, error, fetching }] = useQuery<Query>({
    query: gql`
      query userForm($id: ID!) {
        user(id: $id) {
          id
          username
          enabled
          roles
        }
      }
    `,
    pause: !props.id,
    variables: { id: props.id },
    context: useMemo(
      () => ({
        additionalTypenames: ['User'],
      }),
      []
    ),
  })

  const form = useForm<UserFormData>({
    initialValues: {
      username: '',
      password: '',
      enabled: true,
      roles: '',
    },

    validate: props.id
      ? {}
      : {
          username: (value) =>
            !value || value?.length < 6
              ? 'Username must be at least 6 characters long'
              : null,
          password: (value) =>
            !value || value?.length < 6
              ? 'Password must be at least 6 characters long'
              : null,
        },
  })

  useEffect(() => {
    if (!fetching && !error && data?.user) {
      form.setValues({
        username: data?.user?.username,
        password: undefined,
        enabled: data?.user?.enabled,
        roles: data?.user?.roles,
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
      <form role="form" onSubmit={form.onSubmit(doSubmit)}>
        {props.id ? (
          <></>
        ) : (
          <TextInput
            label="Username"
            name="username"
            {...form.getInputProps('username')}
          />
        )}
        {props.id ? (
          <></>
        ) : (
          <PasswordInput
            label="Password"
            name="password"
            {...form.getInputProps('password')}
          />
        )}
        <Checkbox
          mt={5}
          label="Enabled"
          name="enabled"
          {...form.getInputProps('enabled', { type: 'checkbox' })}
        />
        <MultiSelect
          label="System Role(s)"
          description="Zero or more 'specialized' system-wide roles"
          placeholder="- Pick one or more roles or leave empty -"
          name="roles"
          data={roles.data?.roles?.map((r) => r.name) ?? []}
          searchable
          {...form.getInputProps('roles')}
          value={form.values.roles?.split(',').filter((r) => r !== '') ?? []}
          onChange={(value) => {
            form.setFieldValue('roles', value.join(','))
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
