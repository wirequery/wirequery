
// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { RecordingPlayer } from '@components/shared/app/recording/RecordingPlayer'
import { Recording, RecordingPlayerQuery } from '@generated/graphql'
import { ColorSchemeProvider } from '@mantine/core'
import { screen, render } from '@testing-library/react'
import { Client, Provider } from 'urql'
import { fromValue } from 'wonka'
import rrwebPlayer from 'rrweb-player'

jest.mock('rrweb-player')

jest.mock('@mantine/core', () => ({
  ...jest.requireActual('@mantine/core'),
  ScrollArea: (props: any) => <div>{props.children}</div>
}))

// Most of the code in this component is visual, so we only check if no error occures when rendered.
describe('RecordingPlayer', () => {
  it('renders the player without errors', () => {
    const recordings: Recording[] = [
      {
        id: '1',
        sessionId: 1,
        recording: "[]",
        createdAt: Symbol() as any
      }
    ]
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue<{ data: RecordingPlayerQuery }>({
        data: { recordings },
      })
    )
    const mockClient: Partial<Client> = {
      executeQuery,
      executeMutation: jest.fn(),
      executeSubscription: jest.fn(),
    }

    render(
      <Provider value={mockClient as Client}>
        <ColorSchemeProvider
          colorScheme={undefined as any}
          toggleColorScheme={undefined as any}
        >
          <RecordingPlayer sessionId={'1'} onUpdateCurrentTime={jest.fn()} onMetadataAvailable={jest.fn()} />
        </ColorSchemeProvider>
      </Provider>
    )
  })

  it('renders the logs', () => {
    (rrwebPlayer as any).mockImplementation(() => {
      return {
        getMetaData: () => ({
          startTime: 0,
          endTime: 10
        }),
        addEventListener: jest.fn()
      }
    })

    const recordings: Recording[] = [
      {
        id: '1',
        sessionId: 1,
        recording: `[
          {"timestamp": 0, "payload": {"level": "error", "payload": "Test Log"}, "data": {"plugin": "rrweb/console@1"}},
        ]`,
        createdAt: Symbol() as any
      }
    ]
    const executeQuery = jest.fn()
    executeQuery.mockReturnValue(
      fromValue<{ data: RecordingPlayerQuery }>({
        data: { recordings },
      })
    )
    const mockClient: Partial<Client> = {
      executeQuery,
      executeMutation: jest.fn(),
      executeSubscription: jest.fn(),
    }

    render(
      <Provider value={mockClient as Client}>
        <ColorSchemeProvider
          colorScheme={undefined as any}
          toggleColorScheme={undefined as any}
        >
          <RecordingPlayer sessionId={'1'} onUpdateCurrentTime={jest.fn()} onMetadataAvailable={jest.fn()} />
        </ColorSchemeProvider>
      </Provider>
    )
    expect(screen.findByText('Test Log', { exact: false })).not.toBeNull()
  })
})
