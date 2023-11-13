import { LogTree } from '@components/shared/LogTree'
import { QueryReport } from '@generated/graphql'
import {
  Divider,
  TextInput,
  Title,
  UnstyledButton,
  useMantineTheme,
} from '@mantine/core'
import { IconCopy, IconDeviceFloppy } from '@tabler/icons-react'
import { useState } from 'react'
import { gql, useSubscription } from 'urql'
import { useQueryParam, StringParam, withDefault } from 'use-query-params'
import { QueryLogChart } from '../query-log/QueryLogChart'

const MAX_RESULTS = 100

export interface QueryFormProps {
  onSaveClick: (query: string) => void
}

export const QueryForm = (props: QueryFormProps) => {
  const theme = useMantineTheme()
  const [expression, setExpression] = useState<string>('')
  const [newExpression, setNewExpression] = useQueryParam<string>(
    'q',
    withDefault(StringParam, '')
  )
  const [results, setResults] = useState<any>([])

  // This variable is necessary to ensure that results are appropriately updated
  // even when handleSubscription is called multiple times during a single render.
  let updatedResults = results

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const handleSubscription = (_messages: any = [], response: any): any => {
    const queryReport = response?.query?.queryReport

    if (queryReport) {
      if (updatedResults.length >= MAX_RESULTS) {
        setExpression('')
        return []
      }
      const message = JSON.parse(queryReport.message)
      if (message?.error) {
        setExpression('')
        updatedResults = [
          {
            msg: message.error,
            startTime: message.startTime,
            endTime: message.endTime,
            traceId: message.traceId,
          },
        ]
        setResults(updatedResults)
        return []
      }
      updatedResults = [
        ...updatedResults,
        {
          msg: message?.result,
          startTime: queryReport.startTime,
          endTime: queryReport.endTime,
          traceId: queryReport.traceId,
        },
      ]
      setResults(updatedResults)
      return []
    }
    return []
  }

  useSubscription<QueryReport>(
    {
      query: gql`
        subscription Query($expression: String!) {
          query(expression: $expression) {
            queryReport {
              appName
              queryId
              message
              startTime
              endTime
              traceId
            }
          }
        }
      `,
      pause: !expression || expression === '',
      variables: { expression: expression ?? '' },
    },
    handleSubscription
  )

  return (
    <>
      <Title order={2}>Explore</Title>
      <TextInput
        defaultValue={newExpression}
        styles={{
          input: {
            fontFamily: theme.fontFamilyMonospace,
          },
        }}
        label="Search Query"
        autoFocus
        onChange={(e) => setNewExpression(e.target.value)}
        onKeyUp={(e) => {
          if (!e.repeat && e.key === 'Enter') {
            setExpression('')
            setResults([])
            setTimeout(() => {
              setExpression(newExpression)
              setResults([])
            })
          }
        }}
        rightSection={
          <UnstyledButton onClick={() => props.onSaveClick(newExpression)}>
            <IconDeviceFloppy strokeWidth={1} />
          </UnstyledButton>
        }
      />
      {results && results?.length !== 0 ? (
        <QueryLogChart
          data={results.map((r: any) => ({
            startTime: r.startTime,
            endTime: r.endTime,
          }))}
        />
      ) : undefined}
      {results?.map((result: any, i: number) => (
        <div key={i}>
          <Divider mb={'xs'} />
          <div style={{ float: 'right' }}>
            <UnstyledButton
              onClick={() =>
                navigator.clipboard.writeText(JSON.stringify(result.msg))
              }
            >
              <IconCopy size={16} />
            </UnstyledButton>
          </div>
          <LogTree
            display={result.msg}
            startTime={result.startTime}
            endTime={result.endTime}
            traceId={result.traceId}
          />
        </div>
      ))}
    </>
  )
}
