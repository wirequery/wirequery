// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

import { Basket } from "@/components/Basket";
import { Recorder } from "@/components/Recorder";
import { Products } from "@/components/Products";
import { Container } from "@mantine/core";
import useSWR from "swr";

const fetchWithAccountId = (url: string, fetchArgs: any = {}): Promise<Response> => {
  const result = fetch(url, {
    ...fetchArgs,
    headers: { accountId: "NL69FAKE8085990849" },
  });
  return result.then((res) => res.json());
};

export default function Home() {
  const basket = useSWR(
    "http://localhost:9100/basket-entries",
    fetchWithAccountId,
    { revalidateOnFocus: false }
  );
  const products = useSWR(
    "http://localhost:9101/products",
    fetchWithAccountId,
    { revalidateOnFocus: false }
  );
  return (
    <Container>
      <h1>The Computer Shop</h1>

      <h2>Your Shopping Basket</h2>
      <Basket data={basket?.data as any} />

      <h2>Our Assortment</h2>
      <Products data={products?.data as any} />

      <Recorder />
    </Container>
  );
}
