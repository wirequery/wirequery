import { CurrentUserSettings } from '@components/app/user/CurrentUserSettings'
import DashboardLayout from '@components/layout/DashboardLayout'
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
