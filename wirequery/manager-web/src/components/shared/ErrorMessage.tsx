// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { Alert } from '@mantine/core'
import { IconAlertCircle } from '@tabler/icons-react'
import { CombinedError } from 'urql'

export interface ErrorMessageProps {
  error?: CombinedError
}

export const ErrorMessage = (props: ErrorMessageProps) => {
  if (props.error) {
    if (props.error.graphQLErrors.length > 0) {
      return (
        <Alert
          mt="lg"
          color="red"
          icon={<IconAlertCircle size="1rem" />}
          title="An error occurred"
        >
          {props.error.graphQLErrors[0].message}
        </Alert>
      )
    }
    if (props.error.networkError) {
      return (
        <Alert
          mt="lg"
          color="red"
          icon={<IconAlertCircle size="1rem" />}
          title="Network issues"
        >
          There were network issues while loading. Please check your internet
          connection and try again.
        </Alert>
      )
    }
  }
  return <></>
}
