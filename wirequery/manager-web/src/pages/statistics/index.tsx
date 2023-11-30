// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

import Link from 'next/link'
import { StatisticList } from '@components/shared/app/statistic/StatisticList'

export default function Statistics() {
  return (
    <>
      <h1>Navigation</h1>
      <ul>
        <li>
          <Link href="/statistics/new">New</Link>
        </li>
      </ul>

      <h1>List of Statistics</h1>
      <StatisticList />
    </>
  )
}
