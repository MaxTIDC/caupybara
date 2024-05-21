#import re
import json
import os
import sys

USAGE = """
Usage: python process_spectra.py [Spectra file]
"""

def process_spectra(file_path):
    with open(file_path, 'r') as file:
        lines = file.readlines()
    
    spec_name = ""
    env_vars = []
    sys_vars = []
    assumptions = dict()
    guarantees = dict()
    
    for i in range(len(lines)):
        line = lines[i].strip()
        
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
            assumption_formula = lines[i + 1].strip().strip(';')
            assumptions[assumption_formula] = []
        
        # Process guarantees
        elif "guarantee" in line or "gar" in line:
            guarantee_formula = lines[i + 1].strip().strip(';')
            guarantees[guarantee_formula] = []
    
    # Return processed file as a dictionary
    return {
        "spec_name": spec_name,
        "env": env_vars,
        "sys": sys_vars,
        "assumptions": assumptions,
        "guarantees": guarantees
    }

if __name__ == "__main__":
    # Read file directory
    if len(sys.argv) <= 1:
        print(USAGE)
    else:
        input_path = sys.argv[1]
        processed_dict = process_spectra(input_path)

        output_path = f"{os.path.splitext(input_path)[0]}.json"
        # Print processed data
        try:
            with open(output_path, 'w') as f:
                json.dump(processed_dict, f, indent=2)
        except Exception as e:
            print(str(e))
#         print(json.dumps(processed_dict, indent=2))
