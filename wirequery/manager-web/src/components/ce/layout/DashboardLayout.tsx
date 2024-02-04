// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { SessionForm } from '@components/shared/app/session/SessionForm'
import { Mutation } from '@generated/graphql'
import { AuthorisationsContext } from '@lib/authorisations-context'
import { CurrentUserContext } from '@lib/current-user-context'
import { ResetUrqlClientContext } from '@lib/reset-urql-client-context'
import {
  AppShell,
  Burger,
  Container,
  createStyles,
  getStylesRef,
  Group,
  Header,
  MediaQuery,
  Modal,
  Navbar,
  Text,
  UnstyledButton,
  useMantineTheme,
} from '@mantine/core'
import {
  IconListSearch,
  IconLogout,
  IconReportSearch,
  IconSearch,
  IconServer,
  IconSettings,
  IconTemplate,
  IconUsers,
} from '@tabler/icons-react'
import Link from 'next/link'
import { useRouter } from 'next/router'
import { ReactNode, useContext, useState } from 'react'
import { gql, useMutation } from 'urql'

const useStyles = createStyles((theme) => {
  const icon = getStylesRef('icon')
  return {
    navbar: {
      backgroundColor:
        theme.colorScheme === 'dark'
          ? theme.colors.dark[7]
          : theme.colors.gray[1],
    },

    feedbackButton: {
      backgroundColor: theme.colors?.purple?.[4],
    },

    header: {
      backgroundColor: theme.colors?.purple?.[7],
      color:
        theme.colorScheme === 'dark'
          ? theme.colors.gray[0]
          : theme.colors.gray[0],
    },

    footer: {
      borderTop: `1px solid ${
        theme.colorScheme === 'dark'
          ? theme.colors.dark[4]
          : theme.colors.gray[3]
      }`,
      paddingTop: theme.spacing.md,
    },

    navLogo: {
      ...theme.fn.focusStyles(),
      display: 'flex',
      alignItems: 'center',
      textDecoration: 'none',
      fontSize: theme.fontSizes.sm,
      color:
        theme.colorScheme === 'dark'
          ? theme.colors.gray[1]
          : theme.colors.gray[1],
      padding: `${theme.spacing.xs} 0`,
      borderRadius: theme.radius.sm,
      fontWeight: 600,
    },

    navLink: {
      ...theme.fn.focusStyles(),
      display: 'flex',
      alignItems: 'center',
      textDecoration: 'none',
      fontSize: theme.fontSizes.sm,
      color:
        theme.colorScheme === 'dark'
          ? theme.colors.gray[1]
          : theme.colors.gray[1],
      padding: `${theme.spacing.xs} ${theme.spacing.sm}`,
      borderRadius: theme.radius.sm,
      fontWeight: 500,
    },

    link: {
      ...theme.fn.focusStyles(),
      display: 'flex',
      alignItems: 'center',
      textDecoration: 'none',
      fontSize: theme.fontSizes.sm,
      color:
        theme.colorScheme === 'dark'
          ? theme.colors.dark[1]
          : theme.colors.gray[7],
      padding: `${theme.spacing.xs} ${theme.spacing.sm}`,
      borderRadius: theme.radius.sm,
      fontWeight: 500,

      '&:hover': {
        backgroundColor:
          theme.colorScheme === 'dark'
            ? theme.colors.dark[6]
            : theme.colors.gray[3],
        color: theme.colorScheme === 'dark' ? theme.white : theme.black,

        [`& .${icon}`]: {
          color: theme.colorScheme === 'dark' ? theme.white : theme.black,
        },
      },
    },

    linkIcon: {
      ref: icon,
      color:
        theme.colorScheme === 'dark'
          ? theme.colors.dark[2]
          : theme.colors.gray[6],
      marginRight: theme.spacing.sm,
    },

    sectionHeader: {
      color:
        theme.colorScheme === 'dark'
          ? theme.colors.gray[3]
          : theme.colors.gray[8],
    },

    linkActive: {
      '&, &:hover': {
        backgroundColor:
          theme.colorScheme === 'dark'
            ? theme.colors.gray[8]
            : theme.colors.gray[3],
        color:
          theme.colorScheme === 'dark'
            ? theme.colors.gray[3]
            : theme.colors.gray[8],
        [`& .${icon}`]: {
          color:
            theme.colorScheme === 'dark'
              ? theme.colors.gray[3]
              : theme.colors.gray[8],
        },
      },
    },
  }
})

