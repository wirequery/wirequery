// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

import { Button } from "@mantine/core";
import { useState } from "react";

interface BasketEntry {
  id: string;
  name: string;
  amount: number;
  unitPrice: number;
  totalPrice: number;
}

export interface BasketProps {
  data?: BasketEntry[];
}

export const Basket = (props: BasketProps) => {
  return <div>
    {<table style={{ width: 300 }}>
      <thead>
        <tr>
          <th>Name</th>
          <th>Amount</th>
          <th>Unit Price</th>
          <th>Total Price</th>
        </tr>
      </thead>
      <tbody>
      </tbody>
    </table>}
  </div>
};

