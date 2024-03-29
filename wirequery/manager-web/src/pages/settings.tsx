// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { CurrentUserSettings } from '@components/shared/app/user/CurrentUserSettings'
import DashboardLayout from '@components/ce/layout/DashboardLayout'
import { ActionIcon, Title, useMantineColorScheme } from '@mantine/core'
import { IconMoonStars, IconSun } from '@tabler/icons-react'

export default function Settings() {
  const { colorScheme, toggleColorScheme } = useMantineColorScheme()
  return (
    <>
      <DashboardLayout active="Settings">
        <ActionIcon
          style={{ float: 'right' }}
          color="purple"
          variant="filled"
          onClick={() => toggleColorScheme()}
          size={30}
        >
          {colorScheme === 'dark' ? (
            <IconSun size={16} />
          ) : (
            <IconMoonStars size={16} />
          )}
        </ActionIcon>
        <Title order={2}>Settings</Title>
        <Title order={4} pt="lg">
          User Settings
        </Title>
        <CurrentUserSettings onSave={() => ({})} />
      </DashboardLayout>
    </>
  )
}
