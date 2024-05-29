import json
import os

def load_json(path_str: str) -> dict:
    processed_input = dict()
    with open(path_str, 'r') as f:
        raw_input = json.load(f)

        for k in raw_input.keys():
            k_new = os.path.split(os.path.splitext(k)[0])[1]
            processed_input.update({k_new: raw_input[k]})

    return processed_input

if __name__ == "__main__":
    # Set up project directory
    project_dir = os.getcwd()  # Assume script ran from project root

    # Read input JSONs
    input_dict_beer = load_json(os.path.join(project_dir, "data", "beer2011.json"))
    input_dict_meng = load_json(os.path.join(project_dir, "data", "meng2024.json"))

    print(input_dict_beer["genbuf_05_normalised_dropped107_auto_violation"]["assumptions"])
    print(input_dict_meng["genbuf_05_normalised_dropped107_auto_violation"]["assumptions_conjunct"])
