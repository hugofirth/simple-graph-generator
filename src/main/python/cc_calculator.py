import sys
import os.path
from networkx import *

infilename = sys.argv[1]
outfilename = sys.argv[2]

infile = open(infilename)
outfile = open(outfilename,"w")

lines = infile.readlines()


for line in lines:
    oldline = line
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

    if os.path.exists(filename):
        G = Graph(read_graphml(filename))
    
    
        # calculate cc
        degree_sequence=list(degree(G).values())
        cc = sum(nx.triangles(G).values())
        if cc > 0:
            cc = cc / float(sum(map(lambda x: x*(x-1)/2,degree_sequence)))
    
        # replace  $TODOCC
        oldline = oldline.replace("$TODOCC", str(cc))
    
    # write to output file
    outfile.write(oldline)
    
outfile.close()