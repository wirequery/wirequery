import { Mutation } from '@generated/graphql'
import { showErrorAlert, showInfoAlert } from '@lib/alert'
import {
  Button,
  Grid,
  Group,
  NativeSelect,
  NumberInput,
  Textarea,
  TextInput,
  UnstyledButton,
} from '@mantine/core'
import { DateTimePicker } from '@mantine/dates'
import { useForm } from '@mantine/form'
import { gql, OperationResult, useMutation } from 'urql'

export interface StoredQueryFormProps {
  query?: string
  onSave: (id: string | number) => void
  onCancel: () => void
}

interface StoredQueryFormData {
  name: string | undefined
  type: string | undefined
  query: string | undefined
  queryLimit: number | undefined
  endDate: string | undefined
}

export function StoredQueryForm(props: StoredQueryFormProps) {
  const [, executeCreateMutation] = useMutation<Mutation>(gql`
    mutation createStoredQuery($input: CreateStoredQueryInput!) {
      createStoredQuery(input: $input) {
        id
      }
    }
  `)

  function handleMutationResult(result: OperationResult<Mutation>) {
    if (result.error) {
      showErrorAlert(result.error)
    }
    return !result.error
  }

  const doSubmit = (formData: StoredQueryFormData) => {
    executeCreateMutation({ input: { ...formData } })
      .then(
        (result) =>
          handleMutationResult(result) &&
          showInfoAlert('Query saved') &&
          props.onSave(result?.data?.createStoredQuery?.id as any)
      )
      .catch(showErrorAlert)
  }

  const form = useForm<StoredQueryFormData>({
    initialValues: {
      name: '',
      type: 'TAPPING',
      query: props.query || '',
      queryLimit: 100,
      endDate: undefined,
    },
    validate: {
      name: (value) => (value?.length !== 0 ? null : 'No name set'),
    },
  })

  const setEndDateInMinutes = (hours: number) => {
    const newDateTime = new Date()
    newDateTime.setTime(newDateTime.getTime() + 60 * 60 * 1000 * hours)
    form.setFieldValue('endDate', newDateTime as any)
  }

  const setEndDateInHours = (hours: number) => {
    const newDateTime = new Date()
    newDateTime.setTime(newDateTime.getTime() + 60 * 60 * 1000 * hours)
    form.setFieldValue('endDate', newDateTime as any)
  }

  return (
    <form role="form" onSubmit={form.onSubmit(doSubmit)}>
      <TextInput label="Name" required {...form.getInputProps('name')} />
      {props.query || (
        <Textarea label="Query" {...form.getInputProps('query')} />
      )}
      {false && (
        <NativeSelect
          hidden
          label="Type"
          {...form.getInputProps('type')}
          data={[{ label: 'Tap', value: 'TAPPING' }]}
        />
      )}
      <Grid>
        <Grid.Col span={6}>
          <NumberInput
            label="Query Limit"
            description="Max number of query results"
            min={1}
            max={999}
            required
            type="number"
            {...form.getInputProps('queryLimit')}
          />
        </Grid.Col>
        <Grid.Col span={6}>
          <DateTimePicker
            dropdownType="modal"
            label="Ends at"
            description="No end moment if left empty."
            clearable
            {...form.getInputProps('endDate')}
          />
          <Group>
            <UnstyledButton onClick={() => setEndDateInMinutes(3)}>
              3m
            </UnstyledButton>
            <UnstyledButton onClick={() => setEndDateInMinutes(10)}>
              10m
            </UnstyledButton>
            <UnstyledButton onClick={() => setEndDateInHours(1)}>
              1h
            </UnstyledButton>
            <UnstyledButton onClick={() => setEndDateInHours(12)}>
              12h
            </UnstyledButton>
            <UnstyledButton onClick={() => setEndDateInHours(24)}>
              24h
            </UnstyledButton>
          </Group>
        </Grid.Col>
      </Grid>
      <Group style={{ paddingTop: 15 }}>
        <Button type="submit">Save</Button>
        <Button type="button" variant="outline" onClick={props.onCancel}>
          Cancel
        </Button>
      </Group>
    </form>
  )
}