export function NavbarSimpleColored({
  active,
  hidden,
}: {
  active: string
  hidden: boolean
}) {
  const currentUser = useContext(CurrentUserContext)
  const authorisations = useContext(AuthorisationsContext)
  const [, executeLogoutMutation] = useMutation<Mutation>(gql`
    mutation logout {
      logout
    }
  `)
  const { classes, cx } = useStyles()
  const linkObjects = [
    {
      label: 'QUERYING',
      visible:
        authorisations['QUERY'] ||
        authorisations['VIEW_SESSIONS'] ||
        authorisations['VIEW_TEMPLATES'],
    },
    {
      link: '/',
      label: 'Explore',
      icon: IconSearch,
      visible: authorisations['QUERY'],
    },
    {
      link: '/stored-querys',
      label: 'Queries',
      icon: IconListSearch,
      visible: authorisations['QUERY'],
    },
    {
      link: '/sessions',
      label: 'Sessions',
      icon: IconReportSearch,
      visible: authorisations['VIEW_SESSIONS'],
    },
    {
      link: '/templates',
      label: 'Templates',
      icon: IconTemplate,
      visible: authorisations['VIEW_TEMPLATES'],
    },
    {
      label: 'ADMINISTRATION',
      visible:
        authorisations['VIEW_APPLICATIONS'] ||
        authorisations['MANAGE_USERS'] ||
        authorisations['MANAGE_ROLES'],
    },
    {
      link: '/applications',
      label: 'Applications',
      icon: IconServer,
      visible: authorisations['VIEW_APPLICATIONS'],
    },
    {
      link: '/users',
      label: 'Management',
      icon: IconUsers,
      visible: authorisations['MANAGE_USERS'] || authorisations['MANAGE_ROLES'],
    },
    { label: 'ACCOUNT', visible: true },
    { link: '/settings', label: 'Settings', icon: IconSettings, visible: true },
  ].filter((l) => l.visible)

  const links = linkObjects.map((item) =>
    item.link ? (
      <Link
        className={cx(classes.link, {
          [classes.linkActive]: item.label === active,
        })}
        href={item.link}
        key={item.label}
      >
        <item.icon className={classes.linkIcon} stroke={1.1} />
        <span>{item.label}</span>
      </Link>
    ) : (
      <Text
        size="xs"
        weight={500}
        className={cx(classes.sectionHeader)}
        ml={12}
        mt={12}
        key={item.label}
      >
        {item.label}
      </Text>
    )
  )

  return (
    <ResetUrqlClientContext.Consumer>
      {(resetUrqlClient) => (
        <Navbar
          width={{ sm: 240 }}
          p="sm"
          className={classes.navbar}
          hidden={hidden}
        >
          <Navbar.Section grow>{links}</Navbar.Section>

          {currentUser?.username && (
            <UnstyledButton
              onClick={() => {
                executeLogoutMutation().then(() => {
                  resetUrqlClient?.()
                })
              }}
              className={classes.link}
            >
              <IconLogout className={classes.linkIcon} stroke={1.1} />
              <span>Logout ({currentUser?.username})</span>
            </UnstyledButton>
          )}
        </Navbar>
      )}
    </ResetUrqlClientContext.Consumer>
  )
}

export const DashboardLayout = ({
  active,
  children,
}: {
  active: string
  children?: ReactNode
}) => {
  const theme = useMantineTheme()
  const router = useRouter()
  const [sessionModalActive, setSessionModalActive] = useState(false)
  const [opened, setOpened] = useState(false)
  const { classes } = useStyles()

  return (
    <AppShell
      header={
        <Header height={44} className={classes.header} withBorder={false}>
          <Group sx={{ height: '100%' }} px="lg" position="apart">
            <Group>
              <Link className={classes.navLogo} href="/">
                WireQuery
              </Link>
              <MediaQuery smallerThan="xs" styles={{ display: 'none' }}>
                <UnstyledButton
                  className={classes.navLink}
                  onClick={() => setSessionModalActive(true)}
                >
                  New Session
                </UnstyledButton>
              </MediaQuery>
            </Group>
            <>
              <MediaQuery largerThan="md" styles={{ display: 'none' }}>
                <Burger
                  opened={opened}
                  onClick={() => setOpened((o) => !o)}
                  size="sm"
                  color={theme.colors.gray[3]}
                />
              </MediaQuery>
            </>
          </Group>
        </Header>
      }
      navbarOffsetBreakpoint="md"
      navbar={<NavbarSimpleColored active={active} hidden={!opened} />}
    >
      <Container size="lg" px={0}>
        {children}

        <Modal
          size="lg"
          opened={sessionModalActive}
          title="New Session"
          onClose={() => setSessionModalActive(false)}
        >
          <SessionForm
            onSave={(id) => {
              setSessionModalActive(false)
              router.push('/sessions/' + id)
            }}
            onCancel={() => setSessionModalActive(false)}
          />
        </Modal>
      </Container>
    </AppShell>
  )
}
export default DashboardLayout
