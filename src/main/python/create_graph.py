from networkx import *
import datetime as dt
import sys
from johuba import johuba_graphgen

id = sys.argv[1]
mode = sys.argv[2]
filename = sys.argv[3]
req_cc = float(sys.argv[4])

id = id.split("_")[-1].split(".")[0]

seq = []
with open(filename) as f:
    seq = f.readlines()

seq = map(lambda x: int(x), seq)

def create_buckets(seq):
    a = [0] * int(max(seq)+1)
    for d in seq:
        a[d] += 1
        
    return a
    
def reduce_bucket(a,G):
    # pad
    new_size = int(max(G.degree().values())+1)
    a = a + [0] * (new_size - len(a))
    
    # check off degrees
    for v in G.nodes():
        a[G.degree(v)] -= 1
        if a[G.degree(v)] < 0:
            a[G.degree(v)] = 0
    return a


def print_props(G, id, mode, req_cc, degree_sequence, time_ms, sat_n):
    cc = sum(nx.triangles(G).values())/float(sum(map(lambda x: x*(x-1)/2,degree_sequence)))
    
    props = [id, mode, req_cc, time_ms, sat_n, G.number_of_edges(), cc, 0]
    print reduce(lambda x,y: str(x) + ", "+ str(y),props)
    
    
# read sequence

n = len(seq)
m = sum(seq)/2

n1=dt.datetime.now()
if (mode == "preferential"):
    G = nx.barabasi_albert_graph(n,m/n)
elif (mode == "configmodel"):
    G = nx.configuration_model(seq)
    G = nx.Graph(G)
elif (mode == "powerlaw"):
    G = powerlaw_cluster_graph(n,m/n,req_cc)   
elif (mode == "johuba"):
    G = johuba_graphgen(seq,req_cc)    
    
n2=dt.datetime.now()

bucket = create_buckets(seq)
wrong_nodes = sum(map(lambda x: abs(x),reduce_bucket(bucket,G)))
print_props(G,id,mode,req_cc, G.degree().values(), (n2-n1).microseconds,n-wrong_nodes )