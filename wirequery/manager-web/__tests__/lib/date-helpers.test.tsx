// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import {
  daysInMonth,
  nextDay,
  isBefore,
  toDate,
  toDay,
  startOfLastMonth,
  startOfMonth,
  startOfNextMonth,
} from '@lib/date-helpers'

describe('daysInMonth', () => {
  it('returns the correct days for all months in non-leap years', () => {
    ;[31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31].forEach((value, i) => {
      expect(daysInMonth(2001, i + 1)).toBe(value)
    })
  })

  it('returns leap year value for Feb in the year 4 or multiples', () => {
    expect(daysInMonth(4, 2)).toBe(29)
    expect(daysInMonth(2024, 2)).toBe(29)
  })

  it('does not return leap year value for Feb in the year 100 or non-400 multiples', () => {
    expect(daysInMonth(100, 2)).toBe(28)
    expect(daysInMonth(300, 2)).toBe(28)
  })

  it('returns leap year value for Feb in the year 400 or multiples', () => {
    expect(daysInMonth(400, 2)).toBe(29)
    expect(daysInMonth(1600, 2)).toBe(29)
  })
})

describe('nextDay', () => {
  it('returns the next day for non-edge cases', () => {
    expect(nextDay({ year: 2000, month: 1, day: 1 })).toStrictEqual({
      year: 2000,
      month: 1,
      day: 2,
    })
  })

  it('returns the next day for end of the months', () => {
    expect(nextDay({ year: 2000, month: 1, day: 31 })).toStrictEqual({
      year: 2000,
      month: 2,
      day: 1,
    })
  })

  it('returns the next day for end of the years', () => {
    expect(nextDay({ year: 2000, month: 12, day: 31 })).toStrictEqual({
      year: 2001,
      month: 1,
      day: 1,
    })
  })
})

describe('toDay', () => {
  it('converts Date to our day object', () => {
    expect(toDay(new Date('2021-02-24'))).toStrictEqual({
      year: 2021,
      month: 2,
      day: 24,
    })
  })
})

describe('toDate', () => {
  it('converts HourValue to date', () => {
    expect(
      toDate({
        day: { year: 2021, month: 2, day: 4 },
        hour: 2,
        value: 0,
      }).toISOString()
    ).toStrictEqual(new Date('2021-02-04T02:00').toISOString())
  })
})

describe('isBefore', () => {
  it('returns false if dates are equal', () => {
    expect(
      isBefore(
        { year: 2000, month: 1, day: 1 },
        { year: 2000, month: 1, day: 1 }
      )
    ).toBe(false)
  })

  it('returns true if subject year is before compared to date', () => {
    expect(
      isBefore(
        { year: 1999, month: 1, day: 1 },
        { year: 2000, month: 1, day: 1 }
      )
    ).toBe(true)
  })

  it('returns true if subject month is before compared to date', () => {
    expect(
      isBefore(
        { year: 2000, month: 1, day: 1 },
        { year: 2000, month: 2, day: 1 }
      )
    ).toBe(true)
  })

  it('returns true if subject day is before compared to day', () => {
    expect(
      isBefore(
        { year: 2000, month: 1, day: 1 },
        { year: 2000, month: 1, day: 2 }
      )
    ).toBe(true)
  })
})

describe('startOfLastMonth', () => {
  it('converts to start of previous month when month is not at the start of the year', () => {
    expect(startOfLastMonth({ year: 2020, month: 2, day: 10 })).toStrictEqual({
      year: 2020,
      month: 1,
      day: 1,
    })
  })

  it('converts to start of previous month when month is at the start of the year', () => {
    expect(startOfLastMonth({ year: 2020, month: 1, day: 10 })).toStrictEqual({
      year: 2019,
      month: 12,
      day: 1,
    })
  })
})

describe('startOfNextMonth', () => {
  it('converts to start of next month when month is not at the end of the year', () => {
    expect(startOfNextMonth({ year: 2020, month: 2, day: 10 })).toStrictEqual({
      year: 2020,
      month: 3,
      day: 1,
    })
  })

  it('converts to start of next month when month is at the end of the year', () => {
    expect(startOfNextMonth({ year: 2020, month: 12, day: 10 })).toStrictEqual({
      year: 2021,
      month: 1,
      day: 1,
    })
  })
})

describe('startOfMonth', () => {
  it('returns start of month', () => {
    expect(startOfMonth({ year: 2020, month: 2, day: 10 })).toStrictEqual({
      year: 2020,
      month: 2,
      day: 1,
    })
  })
})
