#import re
import json
import os
import sys

USAGE = """
Usage: python process_spectra.py [Input file path / directory] ([Output file path])
"""

def process_spectra(file_path):
    with open(file_path, 'r') as file:
        lines = file.readlines()
    
    spec_name = ""
    env_vars = []
    sys_vars = []
    assumptions = dict()
    assumptions_conjunct = dict()
    guarantees = dict()
    
    for i in range(len(lines)):
        line = lines[i].strip()

        # Skip over commented lines / PREV operators
        if line.startswith("//"):
            continue
        elif i + 1 < len(lines) and "PREV" in lines[i + 1]:
            continue
        
        # Specification name
        if "module" in line or "spec" in line:
            spec_name = line.split()[1]
        
        # Environment variables (booleans only)
        elif "env boolean" in line:
            env_vars.append(line.split()[2].strip(';'))
        
        # System variables (booleans only)
        elif "sys boolean" in line:
            sys_vars.append(line.split()[2].strip(';'))
        
        # Process assumptions
        elif "assumption" in line or "asm" in line:
            assumption_formula = lines[i+1].strip().strip(';')
            assumptions[assumption_formula] = []
        
        # Process guarantees
        elif "guarantee" in line or "gar" in line:
            guarantee_formula = lines[i+1].strip().strip(';')
            guarantees[guarantee_formula] = []

    assumptions_conjunct[" & ".join(assumptions.keys())] = []

    # Return processed file as a dictionary
    return {
        "spec_name": spec_name,
        "env": env_vars,
        "sys": sys_vars,
        "assumptions": assumptions,
        "assumptions_conjunct": assumptions_conjunct,
        "guarantees": guarantees
    }

if __name__ == "__main__":
    if len(sys.argv) < 2 or len(sys.argv) > 3:
        print(USAGE)
        exit()

    # Read file path / directory
    input_path = sys.argv[1]
    if len(sys.argv) <= 2:  # If output path not specified
        output_path = f"{os.path.splitext(input_path)[0]}.json"
    else:
        output_path = sys.argv[2]

    # Process inputs
    processed_dict = dict()
    if os.path.isfile(input_path):  # If input is a single file
        processed_dict = process_spectra(input_path)
    elif os.path.isdir(input_path):  # Otherwise, if input is a directory
        # Batch process all Spectra files + corresponding trace files
        spectra_path = os.path.join(input_path, "mutated_specifications")  # TODO: more adaptive
        traces_path = os.path.join(input_path, "violation_files")
        
        for filename in os.listdir(spectra_path):
            spectra_file = os.path.join(spectra_path, filename)
            traces_file = os.path.join(traces_path, f"{os.path.splitext(filename)[0]}_auto_violation.txt" )

            processed = process_spectra(spectra_file)
            processed["spectra_file"] = spectra_file
            processed_dict[traces_file] = processed

    # Write processed data to a single JSON file
    try:
        with open(output_path, 'w') as f:
            json.dump(processed_dict, f, indent=2)
    except Exception as e:
        print(str(e))
