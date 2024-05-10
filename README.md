## FYP-Causality

Cause computation tool for LTL properties violation.

### Dependencies

#### Run pre-built Jar under `out/artifacts/fyp_causality_jar`

JRE for Java SE 21 or newer, e.g. `https://jdk.java.net/21/`.

#### Compile locally

// TODO

### Usage

```angular2html
java -jar fyp-causality.jar [--ltl | -l LTL property string] [--trace | -t trace file path] [--cause | -c causality mode] [--out | -o output mode]
```
- `-ltl` / `-l`: Violated LTL property (formula) in string format.
- `--trace` / `-t`: File describing the trace where the violation occurred. See `input-files` for format.
- `--cause` / `-c`: Causality mode. Currently supported: `beer2011` (Beer et al. 2011), `herong2024`.
- `--out` / `-o`: Output mode, `pickled` for pickled version, otherwise as default Scala format.