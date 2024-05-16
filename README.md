## FYP-Causality

Cause computation tool for LTL properties violation.

### Dependencies

#### Running pre-built Jar (under project root)

JRE for Java SE 21 or newer, e.g. [OpenJDK](https://jdk.java.net/22/).

### Usage

```angular2html
java -jar fyp-causality.jar [--ltl | -l LTL property string] [--trace | -t trace file path] [--cause | -c causality mode] [--out | -o output mode]
```
- `-ltl` / `-l`: Violated LTL property (formula) in string format.
- `--trace` / `-t`: File describing the trace where the violation occurred. See `input-files` for format.
- `--cause` / `-c`: Causality mode. Currently supported: `beer2011` (Beer et al. 2011), `meng2024`.
- `--out` / `-o`: Output mode, `pickled` for pickled version, otherwise as default Scala format.

Example: 
```angular2html
java -jar fyp-causality.jar -l 'G((!req1 & !req2) | X ack)' -t ./input-files/Beer2011/req_ack_violation_1.txt -c meng2024 -o pickle
```
