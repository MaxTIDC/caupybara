import json
import os
import subprocess
import sys
import time

def run_causality_checks(data: dict, project_dir: str, causality: str, use_jar: bool) -> dict:
    """
    Run causality check executable file, with provided input arguments.
    """

    def run_subprocess(args: list) -> list:
        try:
            process = subprocess.Popen(args, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            stdout, stderr = process.communicate()
            if stderr:
                print("ERROR: " + stderr.decode('utf-8').strip())
                return [stderr.decode('utf-8').strip()]
            return json.loads(stdout.decode('utf-8').strip())

        except Exception as e:
            print("Exception: " + str(e))
            return [str(e)]

    output = data.copy()

    jar_path = os.path.join(project_dir, "bin", "caupybara.jar")
    bin_path = os.path.join(project_dir, "bin", 
                            "caupybara" + (".exe" if sys.platform.startswith("win") else ""))
#     trace_dir = os.path.join(project_dir, "input-files/")

    if not use_jar and (sys.platform.startswith("win") or sys.platform.startswith("linux")):
        print(f"Running binary ({sys.platform}): {bin_path}")
        args_head = [ bin_path ]  
    else:
        print(f"Running jar ({sys.platform}): {jar_path}")
        args_head = [ 'java', '-jar', jar_path ]

    for trace in data.keys():  # For each trace file
        for key in ["assumptions", "assumptions_conjunct", "guarantees"]:
            for ltl in data.get(trace).get(key).keys():  # For each LTL assumption
                args_tail = ["-c", causality, "-l", ltl, "-t", os.path.join(project_dir, trace), "-b", "4"]

                cause_list = run_subprocess(args_head + args_tail)
                output.get(trace).get(key).update({ltl : cause_list})
                
                # for trace_name in cause_list.keys():
                #     output.get(trace).get(key).get(trace_name).update({ltl : cause_list[trace_name]})

    return output

def write_to_file(json_file: str, output: dict) -> None:
    """
    Write output dictionary to JSON files.
    """
    try:
        with open(json_file, 'w') as f:
            json.dump(output, f, indent=2, sort_keys=True)
    except Exception as e:
        print(str(e))

if __name__ == "__main__":
    # Set up project directory
    if len(sys.argv) > 1 and sys.argv[1] != "jar":
        project_dir = sys.argv[1]
    else:
        project_dir = os.path.abspath(os.path.join(os.getcwd(), "."))  # Assume script ran from project root

    use_jar = ("jar" in sys.argv) if len(sys.argv) > 1 else False

    if not os.path.isdir(project_dir):
        print(f"'{project_dir}' is not a valid directiory!")
    else:
        # Read input JSON
        input_json = os.path.join(project_dir, "data", "input.json")
        output_json_beer = os.path.join(project_dir, "data", "beer2011.json")
        output_json_meng = os.path.join(project_dir, "data", "meng2024.json")

        with open(input_json, 'r') as f:
            data = json.load(f)

        # Run checks on both definitions
        print("Running checks (Beer2011)...")
        timer = time.perf_counter()
        output_beer = run_causality_checks(data, project_dir, "beer2011", use_jar)
        elapsed_time = time.perf_counter() - timer
        print(f"Checks (Beer2011) complete, took {elapsed_time} seconds")

        print("Writing outputs to files.")
        write_to_file(output_json_beer, output_beer)

        print("Running checks (Meng2024)...")
        timer = time.perf_counter()
        output_meng = run_causality_checks(data, project_dir, "meng2024", use_jar)
        elapsed_time = time.perf_counter() - timer
        print(f"Checks (Meng2024) complete, took {elapsed_time} seconds")

        print("Writing outputs to files.")
        write_to_file(output_json_meng, output_meng)
