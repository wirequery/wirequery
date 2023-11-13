import { TableProps } from '@mantine/core'
import { Table } from '@mantine/core'

export const ListTable = ({ children, ...props }: TableProps) => (
  <Table striped highlightOnHover {...props}>
    {children}
  </Table>
)
