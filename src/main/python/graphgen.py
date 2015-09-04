from networkx import *
import sys


G = nx.fast_gnp_random_graph(10000,0.5)
# G = nx.powerlaw_cluster_graph(100,50,0.9)
# Gprime = nx.fast_gnp_random_graph(100,0.5)
degree_sequence=list(degree(G).values()) # degree sequence
print reduce(lambda x,y: str(x) + "\n" + str(y),degree_sequence)
# print "T: ", sum(nx.triangles(G).values())/3

# degseq = degree(G).values()
# cc = sum(nx.triangles(G).values())/float(sum(map(lambda x: x*(x-1)/2,degseq)))
# print cc
