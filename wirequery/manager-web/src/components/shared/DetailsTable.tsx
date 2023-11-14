// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { createStyles, Table, TableProps } from '@mantine/core'

const useStyles = createStyles(() => ({
  myTable: {
    padding: 0,
    td: {
      padding: 0,
      margin: 0,
    },
  },
}))

export const DetailsTable = ({ children, ...props }: TableProps) => {
  const { classes } = useStyles()
  return (
    <Table {...props} className={classes.myTable}>
      {children}
    </Table>
  )
}
