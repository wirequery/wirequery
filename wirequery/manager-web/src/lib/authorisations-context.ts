import React from 'react'

export const AuthorisationsContext = React.createContext<{
  [key: string]: boolean
}>({})
