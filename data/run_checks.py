import json
import os
import subprocess
import sys

def run_causality_checks(data: dict, project_dir: str, causality: str) -> dict:
    """
    Run causality check JAR file, with provided input arguments.
    """
    output = dict()

    JAR_PATH = os.path.join(project_dir, "fyp-causality.jar")
    TRACE_DIR = os.path.join(project_dir, "input-files/")

    for trace in data.keys():  # For each trace file
        output.update({trace : dict()})
        for ltl in data.get(trace).keys():  # For each LTL property
            try:
                args = [
                    'java', '-jar', JAR_PATH, 
                    "-c", causality,
                    "-o", "pickled",
                    "-l", ltl,
                    "-t", TRACE_DIR + trace,
                ]
                process = subprocess.Popen(args, stdout=subprocess.PIPE, stderr=subprocess.PIPE)  # TODO: fix
                
                stdout, stderr = process.communicate()
                # print((stdout, stderr))
                output.get(trace).update({ltl : json.loads(stdout.decode('utf-8').strip())})

            except Exception as e:
                return str(e)

    return output

def write_to_file(json_file: str, output: dict) -> None:
    """
    Write structured outputs of JAR execution to JSON files.
    """
    try:
        with open(json_file, 'w') as f:
            json.dump(output, f, indent=2, sort_keys=True)
    except Exception as e:
        print(str(e))

if __name__ == "__main__":
    # Set up project directory
    if len(sys.argv) > 1:
        project_dir = sys.argv[1]
    else:
        project_dir = ".."

    if not os.path.isdir(project_dir):
        print(f"'{project_dir}' is not a valid directiory!")
    else:
        # Read input JSON
        input_json = "./input.json"
        output_json_beer = "./beer2011.json"
        output_json_meng = "./meng2024.json"

        with open(input_json, 'r') as f:
            data = json.load(f)

        # Run checks on both definitions
        print("Running checks (Beer2011)...")
        output_beer = run_causality_checks(data, project_dir, causality="beer2011")
        print("Running checks (Meng2024)...")
        output_meng = run_causality_checks(data, project_dir, causality="meng2024")

        # Write to files
        print("Writing outputs to files.")
        write_to_file(output_json_beer, output_beer)
        write_to_file(output_json_meng, output_meng)
