from collections import namedtuple
import matplotlib.pyplot as plt
import csv

resultfile = "../dining_result.202003101703.csv"
skip_last_files = 0

Result = namedtuple('Result', 'file formula algorithm result time_fancy time_ns iterations')
SimpleResult = namedtuple('SimpleResult', 'file result time_ns iterations')

matching_dict = {}
styles = ['s', 'o']
markersize = [7, 5]
colors = ['r', 'g', 'b', 'y']


def get_matching(data, matching):
    global matching_dict

    for match in matching:
        # try to find already used matching
        if match in matching_dict and matching_dict[match] == data:
            return match

    # find new match and set
    for match in matching:
        if match not in matching_dict:
            matching_dict[match] = data
            return match

    raise Exception(
        "No remaining free matching for {} and data {}, matching dict {}".format(matching, data, matching_dict))


def result_to_simple(result):
    return SimpleResult(result.file, result.result, int(result.time_ns), int(result.iterations))


def remove_duplicates(seq):
    seen = set()
    return [x for x in seq if not (x in seen or seen.add(x))]


with open(resultfile, newline='') as csvfile:
    reader = csv.reader(csvfile, delimiter=',')
    results = [Result(*row) for row in reader]

formulas = remove_duplicates([r.formula for r in results])
algorithms = remove_duplicates([r.algorithm for r in results])
files = remove_duplicates([r.file for r in results])
if skip_last_files > 0:
    files = files[:-skip_last_files]

result_dict = {}

for formula in formulas:
    result_dict[formula] = {}

    for algorithm in algorithms:
        result_dict[formula][algorithm] = []

        for r in [r for r in results if r.algorithm == algorithm and r.formula == formula]:
            result_dict[formula][algorithm].append(result_to_simple(r))


def get_results(f, alg):
    filtered = result_dict[f][alg]
    if skip_last_files > 0:
        filtered = filtered[:-skip_last_files]
    return filtered


def ns_to_s(ns):
    return ns / 1_000_000_000.0


for formula in formulas:
    fig, ax = plt.subplots()
    for algorithm in algorithms:
        ax.plot(files, [ns_to_s(r.time_ns) for r in get_results(formula, algorithm)],
                marker=get_matching(algorithm, styles), label=algorithm,
                markersize=get_matching(algorithm, markersize)
                )

    ax.legend()
    ax.set_ylabel('Time (seconds)')
    ax.set_xlabel('Labelled Transition System')
    plt.yscale('log')
    plt.title("Log scale of the evaluation time for\n'%s'" % (formula))
    plt.grid(True)

    fig.autofmt_xdate()
    fig.show()


for formula in formulas:
    fig, ax = plt.subplots()
    for algorithm in algorithms:
        ax.plot(files, [r.iterations for r in get_results(formula, algorithm)],
                marker=get_matching(algorithm, styles), label=algorithm,
                markersize=get_matching(algorithm, markersize)
                )

    ax.set_ylim(bottom=0)
    ax.legend()
    ax.set_ylabel('Iterations of eval()')
    ax.set_xlabel('Labelled Transition System')
    plt.title("Iteration count of eval() for\n'%s'" % (formula))
    plt.grid(True)

    fig.autofmt_xdate()
    fig.show()
