import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { Login } from '@components/app/auth/Login'
import { Client, Provider } from 'urql'
import { fromValue } from 'wonka'

describe('Login', () => {
  it('renders form containing the necessary fields', () => {
    const mockClient: Partial<Client> = {
      executeQuery: jest.fn(),
      executeMutation: jest.fn(),
      executeSubscription: jest.fn(),
    }
    render(
      <Provider value={mockClient as Client}>
        <Login onSave={jest.fn()} onCancel={jest.fn()} />
      </Provider>
    )
    expect(screen.queryByText('Username')).not.toBeNull()
    expect(screen.queryByText('Password')).not.toBeNull()
    expect(screen.queryByText('Sign in')).not.toBeNull()
  })

  it('calls a mutation if sign in is clicked', async () => {
    const executeMutation = jest.fn()
    executeMutation.mockReturnValue(
      fromValue({
        data: { login: {} },
      })
    )
    const mockClient: Partial<Client> = {
      executeQuery: jest.fn(),
      executeMutation,
      executeSubscription: jest.fn(),
    }
    const loginFn = jest.fn()
    render(
      <Provider value={mockClient as Client}>
        <Login onLogin={loginFn} />
      </Provider>
    )
    fireEvent.click(screen.getByText('Sign in'))
    await waitFor(() => expect(mockClient.executeMutation).toBeCalled())
    await waitFor(() => expect(loginFn).toBeCalled())
  })
})
