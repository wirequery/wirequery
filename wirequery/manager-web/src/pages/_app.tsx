import { Login } from '@components/app/auth/Login'
import { Query, User } from '@generated/graphql'
import { AuthorisationsContext } from '@lib/authorisations-context'
import { CurrentUserContext } from '@lib/current-user-context'
import { ResetUrqlClientContext } from '@lib/reset-urql-client-context'
import {
  ColorScheme,
  ColorSchemeProvider,
  MantineProvider,
  Modal,
} from '@mantine/core'
import { useLocalStorage } from '@mantine/hooks'
import { Notifications } from '@mantine/notifications'
import { createClient as createSseClient } from 'graphql-sse'
import NextAdapterPages from 'next-query-params/pages'
import { withUrqlClient } from 'next-urql'
import type { AppProps } from 'next/app'
import Head from 'next/head'
import { useMemo } from 'react'
import {
  cacheExchange,
  dedupExchange,
  fetchExchange,
  gql,
  mapExchange,
  subscriptionExchange,
  useQuery,
} from 'urql'
import { QueryParamProvider } from 'use-query-params'
import '../styles/globals.css'

function App({
  Component,
  pageProps,
  resetUrqlClient,
}: AppProps & { resetUrqlClient: () => void }) {
  const [colorScheme, setColorScheme] = useLocalStorage<ColorScheme>({
    key: 'mantine-color-scheme',
    defaultValue: 'light',
    getInitialValueInEffect: true,
  })

  const toggleColorScheme = (value?: ColorScheme) =>
    setColorScheme(value || (colorScheme === 'dark' ? 'light' : 'dark'))

  const [{ data, fetching }] = useQuery<Query>({
    query: gql`
      query currentUser {
        currentUser {
          id
          username
          enabled
          authorisationNames
        }
      }
    `,
    context: useMemo(
      () => ({
        additionalTypenames: ['User'],
      }),
      []
    ),
  })

  const authorisations = (user: User) => {
    const result: { [key: string]: boolean } = {}
    user?.authorisationNames?.forEach((authorisation: string) => {
      result[authorisation] = true
    })
    return result
  }

  return (
    <>
      <Head>
        <title>WireQuery</title>
        <meta name="description" content="WireQuery" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <link rel="icon" href="/favicon.ico" />
      </Head>
      <QueryParamProvider adapter={NextAdapterPages}>
        <ColorSchemeProvider
          colorScheme={colorScheme}
          toggleColorScheme={toggleColorScheme}
        >
          <CurrentUserContext.Provider value={data?.currentUser as User}>
            <ResetUrqlClientContext.Provider value={resetUrqlClient}>
              <MantineProvider
                theme={{
                  colorScheme,
                  primaryColor: 'purple',
                  primaryShade: 4,
                  defaultRadius: 0,
                  spacing: { xs: '.4rem' },
                  globalStyles: (theme) => ({
                    body: {
                      backgroundColor:
                        theme.colorScheme === 'dark'
                          ? theme.colors.dark[8]
                          : theme.colors.white,
                    },
                  }),
                  colors: {
                    red: [
                      '#FEE6EF',
                      '#FDB9D2',
                      '#FC8DB5',
                      '#FB6099',
                      '#FA337C',
                      '#F9065F',
                      '#C7054C',
                      '#950439',
                      '#640226',
                      '#320113',
                    ],
                    purple: [
                      '#EDEBF9',
                      '#CCC7F0',
                      '#ABA3E6',
                      '#8B7FDC',
                      '#6A5BD2',
                      '#4937C8',
                      '#3B2CA0',
                      '#2C2178',
                      '#1D1650',
                      '#0F0B28',
                    ],
                    blue: [
                      '#EAEEFA',
                      '#C5D0F2',
                      '#9FB1EA',
                      '#7993E2',
                      '#5474D9',
                      '#2E56D1',
                      '#2545A7',
                      '#1C347D',
                      '#122254',
                      '#09112A',
                    ],
                  },
                }}
                withGlobalStyles
                withNormalizeCSS
              >
                <Notifications />
                {!fetching && data?.currentUser ? (
                  <AuthorisationsContext.Provider
                    value={authorisations(data?.currentUser)}
                  >
                    <Component {...pageProps} />
                  </AuthorisationsContext.Provider>
                ) : (
                  <></>
                )}
                {!fetching && !data?.currentUser ? (
                  <Modal
                    title="Login"
                    opened
                    withCloseButton={false}
                    onClose={undefined as any}
                  >
                    <Login
                      onLogin={() => {
                        resetUrqlClient()
                      }}
                    />
                  </Modal>
                ) : (
                  <></>
                )}
              </MantineProvider>
            </ResetUrqlClientContext.Provider>
          </CurrentUserContext.Provider>
        </ColorSchemeProvider>
      </QueryParamProvider>
    </>
  )
}

export default withUrqlClient((ssrExchange) => {
  const exchanges = [
    dedupExchange,
    cacheExchange,
    mapExchange({}),
    ssrExchange,
    fetchExchange,
  ]
  if (typeof window !== 'undefined') {
    const sseClient = createSseClient({
      url: '/subscriptions',
      credentials: 'include',
    })
    exchanges.push(
      subscriptionExchange({
        forwardSubscription: (operation) => ({
          subscribe: (sink) => ({
            unsubscribe: sseClient.subscribe(
              {
                query: operation.query as string,
                variables: operation.variables,
              },
              sink
            ),
          }),
        }),
      })
    )
  }
  return {
    url: '/graphql',
    exchanges,
    fetchOptions: {
      credentials: 'include',
    },
    requestPolicy: 'cache-and-network',
  }
})(App)
