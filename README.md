# Calculator on Java

## Introduction
Simple Java calculator using `ArrayDeque`'s.

## How to use
- Download this repo
```bash
git clone https://github.com/skirt-owner/java-calculator.git
```
- Compile project
```java 
javac src/Calculator.java
```
- Run it providing with `args` or `stdin`
```java
java -classpath src Calculator "<your equation here>"
```

## What can you get here?
This calculator supports:
- [X] Unary operators (only `+,-`)
- [X] Parentheses
- [X] Operators order (left -> right for `+,-,*,/`)
- [X] `1 / 0 == Infinity`
- [X] `1 / (1 / 0) == 0`

## Why i don't use `BigDecimal`
We don't need precision due to task, but i really want two end cases to work properly.
Now we have `Double` in calculations and in all code overall 
except the part where we need to round values and trim trailing zeroes
\- here I used `BigDecimal`.

## Contacts
For any inquiries or questions, feel free to [contact me](mailto:skirtsfield@gmail.com) or reach out on [Telegram](https://t.me/skirtsfield).
