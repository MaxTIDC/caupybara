import json
import os
import pandas as pd

# Collect unique causes by spec
def unique_causes(data: dict) -> dict:
    result = dict()
    for key in ["assumptions", "assumptions_conjunct", "guarantees"]:
        result.update({key: dict()})

        for spec in data.keys():
            # Setup for set collection
            result[key].update({spec: set()})

            # Collect unique causes
            data_spec_key = data.get(spec).get(key)
            for ltl in data_spec_key.keys():
                data_spec_key_ltl = data_spec_key.get(ltl)
                for trace in data_spec_key_ltl.keys():
                    result[key][spec].update(to_tuple(cause) for cause in data_spec_key_ltl[trace])

    return result

### Statistics methods ###
def count_num_traces(data: dict) -> dict:
    result = dict()
    for spec in data.keys():
        result[spec] = len(next(iter(data[spec]["guarantees"].values())).keys())

    return result

def count_causes(data: dict) -> dict:
    result = data.copy()
    for spec in data.keys():
        for key in ["assumptions", "assumptions_conjunct", "guarantees"]:
            data_spec_key = data.get(spec).get(key)
            for ltl in data_spec_key.keys():
                data_spec_key_ltl = data_spec_key.get(ltl)
                for trace in data_spec_key_ltl.keys():
                    result[spec][key][ltl][trace] = len(data_spec_key_ltl[trace])

    return result 

def count_by_spec(causes_dict: dict) -> dict:
    # Count causes
    result = dict()
    for spec in causes_dict.keys():
        result.update({spec: len(causes_dict[spec])} )
    return result

def max_size_by_spec(causes_dict: dict) -> dict:
    result = dict()
    for spec in causes_dict.keys():
        causes = causes_dict[spec]
        result.update({spec: max(len(c) for c in causes) if causes else 0})
    return result

def min_size_by_spec(causes_dict: dict) -> dict:
    result = dict()
    for spec in causes_dict.keys():
        causes = causes_dict[spec]
        result.update({spec: min(len(c) for c in causes) if causes else 0})
    return result

def sum_size_by_spec(causes_dict: dict) -> dict:
    result = dict()
    for spec in causes_dict.keys():
        result.update({spec: sum(len(c) for c in causes_dict[spec])})
    return result

def mean_size_by_spec(sum_sizes_dict: dict, counts_dict: dict) -> dict:
    result = dict()
    for spec in counts_dict.keys():
        result.update({spec: float(sum_sizes_dict[spec]) / float(counts_dict[spec]) if counts_dict[spec] else 0})
    return result

### Coverage ###
def coverage_by_spec(meng_causes: dict, beer_causes: dict) -> dict:
    result = dict()
    for spec in beer_causes.keys():
        covered_count = 0
        for (state, atom) in beer_causes[spec]:
            if ((state, atom, False),) in meng_causes[spec] or ((state, atom, True),) in meng_causes[spec]:
                covered_count += 1
        result.update({spec: float(covered_count) / float(len(beer_causes[spec])) if beer_causes[spec] else float('nan')})

    return result

### Helper methods ###
def load_json(path_str: str) -> dict:
    processed_input = dict()
    with open(path_str, 'r') as f:
        raw_input = json.load(f)

        for k in raw_input.keys():
            raw_input[k]["trace_file"] = k
            k_new = os.path.split(os.path.splitext(raw_input[k]["spectra_file"])[0])[1]
            processed_input.update({k_new: raw_input[k]})

    return processed_input

def to_tuple(nested_list):
    if isinstance(nested_list, list):
        return tuple(to_tuple(item) for item in nested_list)
    return nested_list

### Main method ###
if __name__ == "__main__":
    # Set up project directory
    project_dir = os.getcwd()  # Assume script ran from project root

    # Read input JSONs
    input_data_beer = load_json(os.path.join(project_dir, "data", "beer2011.json"))
    input_data_meng = load_json(os.path.join(project_dir, "data", "meng2024.json"))

    # Perform statisics
    beer_unique_causes = unique_causes(input_data_beer)
    meng_unique_causes = unique_causes(input_data_meng)

    # Perform statisics
    for key in ["assumptions", "assumptions_conjunct", "guarantees"]:
        meng_sum_size = sum_size_by_spec(meng_unique_causes[key])
        meng_count = count_by_spec(meng_unique_causes[key])

        stats = pd.DataFrame({
            "beer_causes": count_by_spec(beer_unique_causes[key]),
            "meng_causes": count_by_spec(meng_unique_causes[key]),
            "meng_min_size": min_size_by_spec(meng_unique_causes[key]),
            "meng_max_size": max_size_by_spec(meng_unique_causes[key]),
            "meng_mean_size": mean_size_by_spec(meng_sum_size, meng_count),
            "coverage": coverage_by_spec(meng_unique_causes[key], beer_unique_causes[key])
        })
        stats["coverage"] = stats["coverage"].map('{:.0%}'.format)
        stats.sort_index(inplace=True)

        # Write DataFrame to CSV file
        stats.to_csv(os.path.join(project_dir, "data", "csv", f"{key}.csv"), index=True)

    print(beer_unique_causes["assumptions"]["lift_well_sep_dropped0"])
    print(meng_unique_causes["assumptions"]["lift_well_sep_dropped0"])
