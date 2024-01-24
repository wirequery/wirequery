#!/bin/bash

# This script is intended to simulate production traffic, by calling the balances endpoint of the balance-calculator infinitely.

while :
do
    curl http://localhost:9100/basket-entries -H 'AccountId: 123'
done
