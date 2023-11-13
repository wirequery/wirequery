import { User } from '@generated/graphql'
import React from 'react'

export const CurrentUserContext = React.createContext<User>(undefined as any)
