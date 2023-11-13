import { notifications } from '@mantine/notifications'
import { IconX } from '@tabler/icons-react'
import { CombinedError } from 'urql'

export const showErrorAlert = (error: CombinedError): boolean => {
  if (error.graphQLErrors?.length > 0) {
    if (
      error.graphQLErrors[0].extensions &&
      error.graphQLErrors[0].extensions['classification'] === 'ValidationError'
    ) {
      return showErrorAlertForMessage(
        'Unable to process request. Please check if the form is correctly filled in.'
      )
    }
    return showErrorAlertForMessage(error.graphQLErrors[0].message)
  }
  if (error.networkError) {
    return showErrorAlertForMessage(
      'There were network issues while loading. Please check your internet connection and try again.'
    )
  }
  return showErrorAlertForMessage(error.message)
}

export const showInfoAlert = (message: string): boolean => {
  notifications.show({
    message,
  })
  return true
}

export const showErrorAlertForMessage = (message: string): boolean => {
  notifications.show({
    title: 'An error occured',
    message,
    color: 'red',
    icon: <IconX />,
  })
  return true
}
