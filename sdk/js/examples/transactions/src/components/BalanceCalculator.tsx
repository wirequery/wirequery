import { useState } from "react";

interface Balance {
  balance: number;
  totalPerCurrency: { [currency: string]: number };
}

export interface BalanceCalculatorProps {
  data?: Balance;
}

export const BalanceCalculator = (props: BalanceCalculatorProps) => {

  const [showDetails, setShowDetails] = useState(false);

  return <div>
    <div>
      Total balance: {props.data?.balance}
    </div>
    <div>
      <button onClick={() => setShowDetails(!showDetails)}>{showDetails ? 'Hide' : 'Show'} details</button>
    </div>

    {showDetails &&
      <table style={{ width: 300 }}>
        <thead>
          <tr>
            <th>Currency</th>
            <th>Transfered</th>
          </tr>
        </thead>
        <tbody>
          {Object.keys(props.data?.totalPerCurrency ?? {}).map((currency) => (
            <tr>
              <td>{currency}</td>
              <td>{props.data.totalPerCurrency[currency]}</td>
            </tr>
          ))}
        </tbody>
      </table>}
  </div>
};

