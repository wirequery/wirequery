// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import { createAuditItems } from '@lib/audit'

describe('audit', () => {
  let mockDate: jest.SpyInstance

  beforeAll(() => {
    mockDate = jest
      .spyOn(Date.prototype, 'toLocaleString')
      .mockReturnValue('<datetime>')
  })

  afterAll(() => {
    mockDate.mockRestore()
  })

  describe('createAuditItems', () => {
    const simplify = (elements: (string | null | undefined)[]) =>
      elements
        .map((element) => element?.trim())
        .filter((element) => element !== undefined)

    it('renders only created at if that is the only thing that is passed', () => {
      const actual = createAuditItems({
        createdAt: '1-1-1970',
        updatedAt: null,
        createdBy: null,
        updatedBy: null,
      })

      expect(simplify(actual)).toEqual(['Created <datetime>'])
    })

    it('adds the name of the creator if createdBy is set', () => {
      const actual = createAuditItems({
        createdAt: '1-1-1970',
        updatedAt: null,
        createdBy: 'wouter',
        updatedBy: null,
      })

      expect(simplify(actual)).toEqual(['Created <datetime> by wouter'])
    })

    it('does not render updated at when it is the same as created at', () => {
      const actual = createAuditItems({
        createdAt: '1-1-1970',
        updatedAt: '1-1-1970',
        createdBy: null,
        updatedBy: null,
      })

      expect(simplify(actual)).toEqual(['Created <datetime>'])
    })

    it('renders updated at when it is different from created at', () => {
      const actual = createAuditItems({
        createdAt: '1-1-1970',
        updatedAt: '1-1-1971',
        createdBy: null,
        updatedBy: null,
      })

      expect(simplify(actual)).toEqual([
        'Created <datetime>',
        'Updated <datetime>',
      ])
    })

    it('adds the updator name if it is set', () => {
      const actual = createAuditItems({
        createdAt: '1-1-1970',
        updatedAt: '1-1-1971',
        createdBy: null,
        updatedBy: 'wouter',
      })

      expect(simplify(actual)).toEqual([
        'Created <datetime>',
        'Updated <datetime> by wouter',
      ])
    })
  })
})
