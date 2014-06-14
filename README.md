#Java-BigInt
===========

Programming Exercise: Implement a Big Integer class

###Example
===========
Code:
```java
CharBigInt a = new CharBigInt(0);
CharBigInt b = new CharBigInt(Long.MAX_VALUE);
for (int i = 0; i < 1000; i++) {
    System.out.printf("%s + %s\n", a, b);
    a = a.add(b);
    System.out.println("sum: " + a);
}
```
Output:
```
...
9177255176670501927965 + 9223372036854775807
sum: 9186478548707356703772
9186478548707356703772 + 9223372036854775807
sum: 9195701920744211479579
9195701920744211479579 + 9223372036854775807
sum: 9204925292781066255386
9204925292781066255386 + 9223372036854775807
sum: 9214148664817921031193
9214148664817921031193 + 9223372036854775807
sum: 9223372036854775807000
```

