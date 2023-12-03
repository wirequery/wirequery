// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { incrementHourValues, zeroesForEachHour } from "@lib/chart-helpers"

describe('chart-helpers', () => {
  describe('zeroesForEachHour', () => {
    it('fills in zeroes for each hour', () => {
      const values = zeroesForEachHour(
        { year: 2020, month: 1, day: 1 },
        { year: 2020, month: 1, day: 3 },
      )
      expect(values[0]).toStrictEqual({ day: { year: 2020, month: 1, day: 1 }, hour: 0, value: 0 })
      expect(values[23]).toStrictEqual({ day: { year: 2020, month: 1, day: 1 }, hour: 23, value: 0 })
      expect(values[47]).toStrictEqual({ day: { year: 2020, month: 1, day: 2 }, hour: 23, value: 0 })
      expect(values).toHaveLength(48)
    })
  })

  describe('incrementHourValues', () => {
    it('increments values that are in range', () => {
      const actual = incrementHourValues(
        [{ day: { year: 2020, month: 1, day: 1 }, hour: 0, value: 5 }],
        [{ day: { year: 2020, month: 1, day: 1 }, hour: 0, value: 10 }])

      expect(actual).toStrictEqual([{ day: { year: 2020, month: 1, day: 1 }, hour: 0, value: 15 }])
    })

    it('does not increment values that are not part of the first array', () => {
      const actual = incrementHourValues(
        [{ day: { year: 2020, month: 1, day: 1 }, hour: 0, value: 5 }],
        [
          { day: { year: 2020, month: 1, day: 2 }, hour: 0, value: 10 },
          { day: { year: 2020, month: 2, day: 1 }, hour: 0, value: 10 },
          { day: { year: 2021, month: 1, day: 1 }, hour: 0, value: 10 },
          { day: { year: 2020, month: 1, day: 1 }, hour: 1, value: 10 }
        ])

      expect(actual).toStrictEqual([{ day: { year: 2020, month: 1, day: 1 }, hour: 0, value: 5 }])
    })

    it('supports multiple same values as second arg', () => {
      const actual = incrementHourValues(
        [{ day: { year: 2020, month: 1, day: 1 }, hour: 0, value: 5 }],
        [
          { day: { year: 2020, month: 1, day: 1 }, hour: 0, value: 10 },
          { day: { year: 2020, month: 1, day: 1 }, hour: 0, value: 10 }
        ])

      expect(actual).toStrictEqual([
        { day: { year: 2020, month: 1, day: 1 }, hour: 0, value: 25 }
      ])
    })
  })
})