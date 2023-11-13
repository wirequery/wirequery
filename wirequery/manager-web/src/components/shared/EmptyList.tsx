import { Button, createStyles, Flex, rem, Text, Title } from '@mantine/core'

const useStyles = createStyles((theme) => ({
  root: {
    marginTop: rem(20),
  },

  textBlock: {
    marginTop: 'auto',
    marginBottom: 'auto',
  },

  title: {
    fontWeight: 900,
    fontSize: rem(34),
    marginBottom: theme.spacing.md,
    fontFamily: `Greycliff CF, ${theme.fontFamily}`,

    [theme.fn.smallerThan('sm')]: {
      fontSize: rem(32),
    },
  },

  control: {
    [theme.fn.smallerThan('sm')]: {
      width: '100%',
    },
  },

  highlight: {
    position: 'relative',
    backgroundColor: theme.fn.variant({
      variant: 'light',
      color: theme.primaryColor,
    }).background,
    marginRight: 40,
  },

  mobileImage: {
    [theme.fn.largerThan('sm')]: {
      display: 'none',
    },
  },

  desktopImage: {
    marginLeft: 'auto',
    marginRight: 'auto',
    [theme.fn.smallerThan('sm')]: {
      display: 'none',
    },
  },
}))

export interface EmptyListProps {
  title: string
  description: string
  buttonText?: string
  icon: any
  onClick?: () => void
}

export function EmptyList(props: EmptyListProps) {
  const { classes } = useStyles()

  return (
    <div className={classes.root}>
      <Flex direction={{ base: 'column', sm: 'row' }}>
        <props.icon size={200} stroke={0.375} className={classes.highlight} />
        <div className={classes.textBlock}>
          <Title className={classes.title}>{props.title}</Title>
          <Text color="dimmed" size="lg">
            {props.description}
          </Text>
          {props.onClick ? (
            <Button
              variant="outline"
              size="md"
              mt="xl"
              className={classes.control}
              onClick={props.onClick}
            >
              {props.buttonText}
            </Button>
          ) : (
            <></>
          )}
        </div>
      </Flex>
    </div>
  )
}
