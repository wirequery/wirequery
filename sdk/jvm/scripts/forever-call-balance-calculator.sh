#!/bin/bash

# This script is intended to simulate production traffic, by calling the balances endpoint of the balance-calculator infinitely.

while :
do
    curl http://localhost:9100/balances -H 'AccountId: NL69FAKE8085990849'
done
