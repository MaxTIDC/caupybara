## Caupybara

Cause computation for LTL formula violations in counterexamples.

### Usage

```angular2html
caupybara [--ltl | -l] [--trace | -t] ([--cause | -c] [--bound | -b])
```

- `-ltl` / `-l`: Violated LTL property (formula), in string format.
- `--trace` / `-t`: File containing the counterexample trace. See `input-files` for format.
- `--cause` / `-c`: Causality mode, supported: `beer2011` (Beer et al. 2011), `meng2024`. Default = `meng2024`.
- `--bound` / `-b`: Upper bound on size of causes (>= 1). Default = 5.

Pre-compiled binaries for Linux / Windows (recommended) and JAR files are available under `./bin`.

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

### Evaluation

Raw data and statistics for evaluation computed by automated Python scripts under `./data`. For dependencies
see `./data/requirements.txt`.

To recreate the evaluation results, run the following on command line under root directory:

```angular2html
python ./data/process_spectra.py ./input-files/buckworth2023 ./data/input.json
python ./data/run_checks.py .
python ./data/analysis.py
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
