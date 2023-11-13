import {
  showErrorAlert,
  showErrorAlertForMessage,
  showInfoAlert,
} from '@lib/alert'
import { notifications } from '@mantine/notifications'
import { IconX } from '@tabler/icons-react'
import { CombinedError } from 'urql'

jest.mock('@mantine/notifications', () => ({
  notifications: {
    show: jest.fn(),
  },
}))

describe('alert', () => {
  describe('showErrorAlert', () => {
    it('shows the error message by default and returns true', () => {
      const actual = showErrorAlert({
        message: 'test',
      } as object as CombinedError)
      expect(actual).toBeTruthy()
      expect(notifications.show).toBeCalledWith({
        title: 'An error occured',
        message: 'test',
        color: 'red',
        icon: <IconX />,
      })
    })
  })

  it('shows a validation error message if a ValidationError is returned and returns true', () => {
    const actual = showErrorAlert({
      graphQLErrors: [
        {
          extensions: {
            classification: 'ValidationError',
          },
        },
      ],
    } as object as CombinedError)
    expect(actual).toBeTruthy()
    expect(notifications.show).toBeCalledWith({
      title: 'An error occured',
      message:
        'Unable to process request. Please check if the form is correctly filled in.',
      color: 'red',
      icon: <IconX />,
    })
  })

  it('shows a network error message if there was a network error and returns true', () => {
    const actual = showErrorAlert({
      networkError: {},
    } as object as CombinedError)
    expect(actual).toBeTruthy()
    expect(notifications.show).toBeCalledWith({
      title: 'An error occured',
      message:
        'There were network issues while loading. Please check your internet connection and try again.',
      color: 'red',
      icon: <IconX />,
    })
  })

  describe('showInfoAlert', () => {
    it('shows the provided info message and returns true', () => {
      const actual = showInfoAlert('test')
      expect(actual).toBeTruthy()
      expect(notifications.show).toBeCalledWith({
        message: 'test',
      })
    })
  })

  describe('showErrorAlertForMessage', () => {
    it('shows the provided error message and returns true', () => {
      const actual = showErrorAlertForMessage('test')
      expect(actual).toBeTruthy()
      expect(notifications.show).toBeCalledWith({
        title: 'An error occured',
        message: 'test',
        color: 'red',
        icon: <IconX />,
      })
    })
  })
})
