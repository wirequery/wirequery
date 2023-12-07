// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { Mutation } from '@generated/graphql'
import {
  showErrorAlert,
  showErrorAlertForMessage,
  showInfoAlert,
} from '@lib/alert'
import { Button, Group, PasswordInput } from '@mantine/core'
import { useForm } from '@mantine/form'
import React from 'react'
import { gql, OperationResult, useMutation } from 'urql'

export interface CurrentUserSettingsProps {
  onSave: () => void
}

interface CurrentUserSettingsData {
  currentPassword: string
  newPassword: string | undefined
  confirmPassword: string | undefined
}

export function CurrentUserSettings(props: CurrentUserSettingsProps) {
  const [, executeUpdateMutation] = useMutation<Mutation>(gql`
    mutation updateCurrentUser($input: UpdateCurrentUserInput!) {
      updateCurrentUser(input: $input) {
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

  const doSubmit = (formData: CurrentUserSettingsData) => {
    if (formData.newPassword !== '' || formData.confirmPassword !== '') {
      if (formData.newPassword !== formData.confirmPassword) {
        showErrorAlertForMessage('Passwords did not match. Please try again.')
        return
      }
    }
    executeUpdateMutation({ input: { password: formData.newPassword, currentPassword: formData.currentPassword } })
      .then(
        (result) =>
          handleMutationResult(result) &&
          props.onSave &&
          showInfoAlert('Settings updated') &&
          props.onSave()
      )
      .catch(showErrorAlert)
  }

  const form = useForm<CurrentUserSettingsData>({
    initialValues: {
      currentPassword: '',
      newPassword: '',
      confirmPassword: '',
    },
  })

  return (
    <>
      <form role="form" onSubmit={form.onSubmit(doSubmit)}>
        <PasswordInput
          label="Current Password"
          {...form.getInputProps('currentPassword')}
        />
        <PasswordInput
          label="New Password"
          {...form.getInputProps('newPassword')}
        />
        <PasswordInput
          label="Confirm Password"
          {...form.getInputProps('confirmPassword')}
        />
        <Group style={{ paddingTop: 15 }}>
          <Button type="submit">Save</Button>
        </Group>
      </form>
    </>
  )
}
