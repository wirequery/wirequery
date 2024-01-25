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

export default function Home() {
  return (
    <Container>
      <h1>The Computer Shop</h1>

      <h2>Our Assortment</h2>
      <Products />

      <h2>Your Shopping Basket</h2>
      <Basket />

      <Recorder />
    </Container>
  );
}
