import { Loader } from '@mantine/core'
import { useEffect, useState } from 'react'

export const LoadingScreen = () => {
  const [visible, setVisible] = useState(false)

  useEffect(() => {
    setTimeout(() => {
      setVisible(true)
    }, 500)
  })

  return visible ? <Loader variant="dots" /> : <></>
}
