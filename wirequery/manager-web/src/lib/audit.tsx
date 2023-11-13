export interface CreateAuditItemsProps {
  createdAt: string
  updatedAt?: string | null
  createdBy?: string | null
  updatedBy?: string | null
}

export const createAuditItems = (
  props: CreateAuditItemsProps | undefined | null
) => {
  if (!props) {
    return []
  }
  return [
    'Created ' +
      new Date(props.createdAt).toLocaleString() +
      (props.createdBy ? ' by ' + props.createdBy : ''),
    props.createdAt !== props.updatedAt
      ? props.updatedAt &&
        'Updated ' +
          new Date(props.updatedAt).toLocaleString() +
          (props.updatedBy ? ' by ' + props.updatedBy : '')
      : undefined,
  ]
}
