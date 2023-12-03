// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

export interface Day {
  year: number;
  month: number;
  day: number;
}

export interface HourValue {
  day: Day;
  hour: number;
  value: number
}

export const daysInMonth = (year: number, month: number): number => {
  // February leap year, once in 400 years
  if (month === 2 && year % 4 === 0 && year % 400 === 0) {
    return 29
  }
  // February leap year, not once in 100 years
  if (month === 2 && year % 4 === 0 && year % 100 !== 0) {
    return 29
  }

  const daysInMonth = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31]
  return daysInMonth[month - 1]
}

export const nextDay = (day: Day): Day => {
  const endOfTheMonth = daysInMonth(day.year, day.month) === day.day
  if (endOfTheMonth && day.month === 12) {
    return {
      year: day.year + 1,
      month: 1,
      day: 1
    }
  }
  if (endOfTheMonth) {
    return {
      year: day.year,
      month: day.month + 1,
      day: 1
    }
  }
  return {
    year: day.year,
    month: day.month,
    day: day.day + 1,
  }
}

export const isBefore = (subject: Day, comparedTo: Day) => {
  if (subject.year < comparedTo.year) {
    return true
  }
  if (subject.month < comparedTo.month) {
    return true
  }
  if (subject.day < comparedTo.day) {
    return true
  }
  return false
}

export const toDay = (date: Date): Day => {
  return {
    year: date.getFullYear(),
    month: date.getMonth() + 1,
    day: date.getDate(),
  }
}

export const toDate = (hourValue: HourValue): Date => {
  return new Date(hourValue.day.year, hourValue.day.month - 1, hourValue.day.day, hourValue.hour)
}

export const startOfMonth = (day: Day): Day => {
  return {
    year: day.year,
    month: day.month,
    day: 1
  }
}

export const startOfNextMonth = (day: Day): Day => {
  if (day.month < 12) {
    return {
      year: day.year,
      month: day.month + 1,
      day: 1
    }
  }
  return {
    year: day.year + 1,
    month: 1,
    day: 1
  }
}

export const startOfLastMonth = (day: Day): Day => {
  if (day.month > 1) {
    return {
      year: day.year,
      month: day.month - 1,
      day: 1
    }
  }
  return {
    year: day.year - 1,
    month: 12,
    day: 1
  }
}