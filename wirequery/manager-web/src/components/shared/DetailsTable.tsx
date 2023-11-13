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
