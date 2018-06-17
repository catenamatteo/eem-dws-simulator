# Simulator for '**Efficient Energy Management in Web Search Engines**'

## Usage:
`java -cp eem-dws-simulator-0.0.1-SNAPSHOT-jar-with-dependencies.jar simulator.Simulation METHOD INPUT_PATH NUM_OF_REPLICAS SIMULATION_HOURS OUTPUT_PATH`

METHOD: it can be 
* *perf*: run all the cpu cores at maximum CPU frequency,
* *pegasus*: do power management using PEGASUS [1],
* *pesos*: do power management using PESOS [2],

INPUT_PATH: it is the path where the queries are (e.g., resources/msn.day2.qid.txt),

NUM_OF_REPLICAS: the number of replicas you want in your search engine,

SIMULATION_HOURS: the number of simulated hours, relatively to the input (e.g., msn.day2.qid.txt is a 24-hrs query log),

OUTPUT_PATH: the path where the output is going to be saved (the produced file will be a .gz)

##Input format
The input format has a line for each (simulated) query sent to the search engine. E.g.,
```
0 2378
250 2911
500 8458
750 1522
```
This snippet shows 4 queries. At millisecond 0 (1st field), the query with id 2378 (2nd field) is sent to the system; at millisecond 250 the query with id 2911 is sent to the system; and so on. 

## Output format
The output file has two kinds of entries:
* *broker*, and
* *energy*.

For each query in the input, we will have a broker line in the output, like:
```
[broker] 4 14.000`
```
This tell us that the search engine has received a query at second 4 (2nd field) and that its *completion time* was 14 milliseconds (3rd field).

Also, for each (simulated) second we will have an energy line in the output, like:
```
[energy] 86395 47.728`
```
This tell us that the search engine has consumed 47.728 Joules at second 86395 of the simulation.

##How to munge the output file
To get the energy consumption (1 entry per second, value in Joules):  
`zcat output.gz | grep energy | cut -f3 -d' ' > output.energy`

To get the 95th-tile latency (1 entry per second, 30-seconds moving 95th-tile latency in ms):  
`python3 scripts/mungetime-gzip.py output.gz > output.95th-tile`

Once this two files are generated, information can be plotted using scripts/plot_energy.py and scripts/plot_times.py (filenames are hardcoded, they have to be manually changed in the scripts).