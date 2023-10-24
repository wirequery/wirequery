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
  <table>
    <thead>
      <tr>
        <th>Account</th>
        <th>Currency</th>
        <th>Amount</th>
        <th>Description</th>
      </tr>
    </thead>
    <tbody>
      {props.data?.map((t: Transaction) => (
        <tr key={t.id}>
          <td>{t.toAccount}</td>
          <td>{t.currency}</td>
          <td>{t.type === 'DEBIT' ? -t.actualAmount : t.actualAmount}</td>
          <td>{t.description}</td>
        </tr>
      ))}
    </tbody>
  </table>
);
