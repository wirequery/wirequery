// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { TableProps } from '@mantine/core'
import { Table } from '@mantine/core'

export const ListTable = ({ children, ...props }: TableProps) => (
  <Table striped highlightOnHover {...props}>
    {children}
  </Table>
)
