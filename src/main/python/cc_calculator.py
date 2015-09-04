import sys
from networkx import *

infilename = sys.argv[1]
outfilename = sys.argv[2]

infile = open(infilename)
outfile = open(outfilename,"w")

lines = infile.readlines()

for line in lines:
    #get properties
    line = line.split(",")
    id = line[0].strip()
    strategy = line[1].strip()
    cc = line[2].strip()
    if cc == "0.0":
        cc = 0
    
    
    # read graphml
    folder = "output_"+strategy
    filename = folder+ "/graph_%s_%s.graphml"%(id,cc)
    G = Graph(read_graphml(filename))
    
    
    # calculate cc
    degree_sequence=list(degree(G).values())
    cc = sum(nx.triangles(G).values())/float(sum(map(lambda x: x*(x-1)/2,degree_sequence)))
    
    # replace  $TODOCC
    line.replace("$TODOCC", cc)
    
    # write to output file
    outfile.write(line)
    
outfile.close()