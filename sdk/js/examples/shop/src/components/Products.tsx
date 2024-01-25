// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

import { Button, Text } from "@mantine/core";
import useSWR, { useSWRConfig } from "swr"

interface Product {
  id: string,
  name: string,
  price: number,
}


export const Products = () => {
  const { data: products } = useSWR<Product[]>("/products");
  const { mutate } = useSWRConfig()

  const addToCart = (productId: string) => {
    fetch(`/basket-entries/${productId}`, {
      method: "POST",
      headers: { 'Content-Type': 'application/json', accountId: "123" },
      body: JSON.stringify({ quantity: 1 })
    }).then(() => mutate('/basket-entries'))
  }

  if (!products) {
    return <></>
  }

  return (
    <>
      {products.map((product, i: number) => (
        <div key={i}>
          <div style={{ float: 'right' }}>
            <Text fw={500} size="lg" mt="md">{Intl.NumberFormat("de-DE", {
              style: "currency",
              currency: "EUR"
            }).format(product.price / 100)}</Text>
          </div>
          <Text fw={500} size="lg" mt="md">{product.name}</Text>
          <Button onClick={() => addToCart(product.id)}>Add To Cart</Button>
        </div>
      ))}</>
  );
}
