// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the MIT
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: MIT

import { BalanceCalculator } from "@/components/BalanceCalculator";
import { Recorder } from "@/components/Recorder";
import { Transactions } from "@/components/Transactions";
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
  const balanceCalculator = useSWR(
    "http://localhost:9100/balances",
    fetchWithAccountId,
    { revalidateOnFocus: false }
  );
  const transactions = useSWR(
    "http://localhost:9101/transactions",
    fetchWithAccountId,
    { revalidateOnFocus: false }
  );
  return (
    <Container>
      <h1>NL69FAKE8085990849</h1>
      <BalanceCalculator data={balanceCalculator?.data as any} />

      <h2>Transactions</h2>
      <Transactions data={transactions?.data as any} />

      <Recorder />
    </Container>
  );
}
