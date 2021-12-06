from pprint import pprint
from scipy.stats import chi2, chi2_contingency
from sys import argv
import numpy as np
import pandas as pd

file_name = argv[1]

if len(argv) > 2:
    alpha = float(argv[2])
else:
    alpha = 0.05

def partition(a):
    return {c: (a==c).nonzero()[0] for c in np.unique(a)}

def entropy(s):
    res = 0
    val, counts = np.unique(s, return_counts=True)
    freqs = counts.astype('float')/len(s)
    for p in freqs:
        if p != 0.0:
            res -= p * np.log2(p)
    return res

def information_gain(y, x):
    res = entropy(y)

    val, counts = np.unique(x, return_counts=True)
    freqs = counts.astype('float')/len(x)

    for p, v in zip(freqs, val):
        res -= p * entropy(y[x == v])

    return res

def is_pure(s):
    return len(set(s)) == 1

def recursive_split(x, y, attr_name):
    if is_pure(y) or len(y) == 0:
        return str(y[0])

    gain = np.array([information_gain(y, x_attr) for x_attr in x.T])
    selected_attr = np.argmax(gain)

    if np.all(gain < 1e-6):
        return y

    sets = partition(x[:, selected_attr])

    res = {}
    for k, v in sets.items():
        y_subset = y.take(v, axis=0)
        x_subset = x.take(v, axis=0)
        x_subset = np.delete(x_subset, selected_attr, 1)
        attr_name_subset = attr_name[:selected_attr] + attr_name[selected_attr+1:]
        #recurse on subset of data left
        res[attr_name[selected_attr] + " = " + k ] = recursive_split(x_subset, y_subset, attr_name_subset)

    return res

def pruneLeaves(obj):isLeaf = True
    parent = None
    for key in obj:
        if isinstance(obj[key], dict):
            isLeaf = False
            parent = key
            break
    if isLeaf and obj.keys()[0].split(' ')[0] not in satisfied_attributes:
        global pruned
        pruned = True
        return 'pruned'
    if not isLeaf:
        if pruneLeaves(obj[parent]):
            obj[parent] = None
    return obj

data = np.loadtxt(open(file_name, "rb"), delimiter=",", dtype='string', converters = {3: lambda s: s.strip()})

attr_name = data.take(0,0)[:-1].tolist()

y = data[...,-1][1:]

X = data[...,:-1]
X = np.delete(X,0,0)

tree = recursive_split(X, y, attr_name)

satisfied_attributes = []
for i in range(10):
    contengency = pd.crosstab(X.T[i], y)
    c, p, dof, expected = chi2_contingency(contengency)
    if c > chi2.isf(q=alpha, df=dof):
        satisfied_attributes.append(attr_name[i])

print ('\nDecision tree before pruning-\n')
pprint(tree)

print ('\nDecision tree after pruning-\n')
pruned = True
while pruned:
    pruned = False
    tree = pruneLeaves(tree)
    if tree == 'pruned':
        break
pprint(tree)