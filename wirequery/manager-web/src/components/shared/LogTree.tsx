import { SummaryBar } from '@components/shared/SummaryBar'
import { Box, useMantineTheme } from '@mantine/core'
import { atelierForest } from 'base16'
import { JSONTree } from 'react-json-tree'

export const LogTree = (props: {
  display: any
  startTime?: number
  endTime?: number
  traceId?: string | null
}) => {
  const { colorScheme } = useMantineTheme()
  const theme = useMantineTheme()
  const customTheme = {
    ...atelierForest,
    base00: colorScheme !== 'dark' ? theme.white : theme.colors.dark[8],
    base0D: (theme.colors as any)?.purple?.[5],
  }

  return (
    <div
      style={{
        fontFamily:
          'ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, Liberation Mono, Courier New, monospace',
        fontSize: '14px',
      }}
    >
      <JSONTree data={props.display} hideRoot theme={customTheme} />

      <Box m={'md'}>
        <SummaryBar
          type="badges"
          items={[
            props.startTime
              ? new Date(props.startTime).toLocaleDateString() +
                ' ' +
                new Date(props.startTime).toLocaleTimeString()
              : undefined,
            props.endTime && props.endTime
              ? props.endTime - (props.startTime ?? 0) + ' ms'
              : undefined,
            props.traceId ? props.traceId : undefined,
          ]}
        />
      </Box>
    </div>
  )
}
