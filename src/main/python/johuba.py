import networkx as nx
import sys
import matplotlib.pyplot as plt


def empty(seq):
    return reduce(lambda x,y: x and y == 0,seq,True)
    
def get_highest_index(seq):
    return seq.index(max(seq))      
    
def get_num_new_tri(g,idx,i):
    n1 = g.neighbors(idx)
    n2 = g.neighbors(i)
    return len(set(n1).intersection(set(n2)))


def johuba_graphgen(seq,cc):
    orig_seq = list(seq)
    n = len(seq)
    # print "Req. CC:", cc
    # print "Req. Seq:", seq
    G=nx.Graph()
    G.add_nodes_from(range(0,n))
    G.to_undirected()
    #print(G.nodes())
    #print(G.edges())

    # number of triangles
    seq2 = map(lambda x: int(x)*(int(x)-1)/2,seq)
    numtri = reduce(lambda x,y: x+y, seq2) * float(cc)/3
    # print "triangles needed:", numtri

    penalty = 0

    while not empty(seq):
        # get working node
        idx = get_highest_index(seq)
    
        best_idx = None
        best_tri = numtri + 1
        best_distance = n
    
        for i in range(0,n):
            # skip self-loops
            if i == idx or seq[i] == 0 or G.has_edge(i,idx):
                continue
        
            # is there a triangle formed
            new_tri = numtri - get_num_new_tri(G,idx,i)
        
            # determine if it is better
            distance = abs(idx-i)
            if 0 <= new_tri and ( 
                new_tri < best_tri or 
                (new_tri == best_tri and distance < best_distance)):
                best_idx = i
                best_tri = new_tri
                best_distance = distance
    
    
        if best_idx == None:
            seq[idx] = 0
            penalty += 1
        else:
            # add edge to best option
            # print get_num_new_tri(G,idx,best_idx), "new triangles formed"
            G.add_edge(idx,best_idx)
            G = G.to_undirected()
            seq[idx] -= 1
            seq[best_idx] -= 1
            numtri = best_tri
            # print "adding edge ", idx, best_idx
            # print "degrees", seq[idx],seq[best_idx]
            # print seq
    
    return G
        
        # s='\n'.join(nx.generate_graphml(G))
     #    f = open("jonny.graphml","w")
     #    f.write(s)
     #    f.close()
     #    raw_input("Press Jonny to continue")
    
    # print("---------")
    # # print(G.nodes())
    # # print(G.edges())
    # print "Missing triangles:", numtri
    # # print("CC: ")
    # # print("CC derivation: ")
    # # print("Degree derivation: ")
    # # print("Edge distance: ")
    # print("penalty:", penalty)
    # print("----------")
        
    # s='\n'.join(nx.generate_graphml(G))
    # print s
    # f = open("jonny.graphml","w")
    # f.write(s)
    # f.close()

