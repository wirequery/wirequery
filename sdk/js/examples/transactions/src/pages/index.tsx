import { BalanceCalculator } from "@/components/BalanceCalculator";
import { Recorder } from "@/components/Recorder";
import { Transactions } from "@/components/Transactions";
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
    <>
      <h1>Transactions - NL69FAKE8085990849</h1>
      <h2>Balance Summary</h2>
      <BalanceCalculator data={balanceCalculator?.data as any} />

      <h2>List of Transactions</h2>
      <Transactions data={transactions?.data as any} />

      <Recorder />
    </>
  );
}
