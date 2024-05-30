## Caupybara

Cause computation for LTL formula violations in counterexamples.

### Usage

```angular2html
caupybara [--ltl | -l LTL property string] [--trace | -t trace file path] [--cause | -c causality mode] [--out | -o output mode]
```

- `-ltl` / `-l`: Violated LTL property (formula) in string format.
- `--trace` / `-t`: File describing the trace where the violation occurred. See `input-files` for format.
- `--cause` / `-c`: Causality mode. Currently supported: `beer2011` (Beer et al. 2011), `meng2024`.
- `--out` / `-o`: Output mode, `original` for original Scala AST format, otherwise as pickled version (default).

Pre-compiled binaries for Linux and Windows (recommended), JAR files are available under `./bin`.

#### Examples

##### Linux binary (recommended)
```angular2html
./bin/caupybara -l 'G((!req1 & !req2) | X ack)' -t ./input-files/custom/req_ack_violation_1.txt -c meng2024
```

##### Windows executable (recommended)
```angular2html
./bin/caupybara.exe -l 'G((!req1 & !req2) | X ack)' -t ./input-files/custom/req_ack_violation_1.txt -c meng2024
```

##### JAR
```angular2html
java -jar ./bin/caupybara.jar -l 'G((!req1 & !req2) | X ack)' -t ./input-files/custom/req_ack_violation_1.txt -c meng2024
```

### Dependencies

#### Running pre-built Jar

JRE for Java SE 21 or newer, e.g. [OpenJDK](https://jdk.java.net/22/).

#### Compiling locally

[GraalVM](https://www.graalvm.org/downloads/) can be used to produce AOT optimized binaries. See [Native Image](https://www.graalvm.org/jdk21/reference-manual/native-image/) for details.

Script used to pre-build binaries (for reference):

```angular2html
native-image -O3 -jar ./bin/caupybara.jar --no-fallback -o ./bin/caupybara
```
