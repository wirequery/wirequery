// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

import { Card, Divider, Text } from "@mantine/core";

interface Transaction {
  id: number,
  amount: number,
  actualAmount: number,
  currency: string,
  type: string,
  fromAccount: string,
  toAccount: string,
  description: string,
}

export interface TransactionsProps {
  data?: Transaction[];
}

export const Transactions = (props: TransactionsProps) => (
  <>
    {props.data?.map((t: Transaction, i) => (
      <div key={i}>
        <div style={{ float: 'right' }}>
          <Text fw={500} size="lg" mt="md">{t.currency} {t.type === 'DEBIT' ? -t.actualAmount : t.actualAmount}</Text>
        </div>
        <Text fw={500} size="lg" mt="md">{t.description}</Text>
        <Text mt="xs" c="dimmed" size="sm">{t.toAccount}</Text>
        {i < (props?.data?.length ?? 0) - 1 ? <Divider mb='sm' mt='sm' /> : <></>}
      </div>
    ))}</>
);
