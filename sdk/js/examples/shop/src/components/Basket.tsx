// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

import { Button, Table } from "@mantine/core";
import useSWR, { useSWRConfig } from "swr";

interface BasketEntry {
  id: string;
  name: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
}

export const Basket = () => {
  const { data: entries } = useSWR<BasketEntry[]>('/basket-entries');
  const { mutate } = useSWRConfig()

  const removeFromCart = (productId: string) => {
    fetch(`/basket-entries/${productId}`, {
      method: "DELETE",
      headers: { accountId: "123" },
    }).then(() => mutate('/basket-entries'))
  }

  if (!entries) {
    return <></>
  }

  return <div>
    {entries?.length === 0 ? <>No basket entries.</> : <Table>
      <Table.Thead>
        <Table.Tr>
          <Table.Th>Name</Table.Th>
          <Table.Th>Quantity</Table.Th>
          <Table.Th>Unit Price</Table.Th>
          <Table.Th>Total Price</Table.Th>
          <Table.Th>Action</Table.Th>
        </Table.Tr>
      </Table.Thead>
      <Table.Tbody>{entries?.map(entry => (
        <Table.Tr key={entry.id}>
          <Table.Td>{entry.name}</Table.Td>
          <Table.Td>{entry.quantity}</Table.Td>
          <Table.Td>{Intl.NumberFormat("de-DE", { style: "currency", currency: "EUR" }).format(entry.unitPrice / 100)}</Table.Td>
          <Table.Td>{Intl.NumberFormat("de-DE", { style: "currency", currency: "EUR" }).format(entry.totalPrice / 100)}</Table.Td>
          <Table.Td>
            <Button onClick={() => removeFromCart(entry.id)}>Remove from Cart</Button>
          </Table.Td>
        </Table.Tr>
      ))}
      </Table.Tbody>
    </Table>}
  </div>
};

