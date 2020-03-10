from collections import namedtuple
import matplotlib.pyplot as plt
import csv
import os

fig, ax = plt.subplots()
ax.plot(['dining_%d.aut' % x for x in [2, 3, 4, 5, 6, 7, 8, 9, 10, 11]],
        [12, 66, 300, 1250, 4968, 19159, 72336, 268794, 986430, 3583778],
        marker="o"
        )

ax.set_ylim(bottom=0)
ax.legend()
ax.set_ylabel('#nodes')
ax.set_xlabel('LTS')

plt.title("Node count for the Dining Philosophers LTS's")
plt.grid(True)
fig.autofmt_xdate()

fig.show()

fig.savefig("node_count.pdf", bbox_inches='tight')
