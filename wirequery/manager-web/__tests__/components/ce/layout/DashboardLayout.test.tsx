// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { DashboardLayout } from '@components/ce/layout/DashboardLayout'
import { AuthorisationsContext } from '@lib/authorisations-context'
import { CurrentUserContext } from '@lib/current-user-context'
import { ColorSchemeProvider } from '@mantine/core'
import { render, screen } from '@testing-library/react'
import { Client, Provider } from 'urql'

const mockPushFn = jest.fn()
jest.mock('next/router', () => ({
  useRouter: jest.fn(() => ({ push: mockPushFn })),
}))

let mockClient: Partial<Client>

const renderDashboardLayout = (
  authorisations: any,
  currentUser: any = undefined
) => {
  render(
    <ColorSchemeProvider
      colorScheme={undefined as any}
      toggleColorScheme={undefined as any}
    >
      <CurrentUserContext.Provider value={currentUser}>
        <Provider value={mockClient as Client}>
          <AuthorisationsContext.Provider value={authorisations}>
            <DashboardLayout active=""></DashboardLayout>
          </AuthorisationsContext.Provider>
        </Provider>
      </CurrentUserContext.Provider>
    </ColorSchemeProvider>
  )
}

describe('DashboardLayout', () => {
  beforeEach(() => {
    mockClient = {
      executeQuery: jest.fn(),
      executeMutation: jest.fn(),
      executeSubscription: jest.fn(),
    }
  })

  it('renders the name of the app', () => {
    renderDashboardLayout({})
    expect(screen.queryByText('WireQuery')).not.toBeNull()
  })

  describe('authentication', () => {
    it('renders the currently logged in user and Logout', () => {
      renderDashboardLayout({}, { username: 'wnederhof' })
      expect(screen.queryByText('Logout (wnederhof)')).not.toBeNull()
    })
  })

  describe('querying', () => {
    it('does not render QUERYING caption or items if there are no authorisations', () => {
      renderDashboardLayout({})
      expect(screen.queryByText('QUERYING')).toBeNull()
      expect(screen.queryByText('Explore')).toBeNull()
      expect(screen.queryByText('Queries')).toBeNull()
      expect(screen.queryByText('Sessions')).toBeNull()
      expect(screen.queryByText('Templates')).toBeNull()
    })

    it('renders QUERYING, Queries and Explore if QUERY authorisation is present', () => {
      renderDashboardLayout({ QUERY: true })
      expect(screen.queryByText('QUERYING')).not.toBeNull()
      expect(screen.queryByText('Explore')).not.toBeNull()
      expect(screen.queryByText('Queries')).not.toBeNull()
    })

    it('renders QUERYING and Sessions if VIEW_SESSIONS authorisation is present', () => {
      renderDashboardLayout({ VIEW_SESSIONS: true })
      expect(screen.queryByText('QUERYING')).not.toBeNull()
      expect(screen.queryByText('Sessions')).not.toBeNull()
    })

    it('renders QUERYING and Templates if VIEW_TEMPLATES authorisation is present', () => {
      renderDashboardLayout({ VIEW_TEMPLATES: true })
      expect(screen.queryByText('QUERYING')).not.toBeNull()
      expect(screen.queryByText('Templates')).not.toBeNull()
    })
  })

  describe('administration', () => {
    it('does not render ADMINISTRATION caption or items if there are no authorisations', () => {
      renderDashboardLayout({})
      expect(screen.queryByText('ADMINISTRATION')).toBeNull()
      expect(screen.queryByText('Applications')).toBeNull()
      expect(screen.queryByText('Management')).toBeNull()
    })

    it('renders ADMINISTRATION and Explore if VIEW_APPLICATIONS authorisation is present', () => {
      renderDashboardLayout({ VIEW_APPLICATIONS: true })
      expect(screen.queryByText('ADMINISTRATION')).not.toBeNull()
      expect(screen.queryByText('Applications')).not.toBeNull()
    })

    it('renders ADMINISTRATION and Queries if MANAGE_USERS authorisation is present', () => {
      renderDashboardLayout({ MANAGE_USERS: true })
      expect(screen.queryByText('ADMINISTRATION')).not.toBeNull()
      expect(screen.queryByText('Management')).not.toBeNull()
    })

    it('renders ADMINISTRATION and Queries if MANAGE_ROLES authorisation is present', () => {
      renderDashboardLayout({ MANAGE_ROLES: true })
      expect(screen.queryByText('ADMINISTRATION')).not.toBeNull()
      expect(screen.queryByText('Management')).not.toBeNull()
    })
  })

  it('always renders ACCOUNT caption and Settings', () => {
    renderDashboardLayout({})
    expect(screen.queryByText('ACCOUNT')).not.toBeNull()
    expect(screen.queryByText('Settings')).not.toBeNull()
  })
})
