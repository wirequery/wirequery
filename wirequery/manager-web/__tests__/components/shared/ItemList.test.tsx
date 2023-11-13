import { ItemList } from '@components/shared/ItemList'
import { IconFilter } from '@tabler/icons-react'
import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { act } from 'react-dom/test-utils'

describe('ItemList', () => {
  let oldScrollTo: any

  beforeEach(() => {
    oldScrollTo = global.scrollTo
    global.scrollTo = jest.fn()
  })

  afterEach(() => {
    global.scrollTo = oldScrollTo
  })

  describe('pagination', () => {
    it('should render the first items, capped by the max, which is 10 by default', () => {
      act(() => {
        render(
          <ItemList
            fetching={false}
            error={undefined}
            emptyIcon={IconFilter}
            emptyTitle="test"
            emptyDescription="test"
            data={[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]}
          >
            {(row) => <div key={row}>Element {row}</div>}
          </ItemList>
        )
      })
      ;[1, 2, 3, 4, 5, 6, 7, 8, 9, 10].forEach((el) =>
        expect(screen.getAllByText('Element ' + el)).not.toHaveLength(0)
      )
      ;[11, 12].forEach((el) =>
        expect(screen.queryAllByText('Element ' + el)).toHaveLength(0)
      )
    })

    it('should handle a different page size', () => {
      act(() => {
        render(
          <ItemList
            fetching={false}
            error={undefined}
            emptyIcon={IconFilter}
            emptyTitle="test"
            emptyDescription="test"
            maxPerPage={3}
            data={['a', 'b', 'c', 'd', 'e', 'f']}
          >
            {(row) => <div key={row}>Element {row}</div>}
          </ItemList>
        )
      })
      ;['a', 'b', 'c'].forEach((el) =>
        expect(screen.getAllByText('Element ' + el)).not.toHaveLength(0)
      )
      ;['d', 'e', 'f'].forEach((el) =>
        expect(screen.queryAllByText('Element ' + el)).toHaveLength(0)
      )
    })

    it('should handle pagination', async () => {
      act(() => {
        render(
          <ItemList
            fetching={false}
            error={undefined}
            emptyIcon={IconFilter}
            emptyTitle="test"
            emptyDescription="test"
            maxPerPage={3}
            data={['a', 'b', 'c', 'd', 'e', 'f']}
          >
            {(row) => <div key={row}>Element {row}</div>}
          </ItemList>
        )
      })
      await waitFor(() => {
        act(() => {
          fireEvent(
            screen.getByText('2'),
            new MouseEvent('click', {
              bubbles: true,
              cancelable: true,
            })
          )
        })
      })
      ;['a', 'b', 'c'].forEach((el) =>
        expect(screen.queryAllByText('Element ' + el)).toHaveLength(0)
      )
      ;['d', 'e', 'f'].forEach((el) =>
        expect(screen.getAllByText('Element ' + el)).not.toHaveLength(0)
      )
    })

    it('should have the right number of pages', () => {
      ;[
        { data: [], pageSize: 3, expected: 0 },
        { data: ['a'], pageSize: 3, expected: 1 },
        { data: ['a', 'b', 'c'], pageSize: 3, expected: 1 },
        { data: ['a', 'b', 'c', 'd'], pageSize: 3, expected: 2 },
      ].forEach(({ data, pageSize, expected }) => {
        act(() => {
          render(
            <ItemList
              fetching={false}
              error={undefined}
              emptyIcon={IconFilter}
              emptyTitle="test"
              emptyDescription="test"
              maxPerPage={pageSize}
              data={data}
            >
              {(row) => <div key={row}>Element {row}</div>}
            </ItemList>
          )
        })
        if (expected > 0) {
          expect(screen.getAllByText('' + expected)).not.toHaveLength(0)
        }
        expect(screen.queryAllByText('' + (expected + 1))).toHaveLength(0)
      })
    })
  })

  describe('empty states', () => {
    it('should show empty message if there are no entries with a clickable button', async () => {
      const jestFn = jest.fn()
      render(
        <ItemList
          fetching={false}
          error={undefined}
          emptyIcon={IconFilter}
          emptyTitle="title"
          emptyDescription="description"
          emptyButtonText="button"
          emptyOnClick={jestFn}
          data={[]}
        >
          {(row) => <div key={row}>Element {row}</div>}
        </ItemList>
      )
      expect(screen.getAllByText('title')).not.toHaveLength(0)
      expect(screen.getAllByText('description')).not.toHaveLength(0)

      expect(jestFn).not.toHaveBeenCalled()

      await waitFor(() => {
        act(() => {
          fireEvent(
            screen.getByText('button'),
            new MouseEvent('click', {
              bubbles: true,
              cancelable: true,
            })
          )
        })
      })
      expect(jestFn).toHaveBeenCalled()
    })
  })

  describe('filtering', () => {
    it('should not show the filter button if filters are not defined', () => {
      render(
        <ItemList
          fetching={false}
          error={undefined}
          emptyIcon={IconFilter}
          emptyTitle="title"
          emptyDescription="description"
          data={['a', 'b', 'c']}
        >
          {(row) => <div key={row}>Element {row}</div>}
        </ItemList>
      )
      expect(screen.queryAllByTitle('Filter')).toHaveLength(0)
    })

    it('should show the filter button if filters are defined', () => {
      render(
        <ItemList
          fetching={false}
          error={undefined}
          emptyIcon={IconFilter}
          emptyTitle="title"
          emptyDescription="description"
          data={['a', 'b', 'c']}
          filters={[{ type: 'text', field: 'name', label: 'Name' }]}
        >
          {(row) => <div key={row}>Element {row}</div>}
        </ItemList>
      )
      expect(screen.getAllByTitle('Filter')).toHaveLength(1)
    })

    it('should be able to filter on text values', () => {
      render(
        <ItemList
          fetching={false}
          error={undefined}
          emptyIcon={IconFilter}
          emptyTitle="title"
          emptyDescription="description"
          data={[{ name: 'wouter' }, { name: 'piet' }]}
          filters={[{ type: 'text', field: 'name', label: 'Name' }]}
        >
          {(row) => <div key={row.name}>{row.name}</div>}
        </ItemList>
      )
      expect(screen.queryAllByText('wouter')).toHaveLength(1)
      expect(screen.queryAllByText('piet')).toHaveLength(1)

      fireEvent(
        screen.getByTitle('Filter'),
        new MouseEvent('click', {
          bubbles: true,
          cancelable: true,
        })
      )

      fireEvent.change(screen.getByLabelText('Name', { exact: false }), {
        target: { value: 'wouter' },
      })

      expect(screen.queryAllByText('wouter')).toHaveLength(1)
      expect(screen.queryAllByText('piet')).toHaveLength(0)
    })
  })
})
