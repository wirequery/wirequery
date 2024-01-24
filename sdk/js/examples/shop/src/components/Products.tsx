// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

import { Divider, Text } from "@mantine/core";

interface Product {
  id: number,
  name: string,
  price: number,
}

export interface ShopProps {
  data?: Product[];
}

export const Products = (props: ShopProps) => (
  <>
    {props.data?.map((t: Product, i) => (
      <div key={i}>
        <div style={{ float: 'right' }}>
          <Text fw={500} size="lg" mt="md">{t.name}</Text>
        </div>
        <Text fw={500} size="lg" mt="md">{t.price}</Text>
      </div>
    ))}</>
);
