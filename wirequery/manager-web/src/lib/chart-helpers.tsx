// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { Day, HourValue, isBefore, nextDay } from './date-helpers'

export const zeroesForEachHour = (
  start: Day,
  endExclusive: Day
): HourValue[] => {
  let currentDay = start
  const days: HourValue[] = []
  while (isBefore(currentDay, endExclusive)) {
    for (let i = 0; i < 24; i++) {
      days.push({ day: currentDay, hour: i, value: 0 })
    }
    currentDay = nextDay(currentDay)
  }
  return days
}

const createKey = (hourValue: HourValue): string => {
  return (
    hourValue.day.year +
    ':' +
    hourValue.day.month +
    ':' +
    hourValue.day.day +
    ':' +
    hourValue.hour
  )
}

export const incrementHourValues = (
  hourValues: HourValue[],
  incrementByHourValues: HourValue[]
) => {
  const valueByHourValueKey: { [key: string]: number } = {}

  incrementByHourValues.forEach((hourValue) => {
    const key = createKey(hourValue)
    if (valueByHourValueKey[key] === undefined) {
      valueByHourValueKey[key] = 0
    }
    valueByHourValueKey[key] += hourValue.value
  })

  const result: HourValue[] = []
  hourValues.forEach((hourValue) => {
    result.push({
      day: hourValue.day,
      hour: hourValue.hour,
      value: hourValue.value + (valueByHourValueKey[createKey(hourValue)] ?? 0),
    })
  })
  return result
}
