import { EmptyList } from '@components/shared/EmptyList'
import { ErrorMessage } from '@components/shared/ErrorMessage'
import { Item } from '@components/shared/Item'
import { LoadingScreen } from '@components/shared/LoadingScreen'
import { Mutation, Query } from '@generated/graphql'
import { showErrorAlert, showInfoAlert } from '@lib/alert'
import { Code } from '@mantine/core'
import { IconListSearch } from '@tabler/icons-react'
import { useMemo } from 'react'
import { gql, useMutation, useQuery } from 'urql'

export interface TemplateQueryListProps {
  applicationId?: string
  templateId?: string
  onCreateTemplateQuery?: () => void
}

export function TemplateQueryList(props: TemplateQueryListProps) {
  const [, executeDeleteMutation] = useMutation<Mutation>(gql`
    mutation deleteTemplateQuery($id: ID!) {
      deleteTemplateQuery(id: $id)
    }
  `)

  const handleDelete = (id: string) => {
    confirm('Are you sure you want to delete Template Query?') &&
      executeDeleteMutation({ id }, { additionalTypenames: ['TemplateQuery'] })
        .then(() => showInfoAlert('Query deleted'))
        .catch(showErrorAlert)
  }

  const [{ data, error, fetching }] = useQuery<Query>({
    query: gql`
      query templateQueryList($filter: TemplateQueryFilter) {
        templateQuerys(filter: $filter) {
          id
          template {
            id
            name
          }
          application {
            id
            name
          }
          nameTemplate
          queryTemplate
          queryLimit
        }
      }
    `,
    variables: {
      filter: {
        applicationId:
          props.applicationId !== undefined
            ? parseInt(props.applicationId)
            : undefined,
        templateId:
          props.templateId !== undefined
            ? parseInt(props.templateId)
            : undefined,
      },
    },
    context: useMemo(
      () => ({
        additionalTypenames: ['TemplateQuery'],
      }),
      []
    ),
  })

  if (fetching) {
    return <LoadingScreen />
  }

  if (error?.message) {
    return <ErrorMessage error={error} />
  }

  if (data?.templateQuerys?.length === 0) {
    return (
      <EmptyList
        icon={IconListSearch}
        title="No template queries found"
        description="No template queries were found."
        buttonText="Create new query"
        onClick={props.onCreateTemplateQuery}
      />
    )
  }

  return (
    <>
      {[...(data?.templateQuerys ?? [])]
        .sort((a, b) => a.nameTemplate.localeCompare(b.nameTemplate))
        .map((row) => (
          <Item
            key={row.id}
            label={row.nameTemplate}
            description={<Code>{row.queryTemplate}</Code>}
            onDelete={() => handleDelete(row.id)}
            items={[
              row.template?.name,
              row.application?.name,
              'Max ' + row.queryLimit,
            ]}
          />
        ))}
    </>
  )
}
