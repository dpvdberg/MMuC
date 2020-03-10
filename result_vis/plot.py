from collections import namedtuple
import matplotlib.pyplot as plt
import csv

resultfile = "../dining_result.202003091625.csv"

Result = namedtuple('Result', 'file formula algorithm result time_fancy time_ms iterations')
SimpleResult = namedtuple('SimpleResult', 'file result time_ms iterations')


def result_to_simple(result):
    return SimpleResult(result.file, result.result, int(result.time_ms), int(result.iterations))


def remove_duplicates(seq):
    seen = set()
    return [x for x in seq if not (x in seen or seen.add(x))]


with open(resultfile, newline='') as csvfile:
    reader = csv.reader(csvfile, delimiter=',')
    results = [Result(*row) for row in reader]

formulas = remove_duplicates([r.formula for r in results])
algorithms = remove_duplicates([r.algorithm for r in results])
files = remove_duplicates([r.file for r in results])[:-1]

result_dict = {}

for formula in formulas:
    result_dict[formula] = {}

    for algorithm in algorithms:
        result_dict[formula][algorithm] = []

        for r in [r for r in results if r.algorithm == algorithm and r.formula == formula]:
            result_dict[formula][algorithm].append(result_to_simple(r))

for formula in formulas:
    for algorithm in algorithms:
        # plt.plot(files, [r.time_ms for r in result_dict[formula][algorithm]], 'ro')
        # plt.show()

        fig, ax = plt.subplots()
        ax.plot(files, [r.time_ms for r in result_dict[formula][algorithm]], 'ro')
        fig.autofmt_xdate()
        fig.show()
        # fig.xticks(range(0, len(files) + 1))
        # ax.tick_params(axis='both', which='major')
        # ax.set_xticklabels(files, rotation=45)
        # fig.savefig('test_rotation.png', dpi=300, format='png', bbox_inches='tight')
