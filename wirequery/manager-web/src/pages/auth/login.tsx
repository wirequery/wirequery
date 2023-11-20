// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { Login } from '@components/shared/app/auth/Login'
import { showErrorAlertForMessage } from '@lib/alert'
import { ResetUrqlClientContext } from '@lib/reset-urql-client-context'
import { Container, Paper, Title } from '@mantine/core'
import { useRouter } from 'next/router'

export default function LoginPage() {
  const router = useRouter()
  return (
    <Container size={420} my={40}>
      <Title
        align="center"
        sx={(theme) => ({
          fontFamily: `Greycliff CF, ${theme.fontFamily}`,
          fontWeight: 900,
        })}
      >
        Login
      </Title>
      <Paper withBorder shadow="md" p={30} mt={30} radius="md">
        <ResetUrqlClientContext.Consumer>
          {(resetUrqlClient) => (
            <Login
              onLogin={(success) => {
                if (success) {
                  resetUrqlClient?.()
                  router.push('/')
                } else {
                  showErrorAlertForMessage(
                    'Could not login. Please check your credentials.'
                  )
                }
              }}
            />
          )}
        </ResetUrqlClientContext.Consumer>
      </Paper>
    </Container>
  )
}
