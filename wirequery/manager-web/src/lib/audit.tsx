// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

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
