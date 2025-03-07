## Caupybara

Tool for computing causes to LTL property violations in counterexamples.

### Usage

```angular2html
caupybara [--ltl | -l] [--trace | -t] ([--cause | -c] [--bound | -b])
```

- `-ltl` / `-l`: Violated LTL property (formula), in string format.
- `--trace` / `-t`: File containing the counterexample trace. See `input-files` for format.
- `--cause` / `-c`: Causality mode, supported: `beer` (Beer et al. 2011), `new` (newly revised). Default = `new`.
- `--bound` / `-b`: Upper bound on size of causes (>= 1). Default = 5.

Pre-compiled binaries for Linux / Windows (recommended) and JAR files are available under `./bin/`.

#### Examples

##### Linux x86-64 binary (recommended)
```angular2html
./bin/caupybara -l 'G(HighWater -> X(Pump)) & G(Methane -> X(!Pump))' -t ./input-files/custom/minepump_1.txt
```

##### JAR
```angular2html
java -jar ./bin/caupybara.jar -l 'G(HighWater -> X(Pump)) & G(Methane -> X(!Pump))' -t ./input-files/custom/minepump_1.txt
```

##### Windows executable

Requires compiling locally (see below)

```angular2html
./bin/caupybara.exe -l 'G(HighWater -> X(Pump)) & G(Methane -> X(!Pump))' -t ./input-files/custom/minepump_1.txt
```

### Evaluation

Raw data and statistics for evaluation computed by automated Python scripts under `./data/`. For dependencies
see `./data/requirements.txt`.

To recreate the evaluation results, remove all existing files under `./data/json/` and `./data/csv/`, then run the
following on command line under root directory in order:

```angular2html
python ./data/process_spectra.py ./input-files/buckworth2023 ./data/json/input.json
python ./data/run_checks.py .
python ./data/analysis.py
```

And the new CSV tables under `./data/csv/` should show replicated raw data.

### Dependencies

#### Running pre-built Jar

JRE for Java SE 21 or newer, e.g. [OpenJDK](https://jdk.java.net/22/).

#### Compiling locally

[GraalVM](https://www.graalvm.org/downloads/) can be used to produce AOT optimized binaries. See [Native Image](https://www.graalvm.org/jdk21/reference-manual/native-image/) for details.

Native Image script for building binaries (for reference):

```angular2html
native-image -O3 -jar ./bin/caupybara.jar --no-fallback -o ./bin/caupybara
```
