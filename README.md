# Calculator on Java

## Introduction
Simple [Java Calculator](./src/Calculator.java) (click to go to the source code) using `ArrayDeque`'s.

## What can you get here?
This calculator supports:
- [X] Unary operators (only `-`) - '+' is not considered unary somehow
- [X] Parentheses
- [X] Operators order (left -> right for `+,-,*,/`)
- [X] `1 / 0 == Error`
- [X] but `1 / (1 / 0) == 0`

## Why i don't use `BigDecimal`
We don't need precision due to task, but i really want two end cases to work properly.
Now we have `Double` in calculations and in all code overall 
except the part where we need to round values and trim trailing zeroes
\- here I used `BigDecimal`.

## Contacts
For any inquiries or questions, feel free to [contact me](mailto:skirtsfield@gmail.com) or reach out on [Telegram](https://t.me/skirtsfield).
