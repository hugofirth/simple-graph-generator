from networkx import *
import sys




def print_props(G,id):
    global minCC, maxCC
    degree_sequence=list(degree(G).values())
    # print degree_sequence
    cc = sum(nx.triangles(G).values())/float(sum(map(lambda x: x*(x-1)/2,degree_sequence)))
    # print cc
    maxCC=max(maxCC,cc)
    minCC=min(minCC,cc)
    
    filename = "fast_gnp_random_graph_"+str(id)+".seq"
    file = open(filename,"w")
    file.write(reduce(lambda x,y: str(x) + "\n" + str(y),degree_sequence))
    file.close()
    
    props = [id,filename,"fast_gnp_random_graph",G.number_of_nodes(),G.number_of_edges(),cc ]
    print reduce(lambda x,y: str(x) + ", "+ str(y),props)
    
    
minCC = 1
maxCC = 0
id = 1
for n in range(10,1001,20):
   for i in range(0,50):
        # n = 10*2**v
        # print n,i
        G = nx.fast_gnp_random_graph(n,0.5)
        print_props(G,id)
        id += 1
       
# print minCC, maxCC
        

