import { Mutation } from '@generated/graphql'
import { showErrorAlert, showErrorAlertForMessage } from '@lib/alert'
import { Button, PasswordInput, TextInput } from '@mantine/core'
import { useForm } from '@mantine/form'
import { gql, OperationResult, useMutation } from 'urql'

export interface LoginProps {
  onLogin: (success: boolean) => void
}

interface LoginFormData {
  username: string | undefined
  password: string | undefined
}

export function Login(props: LoginProps) {
  const [, executeLoginMutation] = useMutation<Mutation>(gql`
    mutation login($input: LoginInput!) {
      login(input: $input) {
        id
        username
      }
    }
  `)

  function handleMutationResult(result: OperationResult<Mutation>) {
    if (result.error) {
      showErrorAlert(result.error)
    }
    return !result.error
  }

  const doSubmit = (formData: LoginFormData) => {
    executeLoginMutation({ input: formData })
      .then((result) => {
        if (handleMutationResult(result) && result?.data?.login) {
          props.onLogin(!!result?.data?.login)
        } else {
          showErrorAlertForMessage(
            'Could not login. Please check your credentials.'
          )
        }
      })
      .catch(showErrorAlert)
  }

  const form = useForm({
    initialValues: {
      username: '',
      password: '',
    },
  })

  return (
    <form role="form" onSubmit={form.onSubmit(doSubmit)}>
      <TextInput
        label="Username"
        placeholder="Your username"
        mt="md"
        {...form.getInputProps('username')}
      />
      <PasswordInput
        label="Password"
        placeholder="Your password"
        mt="md"
        {...form.getInputProps('password')}
      />
      <Button fullWidth mt="xl" type="submit">
        Sign in
      </Button>
    </form>
  )
}
