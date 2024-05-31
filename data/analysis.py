import json
import os
import pandas as pd

### Statistics methods ###
def count_num_traces(data: dict) -> dict:  # Tested
    result = dict()
    for spec in data.keys():
        result[spec] = len(next(iter(data[spec]["guarantees"].values())).keys())

    return result

def count_causes(data: dict) -> dict:  # Tested
    result = data.copy()
    for spec in data.keys():
        for key in ["assumptions", "assumptions_conjunct", "guarantees"]:
            data_spec_key = data.get(spec).get(key)
            for ltl in data_spec_key.keys():
                data_spec_key_ltl = data_spec_key.get(ltl)
                for trace in data_spec_key_ltl.keys():
                    result[spec][key][ltl][trace] = len(data_spec_key_ltl[trace])

    return result 

def unique_causes_by_trace(data: dict) -> dict:  # Tested
    result = dict()
    for key in ["assumptions", "assumptions_conjunct", "guarantees"]:
        result.update({key: dict()})

        for spec in data.keys():
            traces = next(iter(data[spec]["guarantees"].values())).keys()
            result[key].update({spec: dict()})

            # Setup for set collection
            for t in traces:
                result[key][spec].update({t: set()})

            # Collect unique causes
            data_spec_key = data.get(spec).get(key)
            for ltl in data_spec_key.keys():
                data_spec_key_ltl = data_spec_key.get(ltl)
                for trace in data_spec_key_ltl.keys():
                    result[key][spec][trace].update(to_tuple(cause) for cause in data_spec_key_ltl[trace])

            # # Count causes
            # for t in traces:
            #     result[key][spec].update({t: len(result[key][spec][t])})

    return result 

def unique_causes_by_spec(data: dict) -> dict:
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
        max_size = 0

        for c in causes:
            max_size = max(len(c), max_size)

        result.update({spec: max_size} )

    return result

def count_by_trace(causes_dict: dict) -> dict:
    # Count causes
    result = dict()
    for spec in causes_dict.keys():
        result.update({spec: dict()})
        for trace in causes_dict[spec].keys():
            result[spec].update({trace: len(causes_dict[spec][trace])})

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
    beer_unique_causes_by_spec = unique_causes_by_spec(input_data_beer)
    meng_unique_causes_by_spec = unique_causes_by_spec(input_data_meng)
    # print(count_num_traces(input_data_beer)["traffic_single_FINAL_dropped0"])
    # print(count_unique_causes_by_trace(input_data_beer)["traffic_single_FINAL_dropped1"]["assumptions"]["trace_name_8"])

    # Perform statisics
    stats = dict()
    for key in ["assumptions", "assumptions_conjunct", "guarantees"]:
        stats[key] = pd.DataFrame({
            "beer_causes_by_spec": count_by_spec(beer_unique_causes_by_spec[key]),
            "meng_causes_by_spec": count_by_spec(meng_unique_causes_by_spec[key]),
            "meng_max_size": max_size_by_spec(meng_unique_causes_by_spec[key]),
        })

        # Write DataFrame to CSV file
        stats[key].to_csv(os.path.join(project_dir, "data", "csv", f"{key}.csv"), index=True)
        # print(stats[key])

    # TODO: integrate into table
    beer_unique_causes_by_trace = unique_causes_by_trace(input_data_beer)
    meng_unique_causes_by_trace = unique_causes_by_trace(input_data_meng)

    # print(beer_unique_causes_by_trace["assumptions"]["traffic_single_FINAL_dropped1"])
    # print(count_by_trace(beer_unique_causes_by_trace["assumptions"])["traffic_single_FINAL_dropped1"])

    # print(meng_unique_causes_by_trace["assumptions"]["traffic_single_FINAL_dropped10"])
    # print(count_by_trace(meng_unique_causes_by_trace["assumptions"])["traffic_single_FINAL_dropped10"])
